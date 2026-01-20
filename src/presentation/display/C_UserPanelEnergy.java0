package presentation.display;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import data.C_Parameters;

import java.awt.*;
import java.text.DecimalFormat;

public class C_UserPanelEnergy extends JPanel {

	static final long serialVersionUID = 1L;
	private final DecimalFormat df = new DecimalFormat("0.00");
	private final JSlider sChloro = vSlider(25, 200, 100); // 0.25×..2.00× (100 = 1.00×)
	private final JSlider sMicro = vSlider(25, 200, 100);
	private final JSlider sWhale = vSlider(25, 200, 100);
	private final JSpinner spChloro = spinner(1.00);
	private final JSpinner spMicro = spinner(1.00);
	private final JSpinner spWhale = spinner(1.00);

	// debounce pour le mode "Live"
	private final Timer debounce = new Timer(200, e -> applyToParameters());
	private boolean live = false;
	private boolean syncing = false;

	public C_UserPanelEnergy() {
		super(new BorderLayout(8, 8));

		// Titre
		JLabel title = new JLabel("Marine Energy Control", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		add(title, BorderLayout.NORTH);

		// Grid 3 colonnes
		JPanel grid = new JPanel(new GridLayout(1, 3, 12, 0));
		grid.add(column("Chlorophyll", sChloro, spChloro, () -> setSpinnerFromSlider(spChloro, sChloro)));
		grid.add(column("Micronekton", sMicro, spMicro, () -> setSpinnerFromSlider(spMicro, sMicro)));
		grid.add(column("Whale", sWhale, spWhale, () -> setSpinnerFromSlider(spWhale, sWhale)));
		add(grid, BorderLayout.CENTER);

		// Bas : boutons & Live
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
		actions.add(cbLive);
		actions.add(btnApply);
		actions.add(btnReset);
		add(actions, BorderLayout.SOUTH);

		// init: charge les valeurs actuelles des paramètres
		setAll(safe(C_Parameters.CHLOROPHYLL_MULTIPLIER), safe(C_Parameters.NEKTON_MULTIPLIER),
				safe(C_Parameters.PARTICLE_MULTIPLIER));

		// Timer de debounce n’exécute qu’une fois après inactivité
		debounce.setRepeats(false);
	}

	// -------- helpers UI --------

	private static JSlider vSlider(int min, int max, int val) {
		JSlider s = new JSlider(JSlider.VERTICAL, min, max, val);
		s.setPaintTicks(true);
		s.setPaintLabels(true);
		s.setMajorTickSpacing(25); // 0.25×
		s.setMinorTickSpacing(5); // 0.05×
		return s;
	}

	private static JSpinner spinner(double init) {
		JSpinner sp = new JSpinner(new SpinnerNumberModel(init, 0.25, 2.0, 0.01));
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
		for (double v : new double[] { 0.5, 1.0, 2.0 }) {
			JButton b = new JButton(df.format(v) + "×");
			b.setMargin(new Insets(2, 6, 2, 6));
			b.addActionListener(e -> {
				setFromDouble(spinner, slider, v);
				valLbl.setText(df.format(slider.getValue() / 100.0) + "×");

			});
			presets.add(b);
		}

		// Sync slider -> spinner (et MAJ étiquette). Application: à la fin du drag.
		slider.addChangeListener((ChangeEvent e) -> {
			if (syncing)
				return;
			syncing = true;
			sliderToSpinner.run();
			valLbl.setText(df.format(slider.getValue() / 100.0) + "×");
			// application
			if (live) {
				debounce.restart(); // debounced en mode Live
			} else if (!slider.getValueIsAdjusting()) {
				applyToParameters(); // à la fin du drag seulement
			}
			syncing = false;
		});

		// Sync spinner -> slider (applique direct si Live sinon on attend clic
		// "Appliquer")
		spinner.addChangeListener(e -> {
			if (syncing)
				return;
			syncing = true;
			double v = ((Number) spinner.getValue()).doubleValue();
			slider.setValue((int) Math.round(v * 100));
			valLbl.setText(df.format(v) + "×");
			if (live)
				debounce.restart();
			syncing = false;
		});

		JPanel col = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(4, 6, 4, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridy = 0;
		c.weighty = 0;
		col.add(lbl, c);
		c.gridy = 1;
		c.weighty = 0;
		col.add(valLbl, c);
		c.gridy = 2;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		col.add(slider, c);
		c.gridy = 3;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		col.add(spinner, c);
		c.gridy = 4;
		col.add(presets, c);

		return col;
	}

	private void setFromDouble(JSpinner sp, JSlider sl, double v) {
		syncing = true;
		sp.setValue(v);
		sl.setValue((int) Math.round(v * 100));
		syncing = false;
	}

	private void setSpinnerFromSlider(JSpinner sp, JSlider sl) {
		sp.setValue(sl.getValue() / 100.0);
	}

	private void setAll(double chl, double mic, double wh) {
		setFromDouble(spChloro, sChloro, chl);
		setFromDouble(spMicro, sMicro, mic);
		setFromDouble(spWhale, sWhale, wh);
	}

	private static double safe(double v) {
		if (Double.isNaN(v) || Double.isInfinite(v) || v < 0.25 || v > 2.0)
			return 1.0;
		return v;
	}

	// -------- application modèle (légère, pas bloquante) --------

	private void applyToParameters() {
		// ⚠️ ne fais ici que des affectations légères ; si tu dois recalculer des trucs
		// lourds,
		// déclenche-les côté scheduler du modèle, pas sur l’EDT (sinon UI gelée).
		C_Parameters.CHLOROPHYLL_MULTIPLIER = ((Number) spChloro.getValue()).doubleValue();
		C_Parameters.NEKTON_MULTIPLIER = ((Number) spMicro.getValue()).doubleValue();
		C_Parameters.PARTICLE_MULTIPLIER = ((Number) spWhale.getValue()).doubleValue();

		// Log (debug)
		// System.out.println("Applied: chl=" + C_Parameters.CHLOROPHYLL_MULTIPLIER
		// + " micro=" + C_Parameters.MICRONEKTON_MULTIPLIER
		// + " whale=" + C_Parameters.WHALE_MULTIPLIER);
	}
}
