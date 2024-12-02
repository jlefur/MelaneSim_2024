package presentation.display;

import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import bsh.util.JConsole;
import data.C_Parameters;
import melanesim.protocol.rodents.A_ProtocolRodent;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorPopulationRodent;
import presentation.epiphyte.C_InspectorTransportation;

/** Tableau de bord de la simulation.
 * @author A Realini 2011, rev. Le Fur 2013, 2018, 2024 */
public class C_UserPanelRodent extends C_UserPanel {
	//
	// FIELDS
	//
	private static C_InspectorPopulationRodent populationInspector = null;
	private static final long serialVersionUID = 1L;
	private static C_InspectorFossorialRodents burrowInspector = null;
	private static C_InspectorTransportation transportationInspector = null;
	private static C_InspectorGenetic geneticInspector = null;
	private static C_InspectorHybrid hybridInspector = null;

	private JPanel metersDispersal = null;
	private JPanel metersHybrid = null;;
	// JPanel metersTransportation = initBoxLayout("Transportation");
	// JPanel metersHybridization = initBoxLayout("Hybridization");

	public static final String METER_POPSIZE_TITLE = "Rodent pop. (X100)";
	public static final String METER_SEXRATIO_TITLE = "sex ratio";
	public static final String METER_MAXDISP_TITLE = "Max (x100m)";
	public static final String METER_MEANDISP_TITLE = "Mean  (x100m)";
	public static final String METER_FIS_TITLE = "Inbreeding (FISx100)";
	public static final String METER_WANDERERS_TITLE = "Wanderers(%)";
	public static final String METER_NIPPED_EGG_TITLE = "Nipped eggs (X1000)";
	public static final String METER_HYBRIDS_TITLE = "hybrids (x100)";
	public static final String METER_LAZARUS_TITLE = "introgressed (x100)";
	//
	private BufferedImage img = null, chronoImage = null;
	public static JConsole consoleOut = null;
	public static JConsole consoleErr = null;

	// meters Population
	private C_Meter meterPopSize;
	private C_Meter meterSexRatio;
	// meters Dispersal
	private C_Meter meterFIS;
	private C_Meter meterMeanDispersal;
	private C_Meter meterMaxDispersal;
	private C_Meter meterWanderers;
	// meters Transportation
	private C_Meter meterCities;
	private C_Meter meterLoads;
	// meters Hybridization
	private C_Meter meterHybrids;
	private C_Meter meterIntrogressed;
	private C_Meter meterNippedEggs;
	// meters General
	// private C_Meter meterObjects;
	String currentImageName = "";

	//
	// CONSTRUCTOR
	//
	public C_UserPanelRodent() {
		super();
	}
	//
	// METHODS
	//
	/** M�thode utilis�e par le UserPanelCreator pour afficher le tableau de bord dans le userPanel */
	public JPanel createPanel() {
		return this;
	}

	public static void addBurrowInspector(C_InspectorFossorialRodents inspector) {
		burrowInspector = inspector;
	}
	public static void addTransportationInspector(C_InspectorTransportation inspector) {
		transportationInspector = inspector;
	}
	public static void addGeneticInspector(C_InspectorGenetic inspector) {
		geneticInspector = inspector;
	}
	public static void addHybridInspector(C_InspectorHybrid inspector) {
		hybridInspector = inspector;
	}
	/** Initialise les composants du tableau de bord */
	protected void initTop() {
		super.initTop();
		//this.metersPopulation = initBoxLayout("Population (every rodents)");
		//if (this.hasToShowDayMoments()) this.createDayMomentsJanel();
		if (C_UserPanelRodent.hybridInspector == null)
			this.metersDispersal = initBoxLayout("Dispersal (every rodents)");
		if (C_UserPanelRodent.hybridInspector != null) this.metersHybrid = initBoxLayout("Hybridization (Mastomys)");

		// General layout for the panel
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.setAutoscrolls(false);

		// C. BOXES METERS //
		// 1.BOX POPULATION
		this.meterPopSize = new C_Meter(METER_POPSIZE_TITLE, true, 100);
		if (!this.hasToShowDayMoments())  {
			this.meterSexRatio = new C_Meter(METER_SEXRATIO_TITLE, false, 0, 2);
			this.metersPopulation.add(this.meterSexRatio.getPan());
		}
		this.metersPopulation.add(this.meterPopSize.getPan());
		this.meterWanderers = new C_Meter(METER_WANDERERS_TITLE, false, 100);
		this.metersPopulation.add(this.meterWanderers.getPan());
		// 2.BOX DISPERSAL
		if (C_UserPanelRodent.hybridInspector == null) {// TODO JLF 2018.05 Temporary for cages, but cannot not be applied for
			// hybridUniform
			this.meterMeanDispersal = new C_Meter(METER_MEANDISP_TITLE, true, 100);
			this.meterMaxDispersal = new C_Meter(METER_MAXDISP_TITLE, true, 100);
			this.metersDispersal.add(this.meterMeanDispersal.getPan());
			this.metersDispersal.add(this.meterMaxDispersal.getPan());
			if (C_UserPanelRodent.geneticInspector != null) {
				this.meterFIS = new C_Meter(METER_FIS_TITLE, true, -5, 5);
				this.metersDispersal.add(this.meterFIS.getPan());
			}
		}
		// 3. BOX HYBRIDIZATION
		if (C_UserPanelRodent.hybridInspector != null) {
			this.meterNippedEggs = new C_Meter(METER_NIPPED_EGG_TITLE, true, 1000);
			this.metersHybrid.add(this.meterNippedEggs.getPan());
			this.meterHybrids = new C_Meter(METER_HYBRIDS_TITLE, true, 100);
			this.metersHybrid.add(this.meterHybrids.getPan());
			this.meterIntrogressed = new C_Meter(METER_LAZARUS_TITLE, true, 100);
			this.metersHybrid.add(this.meterIntrogressed.getPan());
		}
	}

	@Override
	/** Met à jour les données des compteurs */
	protected void update_Meters() {
		super.update_Meters();
		C_UserPanelRodent.populationInspector = A_ProtocolRodent.inspector;
		int popSize = C_InspectorPopulationRodent.rodentList.size();
		this.meterPopSize.setData(popSize);
		if (!this.hasToShowDayMoments())
			this.meterSexRatio.setData((double) C_InspectorPopulationRodent.getNbFemales()
					/ (double) C_InspectorPopulationRodent.getNbMales());
		if (C_UserPanelRodent.hybridInspector == null) {// TODO JLF 2018.05 Temporary for cages, but cannot not be applied for
			// hybridUniform
			this.meterMeanDispersal.setData((populationInspector.getMeanFemaleDispersal() + populationInspector
					.getMeanMaleDispersal()) * .5);
			this.meterMaxDispersal.setData(Math.max(populationInspector.getMaxFemaleDispersal(), populationInspector
					.getMaxMaleDispersal()));
			if (geneticInspector != null) this.meterFIS.setData(100 * geneticInspector.getFixationIndex());
		}
		if (burrowInspector != null) this.meterWanderers.setData(burrowInspector.getWanderingRodents_Upercent() * 100);
		if (hybridInspector != null) {
			this.meterNippedEggs.setData(hybridInspector.getPbNippedEgg());
			hybridInspector.resetPbNippedEggs();
			this.meterHybrids.setData(hybridInspector.getNbHybrids());
			this.meterIntrogressed.setData(hybridInspector.getNbLazarus());
		}
		if (transportationInspector != null) {
			this.meterCities.setData(transportationInspector.getCityList().size());
			this.meterLoads.setData(transportationInspector.getCarriersLoad_Urodent());
		}
		// meterObjects.setData(RunState.getInstance().getMasterContext().size());
	}
	@Override
	/** Gestion de l'image centrale (JLF - june 2011, may 2018) */
	protected JLabel createTitleBlock(String FileName) {
		String fileName = "";
		switch (C_Parameters.PROTOCOL) {
			case CHIZE :
				fileName = "icons/rodents/titleChize.jpg";
				break;
			case CHIZE2 :
				fileName = "icons/rodents/titleChize2.gif";
				break;
			case CENTENAL :
				fileName = "icons/rodents/titleCentenal.gif";
				break;
			case DECENAL :
				fileName = "icons/rodents/titleDecenal.gif";
				break;
			case CAGES :
				fileName = "icons/rodents/titleCages.gif";
				break;
			case DODEL :
				fileName = "icons/rodents/titleDodel1.gif";
				break;
			case DODEL2 :
				fileName = "icons/rodents/titleDodel2.gif";
				break;
			case MUS_TRANSPORT :
				fileName = "icons/rodents/titleMusTransport.gif";
				break;
			case BANDIA :
				fileName = "icons/rodents/titleBandia.gif";
				break;
			case GERBIL :
				fileName = "icons/rodents/titleGerbil.gif";
				break;
			case HYBRID_UNIFORM :
				fileName = "icons/rodents/titleUniform.gif";
				break;
		}
		return super.createTitleBlock(fileName);
	}
	@Override
	public String toString() {
		return "SimMasto panel";
	}
}
