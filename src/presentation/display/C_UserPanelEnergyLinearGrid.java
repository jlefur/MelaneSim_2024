package presentation.display;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
//import java.util.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import data.C_Parameters;
import data.constants.rodents.I_ConstantImagesNames;

public class C_UserPanelEnergyLinearGrid extends JPanel implements I_ConstantImagesNames {

	private static final long serialVersionUID = 1L;
	// ---- Réglages linéaires ----
	private static final double MIN = 0.25, MAX = 2.00;
	private static final File CSV_FILE = new File("energy_scales.csv");
	private static final int ROWS = 3, COLS = 5; // <-- change ici si tu veux une autre grille

	private final DecimalFormat df = new DecimalFormat("0.00");
	private final Timer debounce = new Timer(200, e -> applyToParametersAndSave());
	private boolean live = false;
	private boolean syncing = false;

	private static ImageIcon loadIconFromFile(String imageName, int size) {
		try {
			// Répertoire racine de ton projet
			String basePath = System.getProperty("user.dir") + File.separator + "icons" + File.separator + "sliders"
					+ File.separator;
			File file = new File(basePath + imageName + ".png");

			if (!file.exists()) {
				System.err.println("⚠️  Icône non trouvée : " + file.getAbsolutePath());
				return null;
			}

			Image img = javax.imageio.ImageIO.read(file);
			if (img != null && size > 0) {
				img = img.getScaledInstance(size, size, Image.SCALE_SMOOTH);
			}
			return new ImageIcon(img);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Un "canal" = 1 slider + 1 spinner + liaison getter/setter vers C_Parameters
	private static class Channel {
		final String id; // clé CSV: ex "chl"
		final String label; // titre visible: ex "Chlorophyll"
		final String icon;
		final DoubleSupplier get; // lit C_Parameters
		final DoubleConsumer set; // écrit C_Parameters
		JSlider slider;
		JSpinner spinner;
		JLabel valLabel;

		Channel(String id, String label, String icon, DoubleSupplier get, DoubleConsumer set) {
			this.id = id;
			this.label = label;
			this.icon = icon;
			this.get = get;
			this.set = set;
		}
	}

	// ---- Déclare ici tes 9 canaux (exemples) ----
	private final List<Channel> channels = Arrays.asList(//
			new Channel("par", "particle", PARTICLE_ICON, () -> C_Parameters.PARTICLE_MULTIPLIER,					v -> C_Parameters.PARTICLE_MULTIPLIER = v), //
			new Channel("mou", "mount", MOUNT_ICON, () -> C_Parameters.MOUNT_MULTIPLIER,					v -> C_Parameters.MOUNT_MULTIPLIER = v), //
			new Channel("tem", "temperature", TEMPERATURE_ICON, () -> C_Parameters.TEMPERATURE_MULTIPLIER,					v -> C_Parameters.TEMPERATURE_MULTIPLIER = v), //
			new Channel("chl", "chlorophyll", CHLOROPHYLL_ICON, () -> C_Parameters.CHLOROPHYLL_MULTIPLIER,					v -> C_Parameters.CHLOROPHYLL_MULTIPLIER = v), //
			new Channel("nec", "necton", NEKTON_ICON, () -> C_Parameters.NEKTON_MULTIPLIER,					v -> C_Parameters.NEKTON_MULTIPLIER = v), //
			new Channel("tun", "tuna", TUNA_ICON, () -> C_Parameters.TUNA_MULTIPLIER,					v -> C_Parameters.TUNA_MULTIPLIER = v), //
			new Channel("wha", "whale", WHALE_ICON, () -> C_Parameters.WHALE_MULTIPLIER,					v -> C_Parameters.WHALE_MULTIPLIER = v), //
			new Channel("sha", "shark", SHARK_ICON, () -> C_Parameters.SHARK_MULTIPLIER,					v -> C_Parameters.SHARK_MULTIPLIER = v), //
			new Channel("bir", "bird", BIRD_ICON, () -> C_Parameters.BIRD_MULTIPLIER,					v -> C_Parameters.BIRD_MULTIPLIER = v), //
			new Channel("cor", "coral", TURTLE_ICON, () -> C_Parameters.TURTLE_MULTIPLIER,					v -> C_Parameters.TURTLE_MULTIPLIER = v),
			new Channel("fis", "fisher", FISHER_ICON, () -> C_Parameters.FISHER_MULTIPLIER,					v -> C_Parameters.FISHER_MULTIPLIER = v), //
			new Channel("shi", "ship", SHIP_ICON, () -> C_Parameters.SHIP_MULTIPLIER,					v -> C_Parameters.SHIP_MULTIPLIER = v), //
			new Channel("tou", "tourism", TOURISM_ICON, () -> C_Parameters.TOURISM_MULTIPLIER,					v -> C_Parameters.TOURISM_MULTIPLIER = v), //
			new Channel("pol", "policeControl", POLICE_ICON, () -> C_Parameters.POLICE_MULTIPLIER,					v -> C_Parameters.POLICE_MULTIPLIER = v), //
			new Channel("sew", "sewage", POLLUTION_ICON, () -> C_Parameters.POLLUTION_MULTIPLIER,					v -> C_Parameters.POLLUTION_MULTIPLIER = v)); //

	public C_UserPanelEnergyLinearGrid() {
		super(new BorderLayout(8, 8));

		JLabel title = new JLabel("Marine Energy Control (0.25× → 2.00×)", SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
		add(title, BorderLayout.NORTH);

		// --- Grille ROWS x COLS ---
		JPanel grid = new JPanel(new GridLayout(ROWS, COLS, 12, 12));
		for (Channel ch : channels)
			grid.add(buildColumn(ch));
		add(new JScrollPane(grid, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		// --- Barre d'actions ---
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JCheckBox cbLive = new JCheckBox("Appliquer en direct (Live)");
		cbLive.addActionListener(e -> live = cbLive.isSelected());
		JButton btnApply = new JButton("Appliquer maintenant");
		btnApply.addActionListener(e -> applyToParametersAndSave());
		JButton btnReset = new JButton("Reset 1.00×");
		btnReset.addActionListener(e -> {
			setAllTo(1.0);
			applyToParametersAndSave();
		});
		JButton btnLoad = new JButton("Recharger CSV");
		btnLoad.addActionListener(e -> {
			loadCSV();
			applyToParameters();
		});
		actions.add(cbLive);
		actions.add(btnApply);
		actions.add(btnReset);
		actions.add(btnLoad);
		add(actions, BorderLayout.SOUTH);

		debounce.setRepeats(false);

		// --- Init: recharge CSV si dispo, sinon depuis C_Parameters ---
		if (!loadCSV()) {
			// si pas de fichier, on initialise depuis les getters
			for (Channel ch : channels) {
				double v = safe(ch.get.getAsDouble());
				setUI(ch, v);
			}
			saveCSV(); // crée un CSV dès le départ
		}
	}

	// ---------- Construction d'une "colonne" (vertical) ----------
	private JPanel buildColumn(Channel ch) {
		ch.slider = vSlider((int) Math.round(MIN * 100), (int) Math.round(MAX * 100), 100);
		ch.spinner = spinner(1.00);
		ch.valLabel = new JLabel("1.00×", SwingConstants.CENTER);
		ch.valLabel.setFont(ch.valLabel.getFont().deriveFont(Font.BOLD, 12f));

		// label avec icône + texte centré
		ImageIcon imageIcon = loadIconFromFile(ch.icon, 92); // si ton nom d’image correspond à ch.id (ex. "wh", "mic", etc.)
		JLabel lbl = new JLabel(ch.label, imageIcon, SwingConstants.CENTER);
		lbl.setVerticalTextPosition(SwingConstants.BOTTOM); // texte sous l’image
		lbl.setHorizontalTextPosition(SwingConstants.CENTER);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
		// Si tu veux le texte à droite de l’icône
		lbl.setVerticalTextPosition(SwingConstants.CENTER);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);

		// presets
		JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
		for (double v : new double[]{0.5, 1.0, 2.0}) {
			JButton b = new JButton(df.format(v) + "×");
			b.setMargin(new Insets(2, 6, 2, 6));
			b.addActionListener(e -> {
				setUI(ch, v);
				if (live) debounce.restart();
			});
			presets.add(b);
		}

		// slider -> spinner (+ apply à fin de drag)
		ch.slider.addChangeListener((ChangeEvent e) -> {
			if (syncing) return;
			syncing = true;
			double val = ch.slider.getValue() / 100.0;
			ch.spinner.setValue(val);
			ch.valLabel.setText(df.format(val) + "×");
			if (live) {
				debounce.restart();
			}
			else if (!ch.slider.getValueIsAdjusting()) {
				applyToParametersAndSave();
			}
			syncing = false;
		});

		// spinner -> slider (+ apply si Live)
		ch.spinner.addChangeListener(e -> {
			if (syncing) return;
			syncing = true;
			double v = ((Number) ch.spinner.getValue()).doubleValue();
			ch.slider.setValue((int) Math.round(v * 100));
			ch.valLabel.setText(df.format(v) + "×");
			if (live) debounce.restart();
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
		col.add(ch.valLabel, c);
		c.gridy = 2;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		col.add(ch.slider, c);
		c.gridy = 3;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		col.add(ch.spinner, c);
		c.gridy = 4;
		col.add(presets, c);

		// valeur initiale depuis getter
		setUI(ch, safe(ch.get.getAsDouble()));
		return col;
	}

	private static JSlider vSlider(int min, int max, int val) {
		JSlider s = new JSlider(JSlider.VERTICAL, min, max, val);
		s.setPaintTicks(true);
		s.setPaintLabels(true);
		s.setMajorTickSpacing(25);
		s.setMinorTickSpacing(5);
		s.setLabelTable(createLinearLabelTable(min, max));
		return s;
	}

	private static java.util.Dictionary<Integer, JLabel> createLinearLabelTable(int min, int max) {
		java.util.Hashtable<Integer, JLabel> table = new java.util.Hashtable<>();
		double[] ticks = new double[]{0.25, 0.50, 1.00, 1.50, 2.00};
		for (double v : ticks) {
			int pos = (int) Math.round(v * 100);
			table.put(pos, new JLabel((v == Math.rint(v))
					? String.valueOf((int) v)
					: String.format(Locale.ROOT, "%.2f", v)));
		}
		return table;
	}

	private static JSpinner spinner(double init) {
		JSpinner sp = new JSpinner(new SpinnerNumberModel(init, MIN, MAX, 0.01));
		((JSpinner.NumberEditor) sp.getEditor()).getFormat().applyPattern("0.00");
		sp.setPreferredSize(new Dimension(70, 24));
		return sp;
	}

	// ---------- Helpers de synchro UI ----------
	private void setUI(Channel ch, double v) {
		v = safe(v);
		syncing = true;
		ch.spinner.setValue(v);
		ch.slider.setValue((int) Math.round(v * 100));
		ch.valLabel.setText(df.format(v) + "×");
		syncing = false;
	}

	private void setAllTo(double v) {
		for (Channel ch : channels)
			setUI(ch, v);
	}

	private static double safe(double v) {
		if (Double.isNaN(v) || Double.isInfinite(v) || v < MIN || v > MAX) return 1.0;
		return v;
	}

	// ---------- Application & CSV générique ----------
	private void applyToParameters() {
		for (Channel ch : channels) {
			double v = ((Number) ch.spinner.getValue()).doubleValue();
			ch.set.accept(v); // écrit dans C_Parameters
		}
	}

	private void applyToParametersAndSave() {
		applyToParameters();
		saveCSV();
	}

	private boolean loadCSV() {
		if (!CSV_FILE.exists()) return false;
		Map<String, Double> map = new LinkedHashMap<>();
		try (BufferedReader r = Files.newBufferedReader(CSV_FILE.toPath(), StandardCharsets.UTF_8)) {
			String line;
			boolean first = true;
			while ((line = r.readLine()) != null) {
				if (first) {
					first = false;
					continue;
				}
				String[] parts = line.split(",");
				if (parts.length >= 2) {
					String key = parts[0].trim();
					double val = parseSafe(parts[1].trim(), 1.0);
					map.put(key, val);
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return false;
		}
		// applique aux canaux connus (ignore les clés inconnues)
		for (Channel ch : channels) {
			double v = map.getOrDefault(ch.id, safe(ch.get.getAsDouble()));
			setUI(ch, safe(v));
		}
		return true;
	}

	private void saveCSV() {
		try (BufferedWriter w = Files.newBufferedWriter(CSV_FILE.toPath(), StandardCharsets.UTF_8)) {
			w.write("name,value\n");
			for (Channel ch : channels) {
				double v = ((Number) ch.spinner.getValue()).doubleValue();
				w.write(String.format(Locale.ROOT, "%s,%.6f%n", ch.id, v));
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erreur de sauvegarde CSV: " + ioe.getMessage(), "Erreur",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private static double parseSafe(String s, double def) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return def;
		}
	}
}
