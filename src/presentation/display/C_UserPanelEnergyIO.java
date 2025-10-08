package presentation.display;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import data.C_Parameters;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Locale;

public class C_UserPanelEnergyIO extends JPanel {

  // --- Plage linéaire ---
  private static final double MIN = 0.25;
  private static final double MAX = 2.00;

  // --- Fichier CSV (à la racine du run ; mettre un dossier si tu préfères) ---
  private static final File CSV_FILE = new File("energy_scales.csv");

  private final DecimalFormat df = new DecimalFormat("0.00");

  // Sliders verticaux (valeur *100 pour rester entiers)
  private final JSlider sChloro = vSlider((int)Math.round(MIN*100), (int)Math.round(MAX*100), 100);
  private final JSlider sMicro  = vSlider((int)Math.round(MIN*100), (int)Math.round(MAX*100), 100);
  private final JSlider sWhale  = vSlider((int)Math.round(MIN*100), (int)Math.round(MAX*100), 100);

  // Champs numériques
  private final JSpinner spChloro = spinner(1.00);
  private final JSpinner spMicro  = spinner(1.00);
  private final JSpinner spWhale  = spinner(1.00);

  // Dé-bounce pour le mode “Live”
  private final Timer debounce = new Timer(200, e -> applyToParametersAndSave());

  private boolean live = false;
  private boolean syncing = false;

  public C_UserPanelEnergyIO() {
    super(new BorderLayout(8, 8));

    JLabel title = new JLabel("Marine Energy Control (0.25× → 2.00×)", SwingConstants.CENTER);
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
    btnApply.addActionListener(e -> applyToParametersAndSave());

    JButton btnReset = new JButton("Reset 1.00×");
    btnReset.addActionListener(e -> {
      setAll(1.0, 1.0, 1.0);
      applyToParametersAndSave();
    });

    JButton btnLoad = new JButton("Recharger CSV");
    btnLoad.addActionListener(e -> {
      loadCSV();        // recharge UI
      applyToParameters(); // applique sans re-sauver
    });

    actions.add(cbLive);
    actions.add(btnApply);
    actions.add(btnReset);
    actions.add(btnLoad);
    add(actions, BorderLayout.SOUTH);

    // Dé-bounce: une seule application 200 ms après la dernière modif
    debounce.setRepeats(false);

    // Init : recharge depuis CSV si présent, sinon depuis C_Parameters
    if (!loadCSV()) {
      setAll(safe(C_Parameters.CHLOROPHYLL_MULTIPLIER),
             safe(C_Parameters.MICRONEKTON_MULTIPLIER),
             safe(C_Parameters.ENERGY_MULTIPLIER_PLANKTON));
      // et on sauvegarde une première fois
      saveCSV();
    }
  }

  // ---------- UI helpers ----------

  private static JSlider vSlider(int min, int max, int val) {
    JSlider s = new JSlider(JSlider.VERTICAL, min, max, val);
    s.setPaintTicks(true);
    s.setPaintLabels(true);
    s.setMajorTickSpacing(25); // 0.25×
    s.setMinorTickSpacing(5);  // 0.05×
    // labels principaux (0.25 / 0.5 / 1.0 / 1.5 / 2.0)
    s.setLabelTable(createLinearLabelTable(min, max));
    return s;
  }

  private static java.util.Dictionary<Integer, JLabel> createLinearLabelTable(int min, int max) {
    java.util.Hashtable<Integer, JLabel> table = new java.util.Hashtable<>();
    double[] ticks = new double[]{0.25, 0.50, 1.00, 1.50, 2.00};
    for (double v : ticks) {
      int pos = (int)Math.round(v*100);
      table.put(pos, new JLabel((v == Math.rint(v)) ? String.valueOf((int)v) : String.format(Locale.ROOT,"%.2f", v)));
    }
    return table;
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

    // Presets
    JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
    for (double v : new double[]{0.5, 1.0, 2.0}) {
      JButton b = new JButton(df.format(v) + "×");
      b.setMargin(new Insets(2, 6, 2, 6));
      b.addActionListener(e -> {
        setFromDouble(spinner, slider, v);
        valLbl.setText(df.format(v) + "×");
        if (live) debounce.restart();
      });
      presets.add(b);
    }

    // slider -> spinner (+ apply à fin de drag)
    slider.addChangeListener((ChangeEvent e) -> {
      if (syncing) return;
      syncing = true;
      sliderToSpinner.run();
      double val = slider.getValue() / 100.0;
      valLbl.setText(df.format(val) + "×");
      if (live) {
        debounce.restart();
      } else if (!slider.getValueIsAdjusting()) {
        applyToParametersAndSave();
      }
      syncing = false;
    });

    // spinner -> slider (+ apply seulement si Live)
    spinner.addChangeListener(e -> {
      if (syncing) return;
      syncing = true;
      double v = ((Number) spinner.getValue()).doubleValue();
      slider.setValue((int)Math.round(v * 100));
      valLbl.setText(df.format(v) + "×");
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
    sl.setValue((int)Math.round(v * 100));
    syncing = false;
  }

  private void spinnerFromSlider(JSpinner sp, JSlider sl) {
    sp.setValue(sl.getValue() / 100.0);
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

  // ---------- Application & CSV ----------

  private void applyToParameters() {
    C_Parameters.CHLOROPHYLL_MULTIPLIER = ((Number) spChloro.getValue()).doubleValue();
    C_Parameters.MICRONEKTON_MULTIPLIER = ((Number) spMicro.getValue()).doubleValue();
    C_Parameters.ENERGY_MULTIPLIER_PLANKTON       = ((Number) spWhale.getValue()).doubleValue();
  }

  private void applyToParametersAndSave() {
    applyToParameters();
    saveCSV();
  }

  private boolean loadCSV() {
    if (!CSV_FILE.exists()) return false;
    double chl = 1.0, mic = 1.0, wh = 1.0;

    try (BufferedReader r = Files.newBufferedReader(CSV_FILE.toPath(), StandardCharsets.UTF_8)) {
      String line; boolean first = true;
      while ((line = r.readLine()) != null) {
        if (first) { first = false; continue; } // skip header
        String[] parts = line.split(",");
        if (parts.length >= 2) {
          String key = parts[0].trim();
          double val = parseSafe(parts[1].trim(), 1.0);
          switch (key) {
            case "chl" -> chl = val;
            case "mic" -> mic = val;
            case "wh"  -> wh  = val;
          }
        }
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return false;
    }
    setAll(safe(chl), safe(mic), safe(wh));
    return true;
  }

  private void saveCSV() {
    try (BufferedWriter w = Files.newBufferedWriter(CSV_FILE.toPath(), StandardCharsets.UTF_8)) {
      w.write("name,value\n");
      w.write(String.format(Locale.ROOT, "chl,%.6f%n", ((Number) spChloro.getValue()).doubleValue()));
      w.write(String.format(Locale.ROOT, "mic,%.6f%n", ((Number) spMicro.getValue()).doubleValue()));
      w.write(String.format(Locale.ROOT, "wh,%.6f%n",  ((Number) spWhale.getValue()).doubleValue()));
    } catch (IOException ioe) {
      ioe.printStackTrace();
      JOptionPane.showMessageDialog(this, "Erreur de sauvegarde CSV: " + ioe.getMessage(),
              "Erreur", JOptionPane.ERROR_MESSAGE);
    }
  }

  private static double parseSafe(String s, double def) {
    try { return Double.parseDouble(s); } catch (Exception e) { return def; }
  }
}
