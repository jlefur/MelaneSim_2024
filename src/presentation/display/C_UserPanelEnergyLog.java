package presentation.display;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import data.C_Parameters;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Properties;

public class C_UserPanelEnergyLog extends JPanel {

  // ---- Réglages de l’échelle log ----
  private static final double MIN = 0.1;   // 0.1×
  private static final double MAX = 10.0;  // 10×
  private static final int SL_MIN = 0;
  private static final int SL_MAX = 1000;  // résolution fine
  private static final String STORE = "energy_scales.properties";

  private final DecimalFormat df = new DecimalFormat("0.00");

  private final JSlider sChloro = vSlider();
  private final JSlider sMicro  = vSlider();
  private final JSlider sWhale  = vSlider();

  private final JSpinner spChloro = spinner(1.00);
  private final JSpinner spMicro  = spinner(1.00);
  private final JSpinner spWhale  = spinner(1.00);

  private final Timer debounce = new Timer(200, e -> applyToParameters());
  private boolean live = false;
  private boolean syncing = false;

  public C_UserPanelEnergyLog() {
    super(new BorderLayout(8, 8));

    JLabel title = new JLabel("Marine Energy Control (Log 0.1× → 10×)", SwingConstants.CENTER);
    title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
    add(title, BorderLayout.NORTH);

    JPanel grid = new JPanel(new GridLayout(1, 3, 12, 0));
    grid.add(column("Chlorophyll", sChloro, spChloro, () -> spinnerFromSlider(spChloro, sChloro)));
    grid.add(column("Micronekton", sMicro,  spMicro,  () -> spinnerFromSlider(spMicro,  sMicro)));
    grid.add(column("Whale",       sWhale,  spWhale,  () -> spinnerFromSlider(spWhale,  sWhale)));
    add(grid, BorderLayout.CENTER);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JCheckBox cbLive = new JCheckBox("Appliquer en direct (Live)");
    cbLive.addActionListener(e -> live = cbLive.isSelected());
    JButton btnApply = new JButton("Appliquer maintenant");
    btnApply.addActionListener(e -> applyToParameters());
    JButton btnReset = new JButton("Reset 1.00×");
    btnReset.addActionListener(e -> {
      setAll(1.0, 1.0, 1.0);
      applyToParameters();
    });
    JButton btnSave = new JButton("Sauvegarder");
    btnSave.addActionListener(e -> saveToFile());
    JButton btnLoad = new JButton("Recharger");
    btnLoad.addActionListener(e -> {
      loadFromFile();
      applyToParameters();
    });
    actions.add(cbLive); actions.add(btnApply); actions.add(btnReset);
    actions.add(btnSave); actions.add(btnLoad);
    add(actions, BorderLayout.SOUTH);

    // init : charge paramètres actuels (ou 1.0 s’ils sont hors bornes)
    setAll(safe(C_Parameters.CHLOROPHYLL_MULTIPLIER),
           safe(C_Parameters.MICRONEKTON_MULTIPLIER),
           safe(C_Parameters.ENERGY_MULTIPLIER_PLANKTON));

    debounce.setRepeats(false);
  }

  // ---------- UI helpers ----------

  private static JSlider vSlider() {
    JSlider s = new JSlider(JSlider.VERTICAL, SL_MIN, SL_MAX, valToSlider(1.0));
    s.setPaintTicks(true);
    s.setPaintLabels(true);
    // repères log : 0.1, 0.2, 0.5, 1, 2, 5, 10 (approx via positions)
    // (facultatif : pour des labels plus jolis, on pourrait créer une Hashtable<Integer,JLabel>)
    s.setMajorTickSpacing(200);
    s.setMinorTickSpacing(50);
    return s;
  }

  private static JSpinner spinner(double init) {
    JSpinner sp = new JSpinner(new SpinnerNumberModel(init, MIN, MAX, 0.01));
    ((JSpinner.NumberEditor) sp.getEditor()).getFormat().applyPattern("0.00");
    sp.setPreferredSize(new Dimension(70, 24));
    return sp;
  }

  private JPanel column(String name, JSlider slider, JSpinner spinner, Runnable sliderToSpinner) {
    JLabel lbl = new JLabel(name, SwingConstants.CENTER);
    lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));

    JLabel valLbl = new JLabel("1.00×", SwingConstants.CENTER);
    valLbl.setFont(valLbl.getFont().deriveFont(Font.BOLD, 12f));

    JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
    for (double v : new double[]{0.2, 0.5, 1.0, 2.0, 5.0}) {
      JButton b = new JButton(fmt(v) + "×");
      b.setMargin(new Insets(2, 6, 2, 6));
      b.addActionListener(e -> {
        setFromDouble(spinner, slider, v);
        valLbl.setText(fmt(v) + "×");
        if (live) debounce.restart();
      });
      presets.add(b);
    }

    slider.addChangeListener((ChangeEvent e) -> {
      if (syncing) return;
      syncing = true;
      sliderToSpinner.run();
      double val = sliderToVal(slider.getValue());
      valLbl.setText(fmt(val) + "×");
      if (live) {
        debounce.restart();
      } else if (!slider.getValueIsAdjusting()) {
        applyToParameters();
      }
      syncing = false;
    });

    spinner.addChangeListener(e -> {
      if (syncing) return;
      syncing = true;
      double v = ((Number) spinner.getValue()).doubleValue();
      slider.setValue(valToSlider(v));
      valLbl.setText(fmt(v) + "×");
      if (live) debounce.restart();
      syncing = false;
    });

    JPanel col = new JPanel(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0; c.weightx = 1; c.insets = new Insets(4,6,4,6); c.fill = GridBagConstraints.HORIZONTAL;

    c.gridy = 0; c.weighty = 0; col.add(lbl, c);
    c.gridy = 1; c.weighty = 0; col.add(valLbl, c);
    c.gridy = 2; c.weighty = 1; c.fill = GridBagConstraints.BOTH; col.add(slider, c);
    c.gridy = 3; c.weighty = 0; c.fill = GridBagConstraints.HORIZONTAL; col.add(spinner, c);
    c.gridy = 4; col.add(presets, c);

    return col;
  }

  private void setFromDouble(JSpinner sp, JSlider sl, double v) {
    syncing = true;
    sp.setValue(v);
    sl.setValue(valToSlider(v));
    syncing = false;
  }

  private void spinnerFromSlider(JSpinner sp, JSlider sl) {
    sp.setValue(sliderToVal(sl.getValue()));
  }

  private void setAll(double chl, double mic, double wh) {
    setFromDouble(spChloro, sChloro, chl);
    setFromDouble(spMicro,  sMicro,  mic);
    setFromDouble(spWhale,  sWhale,  wh);
  }

  private static double safe(double v) {
    if (Double.isNaN(v) || Double.isInfinite(v) || v < MIN || v > MAX) return 1.0;
    return v;
  }

  private static String fmt(double v) {
    // formatting smart: 0.10, 0.20, 0.50, 1.00, 2.00, 5.00, 10.00
    if (v >= 10) return "10.00";
    if (v < 1 && v >= 0.1) return String.format("%.2f", v);
    return String.format("%.2f", v);
  }

  // ---------- mapping linéaire (0..1000) ↔ log10(MIN..MAX) ----------

  private static int valToSlider(double val) {
    double t = (Math.log10(val) - Math.log10(MIN)) / (Math.log10(MAX) - Math.log10(MIN));
    int pos = (int)Math.round(SL_MIN + t * (SL_MAX - SL_MIN));
    if (pos < SL_MIN) pos = SL_MIN;
    if (pos > SL_MAX) pos = SL_MAX;
    return pos;
  }

  private static double sliderToVal(int sliderPos) {
    double t = (sliderPos - SL_MIN) / (double)(SL_MAX - SL_MIN);
    double logV = Math.log10(MIN) + t * (Math.log10(MAX) - Math.log10(MIN));
    double val = Math.pow(10, logV);
    if (val < MIN) val = MIN;
    if (val > MAX) val = MAX;
    return val;
  }

  // ---------- application modèle & I/O ----------

  private void applyToParameters() {
    C_Parameters.CHLOROPHYLL_MULTIPLIER = ((Number) spChloro.getValue()).doubleValue();
    C_Parameters.MICRONEKTON_MULTIPLIER = ((Number) spMicro.getValue()).doubleValue();
    C_Parameters.ENERGY_MULTIPLIER_PLANKTON       = ((Number) spWhale.getValue()).doubleValue();
  }

  private void saveToFile() {
    Properties p = new Properties();
    p.setProperty("chl", String.valueOf(((Number) spChloro.getValue()).doubleValue()));
    p.setProperty("mic", String.valueOf(((Number) spMicro.getValue()).doubleValue()));
    p.setProperty("wh",  String.valueOf(((Number) spWhale.getValue()).doubleValue()));
    try (FileOutputStream fos = new FileOutputStream(STORE)) {
      p.store(fos, "Energy scale multipliers");
      JOptionPane.showMessageDialog(this, "Sauvegardé dans " + STORE, "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Erreur de sauvegarde: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void loadFromFile() {
    Properties p = new Properties();
    try (FileInputStream fis = new FileInputStream(STORE)) {
      p.load(fis);
      double chl = safe(Double.parseDouble(p.getProperty("chl", "1.0")));
      double mic = safe(Double.parseDouble(p.getProperty("mic", "1.0")));
      double wh  = safe(Double.parseDouble(p.getProperty("wh",  "1.0")));
      setAll(chl, mic, wh);
      JOptionPane.showMessageDialog(this, "Rechargé depuis " + STORE, "OK", JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(this, "Impossible de lire " + STORE + " : " + ex.getMessage(),
              "Info", JOptionPane.WARNING_MESSAGE);
    }
  }
}
