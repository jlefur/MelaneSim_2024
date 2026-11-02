package presentation.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;

import data.C_Parameters;
import data.constants.rodents.I_ConstantImagesNames;
import melanesim.protocol.A_Protocol;
import thing.I_MarineActor.DriverType;

public class C_UserPanelEnergyLinearGrid extends JPanel implements I_ConstantImagesNames {

	private static final long serialVersionUID = 1L;
	// ---- Réglages linéaires ----
	private static final double MIN = 0.25, MAX = 10.00;
	private static final File CSV_FILE = new File("energy_scales.csv");
	private static final int ROWS = 3, COLS = 5; // <-- change ici si tu veux une autre grille

	private final DecimalFormat df = new DecimalFormat("0.00");
	private final Timer debounce = new Timer(200,e->applyToParametersAndSave());
	private boolean live = true;
	private boolean syncing = false;
	/** Couleurs des curseurs de la version horizontale (une par canal) */
	private static final Color[] SLIDER_COLORS = {new Color(61,175,85),new Color(59,142,206),new Color(103,88,230),
	        new Color(241,79,107),new Color(240,150,40)};
	/** Largeur (px) des sliders horizontaux : préférée / minimale */
	private static final int SLIDER_WIDTH = 220, SLIDER_MIN_WIDTH = 120;
	// Pour imprimer le changement de valeur
	private final java.util.Map<String,Double> lastApplied = new java.util.HashMap<>();
	private static final double EPS = 1e-6;

	private static ImageIcon loadIconFromFile(String imageName, int size) {
		try{
			// Répertoire racine de ton projet
			String basePath = System.getProperty("user.dir")+File.separator+"icons"+File.separator+"sliders"
			        +File.separator;
			File file = new File(basePath+imageName+".png");
			if(!file.exists()){
				System.err.println("⚠️  Icône non trouvée : "+file.getAbsolutePath());
				return null;
			}
			Image img = javax.imageio.ImageIO.read(file);
			if(img!=null&&size>0) img = img.getScaledInstance(size,size,Image.SCALE_SMOOTH);
			return new ImageIcon(img);

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/** Panneau qui prend la largeur du JScrollPane au lieu de sa largeur préférée : rien n'est coupé à droite */
	private static class WidthTrackingPanel extends JPanel implements Scrollable {
		private static final long serialVersionUID = 1L;

		WidthTrackingPanel(java.awt.LayoutManager layout) { super(layout); }

		@Override
		public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }

		@Override
		public int getScrollableUnitIncrement(java.awt.Rectangle r, int orientation, int direction) { return 16; }

		@Override
		public int getScrollableBlockIncrement(java.awt.Rectangle r, int orientation, int direction) {
			return orientation==SwingConstants.VERTICAL?r.height:r.width;
		}

		@Override
		public boolean getScrollableTracksViewportWidth() { return true; }

		@Override
		public boolean getScrollableTracksViewportHeight() { return false; }
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

		Channel(String id,String label,String icon,DoubleSupplier get,DoubleConsumer set) {
			this.id = id;
			this.label = label;
			this.icon = icon;
			this.get = get;
			this.set = set;
		}
	}

	// ---- Déclare ici tes 9 canaux (exemples) ----
	private final List<Channel> channels = Arrays.asList(//
	        new Channel("par","particle",PARTICLE_ICON,()->C_Parameters.getMultiplier(DriverType.PARTICLES),
	                v->C_Parameters.setMultiplier(DriverType.PARTICLES,v)), //
	        new Channel("chl","chlorophyll",CHLOROPHYLL_ICON,()->C_Parameters.getMultiplier(DriverType.PLANKTON),
	                v->C_Parameters.setMultiplier(DriverType.PLANKTON,v)),//
	        new Channel("nec","necton",NEKTON_ICON,()->C_Parameters.getMultiplier(DriverType.NEKTON),v->C_Parameters
	                .setMultiplier(DriverType.NEKTON,v)),//
	        new Channel("shi","ship",SHIP_ICON,()->C_Parameters.getMultiplier(DriverType.SHIP),v->C_Parameters
	                .setMultiplier(DriverType.SHIP,v)),//
	        new Channel("wha","whale",WHALE_ICON,()->C_Parameters.getMultiplier(DriverType.WHALE),v->C_Parameters
	                .setMultiplier(DriverType.WHALE,v)));//
	// new Channel("mou", "mount", MOUNT_ICON, () -> C_Parameters.MOUNT_MULTIPLIER,
	// v -> C_Parameters.MOUNT_MULTIPLIER = v), //
	// new Channel("tem", "temperature", TEMPERATURE_ICON, () -> C_Parameters.TEMPERATURE_MULTIPLIER,
	// v -> C_Parameters.TEMPERATURE_MULTIPLIER = v), //
	// new Channel("tun", "tuna", TUNA_ICON, () -> C_Parameters.TUNA_MULTIPLIER,
	// v -> C_Parameters.TUNA_MULTIPLIER = v), //
	// new Channel("wha", "whale", WHALE_ICON, () -> C_Parameters.WHALE_MULTIPLIER,
	// v -> C_Parameters.WHALE_MULTIPLIER = v), //
	// new Channel("sha", "shark", SHARK_ICON, () -> C_Parameters.SHARK_MULTIPLIER,
	// v -> C_Parameters.SHARK_MULTIPLIER = v), //
	// new Channel("bir", "bird", BIRD_ICON, () -> C_Parameters.BIRD_MULTIPLIER,
	// v -> C_Parameters.BIRD_MULTIPLIER = v), //
	// new Channel("tur", "turtle", TURTLE_ICON, () -> C_Parameters.TURTLE_MULTIPLIER,
	// v -> C_Parameters.TURTLE_MULTIPLIER = v), //
	// new Channel("fis", "fisher", FISHER_ICON, () -> C_Parameters.FISHER_MULTIPLIER,
	// v -> C_Parameters.FISHER_MULTIPLIER = v), //
	// new Channel("tou", "tourism", TOURISM_ICON, () -> C_Parameters.TOURISM_MULTIPLIER,
	// v -> C_Parameters.TOURISM_MULTIPLIER = v), //
	// new Channel("pol", "policeControl", POLICE_ICON, () -> C_Parameters.POLICE_MULTIPLIER,
	// v -> C_Parameters.POLICE_MULTIPLIER = v), //
	// new Channel("sew", "sewage", POLLUTION_ICON, () -> C_Parameters.POLLUTION_MULTIPLIER,
	// v -> C_Parameters.POLLUTION_MULTIPLIER = v)) //
	//
	// CONSTRUCTOR
	//
	public C_UserPanelEnergyLinearGrid() { this(false); }

	/** @param horizontal true pour la version MIAGE - TEST (sliders horizontaux, un canal par ligne) */
	public C_UserPanelEnergyLinearGrid(boolean horizontal) {
		super(new BorderLayout(8,8));

		JLabel title = new JLabel("Marine Energy Control ("+MIN+"× → "+MAX+"×)",SwingConstants.CENTER);
		title.setFont(title.getFont().deriveFont(Font.BOLD,16f));
		add(title,BorderLayout.NORTH);

		if(horizontal){
			// --- Une ligne par canal, empilées en haut (maquette 20261019-MIAGE2_UserPanel, phase 1) ---
			JPanel rows = new WidthTrackingPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(6,10,6,10);
			for(int i = 0;i<channels.size();i++)
				rows.add(buildRow(channels.get(i),SLIDER_COLORS[i%SLIDER_COLORS.length]),c);
			c.weighty = 1; // pousse les lignes vers le haut
			rows.add(new JPanel(),c);
			JScrollPane scroll = new JScrollPane(rows,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.getVerticalScrollBar().setUnitIncrement(16);
			add(scroll,BorderLayout.CENTER);
		}
		else{
			// --- Grille ROWS x COLS ---
			JPanel grid = new JPanel(new GridLayout(ROWS,COLS,12,12));
			for(Channel ch:channels) grid.add(buildColumn(ch));
			add(new JScrollPane(grid,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
		}

		// --- Barre d'actions ---
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JCheckBox cbLive = new JCheckBox("Appliquer en direct (Live)");
		cbLive.setSelected(live);
		cbLive.addActionListener(e->live = cbLive.isSelected());
		JButton btnApply = new JButton("Appliquer maintenant");
		btnApply.addActionListener(e->applyToParametersAndSave());
		JButton btnReset = new JButton("Reset 1.00×");
		btnReset.addActionListener(e->{
			setAllTo(1.0);
			applyToParametersAndSave();
		});
		JButton btnLoad = new JButton("Recharger CSV");
		btnLoad.addActionListener(e->{
			loadCSV();
			applyToParameters();
		});
		actions.add(cbLive);
		actions.add(btnApply);
		actions.add(btnReset);
		actions.add(btnLoad);
		add(actions,BorderLayout.NORTH);

		debounce.setRepeats(false);

		// --- Init: recharge CSV si dispo, sinon depuis C_Parameters ---
		if(!loadCSV()){
			// si pas de fichier, on initialise depuis les getters
			for(Channel ch:channels){
				double v = safe(ch.get.getAsDouble());
				setUI(ch,v);
			}
			saveCSV(); // crée un CSV dès le départ
		}
		// Capture l’état initial pour éviter un log massif au premier “apply”
		for(Channel ch:channels){
			lastApplied.put(ch.id,((Number)ch.spinner.getValue()).doubleValue());
		}

		// Plusieurs onglets éditent les mêmes paramètres : à l'affichage, on relit le CSV (sauvegardé à chaque
		// application) pour refléter les réglages faits dans l'autre onglet.
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) { resyncFromCSV(); }
		});
	}

	private void resyncFromCSV() {
		if(debounce.isRunning()) return; // modification locale en attente : ne pas l'écraser
		if(loadCSV()) for(Channel ch:channels)
			lastApplied.put(ch.id,((Number)ch.spinner.getValue()).doubleValue());
	}

	// ---------- Construction d'une "ligne" (slider horizontal) ----------
	// Disposition (maquette phase 1) :
	// [icône] Nom
	//         ----------●-------------------
	//         [0.25×][1.00×][10.00×]   [spinner]
	private JPanel buildRow(Channel ch, Color color) {
		JPanel presets = createControls(ch,JSlider.HORIZONTAL);
		ch.slider.setPaintTicks(false);
		ch.slider.setPaintLabels(false);
		ch.slider.setUI(new C_FlatSliderUI(ch.slider,color));
		ch.slider.setToolTipText(ch.label);
		for(java.awt.Component b:presets.getComponents())
			b.setFont(b.getFont().deriveFont(10f));

		JLabel icon = new JLabel(loadIconFromFile(ch.icon,64));
		icon.setBorder(BorderFactory.createLineBorder(new Color(190,190,190)));
		String name = ch.label.substring(0,1).toUpperCase(Locale.ROOT)+ch.label.substring(1);
		JLabel lbl = new JLabel(name);
		lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN,18f));

		JPanel row = new JPanel(new GridBagLayout());
		row.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
		GridBagConstraints c = new GridBagConstraints();
		// icône sur les 3 lignes
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 3;
		c.insets = new Insets(0,0,0,12);
		c.anchor = GridBagConstraints.CENTER;
		row.add(icon,c);
		c.gridheight = 1;
		c.insets = new Insets(0,0,0,0);
		// nom
		c.gridx = 1;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.WEST;
		row.add(lbl,c);
		// slider : largeur fixe (rétrécit jusqu'à SLIDER_MIN_WIDTH si la fenêtre est étroite)
		ch.slider.setPreferredSize(new Dimension(SLIDER_WIDTH,24));
		ch.slider.setMinimumSize(new Dimension(SLIDER_MIN_WIDTH,24));
		c.gridy = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		row.add(ch.slider,c);
		// presets + spinner
		c.gridy = 2;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		row.add(presets,c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.EAST;
		row.add(ch.spinner,c);
		// colonne vide qui absorbe la largeur restante : la ligne reste compacte, calée à gauche
		c.gridx = 3;
		c.gridy = 0;
		c.weightx = 1;
		row.add(Box.createHorizontalGlue(),c);

		setUI(ch,safe(ch.get.getAsDouble()));
		return row;
	}

	// ---------- Construction d'une "colonne" (vertical) ----------
	private JPanel buildColumn(Channel ch) {
		// label avec icône, texte à droite de l'icône
		ImageIcon imageIcon = loadIconFromFile(ch.icon,92);
		JLabel lbl = new JLabel(ch.label,imageIcon,SwingConstants.CENTER);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD,12f));
		lbl.setVerticalTextPosition(SwingConstants.CENTER);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		JPanel presets = createControls(ch,JSlider.VERTICAL);

		JPanel col = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1;
		c.insets = new Insets(4,6,4,6);
		c.fill = GridBagConstraints.HORIZONTAL;

		c.gridy = 0;
		c.weighty = 0;
		col.add(lbl,c);
		c.gridy = 1;
		c.weighty = 0;
		col.add(ch.valLabel,c);
		c.gridy = 2;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		col.add(ch.slider,c);
		c.gridy = 3;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		col.add(ch.spinner,c);
		c.gridy = 4;
		col.add(presets,c);

		// valeur initiale depuis getter
		setUI(ch,safe(ch.get.getAsDouble()));
		return col;
	}

	/** Crée slider + spinner + label de valeur du canal, les synchronise et retourne le panneau de presets */
	private JPanel createControls(Channel ch, int orientation) {
		ch.slider = slider(orientation,(int)Math.round(MIN*100),(int)Math.round(MAX*100),100);
		ch.spinner = spinner(1.00);
		ch.valLabel = new JLabel("1.00×",SwingConstants.CENTER);
		ch.valLabel.setFont(ch.valLabel.getFont().deriveFont(Font.BOLD,12f));

		// presets
		JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER,4,0));
		for(double v:new double[]{MIN,1.0,MAX}){
			JButton b = new JButton(df.format(v)+"×");
			b.setMargin(new Insets(2,6,2,6));
			b.addActionListener(e->{
				setUI(ch,v);
				if(live) debounce.restart();
			});
			presets.add(b);
		}

		// slider -> spinner (+ apply à fin de drag)
		ch.slider.addChangeListener((ChangeEvent e)->{
			if(syncing) return;
			syncing = true;
			double val = ch.slider.getValue()/100.0;
			ch.spinner.setValue(val);
			ch.valLabel.setText(df.format(val)+"×");
			if(live){
				debounce.restart();
			}
			else if(!ch.slider.getValueIsAdjusting()){
				applyToParametersAndSave();
			}
			syncing = false;
		});

		// spinner -> slider (+ apply si Live)
		ch.spinner.addChangeListener(e->{
			if(syncing) return;
			syncing = true;
			double v = ((Number)ch.spinner.getValue()).doubleValue();
			ch.slider.setValue((int)Math.round(v*100));
			ch.valLabel.setText(df.format(v)+"×");
			if(live) debounce.restart();
			syncing = false;
		});
		return presets;
	}

	private static JSlider slider(int orientation, int min, int max, int val) {
		JSlider s = new JSlider(orientation,min,max,val);
		s.setPaintTicks(true);
		s.setPaintLabels(true);
		s.setMajorTickSpacing(25);
		s.setMinorTickSpacing(5);
		s.setLabelTable(createLinearLabelTable(min,max));
		return s;
	}

	private static java.util.Dictionary<Integer,JLabel> createLinearLabelTable(int min, int max) {
		java.util.Hashtable<Integer,JLabel> table = new java.util.Hashtable<>();
		double[] ticks = new double[]{0.,1.00,2.,3.,4.,5.,6.,7.,8.,9.,10.};
		for(double v:ticks){
			int pos = (int)Math.round(v*100);
			table.put(pos,new JLabel((v==Math.rint(v))?String.valueOf((int)v):String.format(Locale.ROOT,"%.2f",v)));
		}
		return table;
	}

	private static JSpinner spinner(double init) {
		JSpinner sp = new JSpinner(new SpinnerNumberModel(init,MIN,MAX,0.01));
		((JSpinner.NumberEditor)sp.getEditor()).getFormat().applyPattern("0.00");
		sp.setPreferredSize(new Dimension(70,24));
		return sp;
	}

	// ---------- Helpers de synchro UI ----------
	private void setUI(Channel ch, double v) {
		v = safe(v);
		syncing = true;
		ch.spinner.setValue(v);
		ch.slider.setValue((int)Math.round(v*100));
		ch.valLabel.setText(df.format(v)+"×");
		syncing = false;
	}

	private void setAllTo(double v) { for(Channel ch:channels) setUI(ch,v); }

	private static double safe(double v) {
		if(Double.isNaN(v)||Double.isInfinite(v)||v<MIN||v>MAX) return 1.0;
		return v;
	}

	// ---------- Application & CSV générique ----------
	private void applyToParametersWithLogging() {
		for(Channel ch:channels){
			double v = ((Number)ch.spinner.getValue()).doubleValue();
			Double prev = lastApplied.get(ch.id);

			// si changement significatif, on applique ET on logue
			if(prev==null||Math.abs(prev-v)>EPS){
				ch.set.accept(v); // écrit dans C_Parameters
				A_Protocol.event("C_UserPanelEnergyLinearGrid","!!!!!! PATRIMONIAL WEIGHT CHANGED: "+ch.label+"("+ch.id
				        +") "+v,false);
				lastApplied.put(ch.id,v);
			}
			// sinon: rien (pas de log, pas de réécriture)
		}
	}

	private void applyToParameters() { applyToParametersWithLogging(); }

	private void applyToParameters0() {
		for(Channel ch:channels){
			double v = ((Number)ch.spinner.getValue()).doubleValue();
			ch.set.accept(v); // écrit dans C_Parameters
			//
			double prev = lastApplied.getOrDefault(ch.id,Double.NaN);
			if(Double.isNaN(prev)||Math.abs(prev-v)>EPS){
				System.out.printf("[EnergyGrid] %s (%s) -> %.2fx%n",ch.label,ch.id,v);
				lastApplied.put(ch.id,v);
			}

		}
	}

	private void applyToParametersAndSave() {
		applyToParametersWithLogging(); // n’affiche que ce qui change
		saveCSV();
	}

	private boolean loadCSV() {
		if(!CSV_FILE.exists()) return false;
		Map<String,Double> map = new LinkedHashMap<>();
		try(BufferedReader r = Files.newBufferedReader(CSV_FILE.toPath(),StandardCharsets.UTF_8)){
			String line;
			boolean first = true;
			while((line = r.readLine())!=null){
				if(first){
					first = false;
					continue;
				}
				String[] parts = line.split(",");
				if(parts.length>=2){
					String key = parts[0].trim();
					double val = parseSafe(parts[1].trim(),1.0);
					map.put(key,val);
				}
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
			return false;
		}
		// applique aux canaux connus (ignore les clés inconnues)
		for(Channel ch:channels){
			double v = map.getOrDefault(ch.id,safe(ch.get.getAsDouble()));
			setUI(ch,safe(v));
		}
		return true;
	}

	private void saveCSV() {
		try(BufferedWriter w = Files.newBufferedWriter(CSV_FILE.toPath(),StandardCharsets.UTF_8)){
			w.write("name,value\n");
			for(Channel ch:channels){
				double v = ((Number)ch.spinner.getValue()).doubleValue();
				w.write(String.format(Locale.ROOT,"%s,%.6f%n",ch.id,v));
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
			JOptionPane.showMessageDialog(this,"Erreur de sauvegarde CSV: "+ioe.getMessage(),"Erreur",
			        JOptionPane.ERROR_MESSAGE);
		}
	}

	private static double parseSafe(String s, double def) {
		try{
			return Double.parseDouble(s);
		}catch(Exception e){
			return def;
		}
	}
}
