package presentation.display;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;

import bsh.util.JConsole;
import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import data.constants.rodents.I_ConstantImagesNames;
import data.constants.rodents.I_ConstantStringRodents;
import melanesim.C_ContextCreator;
import melanesim.protocol.A_Protocol;
import presentation.epiphyte.A_Inspector;
import presentation.epiphyte.A_InspectorPopulation;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.userpanel.ui.UserPanelCreator;

/** Tableau de bord de la simulation. Contient la date de la simulation (à chaque tick), des moteurs et un retour de la console.
 * @author A Realini 2011, rev. Le Fur 2013, 2018, 2024 */
public class C_UserPanel extends JPanel implements UserPanelCreator, I_ConstantStringRodents, I_ConstantPNMC,
		I_ConstantImagesNames {
	//
	// FIELDS
	//
	protected static final long serialVersionUID = 1L;
	public static A_Inspector populationInspector = null;

	protected JPanel titleBox = null;
	protected JPanel dateBox = null;
	protected JPanel metersPopulation = null;
	protected JPanel consoleOutBox = null;
	protected JPanel consoleErrBox = null;
	protected JPanel daytimeJpanel = null;
	protected JLabel dayMomentsJlabel = null;

	public static String METER_POPSIZE_TITLE = "Context Size (X1E3)";
	protected Font font = new Font("Courier", Font.BOLD, 18);
	protected BufferedImage img = null, chronoImage = null;
	protected JLabel dateLab;
	public static JConsole consoleOut = null;
	public static JConsole consoleErr = null;

	protected C_Meter meterPopSize;
	String currentImageName = "";

	//
	// CONSTRUCTOR
	//
	public C_UserPanel() {
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		RunState.getInstance().getMasterContext().add(this); // needed to step panel
		init();
	}
	//
	// METHODS
	//
	/** Méthode utilisée par le UserPanelCreator pour afficher le tableau de bord dans le userPanel */
	public JPanel createPanel() {
		return this;
	}
	@ScheduledMethod(start = 0, interval = 1, shuffle = false, priority = 0)
	public void step() {
		this.dateLab.setText(C_ContextCreator.protocol.getStringFullDate());
		update_Meters();
		if (this.hasToShowDayMoments()) this.updateDaysMomentsJpanel();
	}

	/** Initialise les composants du tableau de bord */
	protected void init() {
		this.initTop();
		this.initBottom();
	}
	protected void initTop() {
		this.titleBox = initBoxLayout("ßytemån project / IRD / CBGP");
		this.dateBox = initBoxLayout("Simulation Date");
		this.metersPopulation = initBoxLayout("Population (every agents)");
		if (this.hasToShowDayMoments()) this.createDayMomentsJanel();

		// General layout for the panel
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setAutoscrolls(false);

		// A. BOX TITLE //
		String fileName = "icons/titleEmpty";
		this.titleBox.add(createTitleBlock(fileName));

		// B. BOX DATE //
		this.dateLab = new JLabel();
		this.dateLab.setFont(this.font);
		this.dateLab.setText(C_ContextCreator.protocol.getStringFullDate());
		this.dateBox.add(this.dateLab);

		// C. BOXES METERS //

		// 1.BOX POPULATION
		this.meterPopSize = new C_Meter(METER_POPSIZE_TITLE, true, 1000);
		if (this.hasToShowDayMoments()) this.metersPopulation.add(this.daytimeJpanel);
		else {}
		this.metersPopulation.add(this.meterPopSize.getPan());
	}
	protected void initBottom0() {
		this.consoleOutBox = initBoxLayout("Console output");
		this.consoleErrBox = initBoxLayout("Console Error");
		// D. BOX CONSOLE OUT//
		C_UserPanel.consoleOut = createConsole();
		System.setOut(consoleOut.getOut()); // redirect output to GUI console
		this.consoleOutBox.add(C_UserPanel.consoleOut);
		// E. BOX CONSOLE ERR//
		C_UserPanel.consoleErr = createConsole();
		System.setErr(C_UserPanel.consoleErr.getErr()); // redirect error output to GUI console
		this.consoleErrBox.add(C_UserPanel.consoleErr);
	}
	/** @author chatGPT 03.2025 */
	protected void initBottom() {
		// Les consoles sont mises dans un panel vertical à 2 cases égales
		JPanel consoleWrapper = new JPanel();
		consoleWrapper.setLayout(new GridLayout(2, 1)); // Deux cases verticales : 50% / 50%
		consoleWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
		// D. BOX CONSOLE OUT//
		C_UserPanel.consoleOut = createConsole();
		System.setOut(consoleOut.getOut());
		// E. BOX CONSOLE ERR//
		C_UserPanel.consoleErr = createConsole();
		System.setErr(consoleErr.getErr());
		// Envelopper chaque console dans un JPanel avec un titre
		consoleOutBox = wrapConsoleWithTitle("Console Output", consoleOut);
		consoleErrBox = wrapConsoleWithTitle("Console Error", consoleErr);
		// Ajouter dans le wrapper
		consoleWrapper.add(consoleOutBox);
		consoleWrapper.add(consoleErrBox);
		// Ajouter le wrapper à ce panneau (C_UserPanel)
		this.add(consoleWrapper); // à la fin : occupe tout l’espace restant
	}
	/** @author chatGPT 03.2025 */
	private JPanel wrapConsoleWithTitle(String title, JConsole console) {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder(title));
		panel.add(console, BorderLayout.CENTER);
		return panel;
	}

	protected JConsole createConsole() {
		JConsole console = new JConsole();
		console.createHorizontalScrollBar();
		console.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		console.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		console.setVisible(true);
		console.setFont(new Font("serif", Font.PLAIN, 9));
		console.setPreferredSize(new Dimension(1000, 100));
		return console;
	}
	/** Met à jour les données des compteurs */
	protected void update_Meters() {
		C_UserPanel.populationInspector = A_Protocol.inspector;
		int popSize = A_InspectorPopulation.agentPopulation;
		this.meterPopSize.setData(popSize);
		// meterObjects.setData(RunState.getInstance().getMasterContext().size());
	}
	protected JPanel initBoxLayout(String title) {
		JPanel onePanel = new JPanel();
		onePanel.setLayout(new BoxLayout(onePanel, BoxLayout.LINE_AXIS));
		onePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		onePanel.setSize(350, onePanel.getHeight());
		if (!title.isEmpty()) onePanel.setBorder(BorderFactory.createTitledBorder(title));
		this.add(onePanel);
		return onePanel;
	}
	/** Gestion de l'image centrale (JLF 2011, 2018) */
	protected JLabel createTitleBlock(String fileName) {
		try {
			this.img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			A_Protocol.event("C_UserPanel.createTitleBlock", "Could not find " + fileName, isError);
		}
		JLabel image = new JLabel();
		image.setIcon(new ImageIcon(this.img));
		return (image);
	}
	/** It the container of the daytime display.
	 * @author M SALL 2020, */
	protected void createDayMomentsJanel() {
		this.daytimeJpanel = initBoxLayout("Daytime");
		this.daytimeJpanel.setMaximumSize(new Dimension(100, 120));
		this.daytimeJpanel.add(this.updateChronoBlock(DAY));
	}

	/** Verify the current display of the daytime JPanel and change it if necessary.
	 * @author M SALL 12.2020, */
	protected void updateDaysMomentsJpanel() {
		Boolean hasToSwitchImage = false;
		if (A_Protocol.protocolCalendar.isDayTime() && this.currentImageName != DAY) {
			this.currentImageName = DAY;
			hasToSwitchImage = true;
		}
		else if (A_Protocol.protocolCalendar.isTwilight() && this.currentImageName != TWILIGHT) {
			this.currentImageName = TWILIGHT;
			hasToSwitchImage = true;
		}
		else if (A_Protocol.protocolCalendar.isNightTime() && this.currentImageName != NIGHT) {
			this.currentImageName = NIGHT;
			hasToSwitchImage = true;
		}
		else if (A_Protocol.protocolCalendar.isDawn() && this.currentImageName != DAWN) {
			this.currentImageName = DAWN;
			hasToSwitchImage = true;
		}
		if (hasToSwitchImage) {
			this.updateChronoBlock(this.currentImageName);
		}

	}
	/** Create the chrono block image if it null or update it if not.
	 * @author M SALL 12.2020, */
	protected JLabel updateChronoBlock(String imageName) {
		try {
			this.chronoImage = ImageIO.read(new File($PATH + imageName + ext));
		} catch (IOException e) {
			A_Protocol.event("C_UserPanel.createTitleBlock", "Could not find " + "", isError);
		}
		if (this.dayMomentsJlabel != null)
			this.dayMomentsJlabel.setIcon(new ImageIcon(this.chronoImage.getScaledInstance(85, 85, 0)));
		else
			this.dayMomentsJlabel = new JLabel(new ImageIcon(this.chronoImage.getScaledInstance(85, 85, 0)));
		return this.dayMomentsJlabel;
	}
	/** Create the chrono block image if it null or update it if not.
	 * @author M SALL 12.2020, */
	public boolean hasToShowDayMoments() {
		switch (C_Parameters.PROTOCOL) {
			case DODEL2 :
			case DODEL :
			case GERBIL :
			case PNMC_DRIFTERS :
			case PNMC_PLANKTON :
				return true;
			default :
				return false;
		}
	}

	@Override
	public String toString() {
		return "User panel";
	}
}
