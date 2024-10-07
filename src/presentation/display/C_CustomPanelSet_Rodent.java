package presentation.display;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.plot.XYPlot;

import presentation.epiphyte.C_InspectorBorreliaCrocidurae;
import presentation.epiphyte.C_InspectorCMR;
import presentation.epiphyte.C_InspectorFossorialRodents;
import presentation.epiphyte.C_InspectorGenetic;
import presentation.epiphyte.C_InspectorHybrid;
import presentation.epiphyte.C_InspectorOrnithodorosSonrai;
import presentation.epiphyte.C_InspectorTransportation;
import presentation.epiphyte.C_InspectorVegetation;
import repast.simphony.engine.environment.GUIRegistryType;
import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;

/** Initialise la simulation avec des onglets "programmables"
 * @author A. Realini, rev. J. Le Fur 02.2013, O2.2017, 02.2021 */
public class C_CustomPanelSet_Rodent extends C_CustomPanelSet {

	private C_CustomPanelFactory curveFIS, curveRates, curveVegetation, curveOrnithodoros, curvedesease;
	// the following are meant to avoid multiple calls to ContextCreator (see execute()) // JLF 02.2013
	private static C_InspectorHybrid hybridInspector = null;
	private static C_InspectorTransportation transportationInspector = null;
	private static C_InspectorGenetic geneticInspector = null;
	private static C_InspectorCMR C_InspectorCMR = null;// Ajout Malick
	private static C_InspectorVegetation vegetationInspector = null;
	private static C_InspectorOrnithodorosSonrai ornithodorosInspector = null;

	protected void initWorldSpecificPlots(RunState runState) {
		XYPlot plot;
		if (C_InspectorCMR != null) {
			curvePopSize.getChart().addXYSerie("MNA");
			curvePopSize.getChart().addXYSerie("TrapSystem");
			curveDispersal.getChart().addXYSerie("DRS");
			curveDispersal.getChart().addXYSerie("DMR");
		}
		if (hybridInspector != null) {
			curvePopSize.getChart().addXYSerie("M.ery");
			curvePopSize.getChart().addXYSerie("M.nat");
			curvePopSize.getChart().addXYSerie("M.laz");
			curvePopSize.getChart().addXYSerie("Hybrids");
		}
		// RATES
		curveRates = new C_CustomPanelFactory("Rates", C_Chart.LINE, "Ticks", "rate (%)");
		runState.getGUIRegistry().addDisplay("Rates", GUIRegistryType.OTHER, curveRates);
		curveRates.getChart().addXYSerie("Death");
		curveRates.getChart().addXYSerie("Birth");
		curveRates.getChart().addXYSerie("WanderingRodents");

		// FIS
		if (geneticInspector != null) {
			curveFIS = new C_CustomPanelFactory("Fixation index (FIS)", C_Chart.LINE, "Ticks", "Fixation Index (FIS)");
			runState.getGUIRegistry().addDisplay("FIS", GUIRegistryType.OTHER, curveFIS);
			curveFIS.getChart().addXYSerie("Fixation Index");
		}

		// TRANSPORTATION
		if (transportationInspector != null) {
			// citiesBars = new C_CustomPanelFactory("Carriers & rodents in cities", C_Chart.BAR, "city", "N
			// carriers/rodents AGENTS");
			// runState.getGUIRegistry().addDisplay("Transportation", GUIRegistryType.OTHER, citiesBars);
		}

		// VEGETATION
		if (vegetationInspector != null) {
			curveVegetation = new C_CustomPanelFactory("Total energy (Vegetation)", C_Chart.LINE, "Tick",
					"mean energy (pseudo-kcal)");
			runState.getGUIRegistry().addDisplay("Vegetation and rain", GUIRegistryType.OTHER, curveVegetation);
			curveVegetation.getChart().addXYSerie("ShrubEnergy");
			curveVegetation.getChart().addXYSerie("CropEnergy");
			curveVegetation.getChart().addXYSerie("GrassEnergy");
			curveVegetation.getChart().addXYSerie("TotalRainFall");
			plot = curveVegetation.getChart().getChartPanel().getChart().getXYPlot();
			plot.getRenderer().setSeriesPaint(0, Color.PINK);
			plot.getRenderer().setSeriesPaint(1, Color.GREEN);
			plot.getRenderer().setSeriesPaint(2, Color.BLUE);
			plot.getRenderer().setSeriesPaint(3, Color.BLACK);
			plot.getRenderer().setSeriesStroke(3, new BasicStroke(.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
					1.0f, new float[]{10.0f, 6.0f}, 0.0f));
		}

		// ORNITHODOROS
		if (ornithodorosInspector != null) {
			curveOrnithodoros = new C_CustomPanelFactory("Tick action curve", C_Chart.LINE, "Tick", "Actions");
			runState.getGUIRegistry().addDisplay("Ticks", GUIRegistryType.OTHER, curveOrnithodoros);
			curveOrnithodoros.getChart().addXYSerie("Births");
			// curveOrnithodoros.getChart().addXYSerie("Hibernation Average");
			curveOrnithodoros.getChart().addXYSerie("Bites");
			curveOrnithodoros.getChart().addXYSerie("Infected");
			// curveOrnithodoros.getChart().addXYSerie("Population");
			// curveOrnithodoros.getChart().addXYSerie("Male");
			// curveOrnithodoros.getChart().addXYSerie("Female");
			plot = curveOrnithodoros.getChart().getChartPanel().getChart().getXYPlot();
			plot.getRenderer().setSeriesPaint(0, Color.BLUE);
			plot.getRenderer().setSeriesPaint(1, Color.DARK_GRAY);
			plot.getRenderer().setSeriesPaint(2, Color.PINK);
			plot.getRenderer().setSeriesPaint(3, Color.GREEN);
			plot.getRenderer().setSeriesPaint(4, Color.YELLOW);
			plot.getRenderer().setSeriesPaint(5, Color.ORANGE);

			// DESEASE CURVE
			curvedesease = new C_CustomPanelFactory("Desease curve", C_Chart.LINE, "Tick", "Infected size");
			runState.getGUIRegistry().addDisplay("Infected size", GUIRegistryType.OTHER, curvedesease);
			curvedesease.getChart().addXYSerie("Infected Ticks");
			curvedesease.getChart().addXYSerie("Healthy Ticks");
			curvedesease.getChart().addXYSerie("Infected Rodent");
			curvedesease.getChart().addXYSerie("Healthy Rodent");
			plot = curvedesease.getChart().getChartPanel().getChart().getXYPlot();
			plot.getRenderer().setSeriesPaint(0, Color.PINK);
			plot.getRenderer().setSeriesPaint(1, Color.GREEN);
			plot.getRenderer().setSeriesPaint(2, Color.red);
			plot.getRenderer().setSeriesPaint(3, Color.BLUE);
		}
	}

	public String toString() {
		return "SimMastoInitializer";
	}

	/** Use it for no graphs in the GUI */
	public void execute0() {}

	/** Update each series with the corresponding data */
	public void execute() {
		super.execute();

		// BURROWS
		/*
		 * if (burrowInspector != null) curvePopSize.getChart().setData("WanderingRodents", RepastEssentials.GetTickCount(),
		 * burrowInspector .getWanderingRodents_Upercent() * C_InspectorPopulation.rodentList.size());
		 */
		// TRANSPORTATION
		if (transportationInspector != null)
			curvePopSize.getChart().setData("LoadedRodents(x10)", RepastEssentials.GetTickCount(),
					transportationInspector.getCarriersLoad_Urodent() * 10);
		// HYBRIDIZATION IN POPULATION DISPLAY
		if (hybridInspector != null) {
			curvePopSize.getChart().setData("Hybrids", RepastEssentials.GetTickCount(), hybridInspector.getNbHybrids());
			curvePopSize.getChart().setData("M.ery", RepastEssentials.GetTickCount(), hybridInspector.getNbEry());
			curvePopSize.getChart().setData("M.laz", RepastEssentials.GetTickCount(), hybridInspector.getNbLazarus());
			curvePopSize.getChart().setData("M.nat", RepastEssentials.GetTickCount(), hybridInspector.getNbNat());
		}
		// CMR DISPLAY
		if (C_InspectorCMR != null) {
			curvePopSize.getChart().setData("MNA", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentMNA());
			curvePopSize.getChart().setData("TrapSystem", RepastEssentials.GetTickCount(), C_InspectorCMR
					.getTrapAreaPopulation_Urodent());
			curveDispersal.getChart().setData("DRS", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentDRS());
			curveDispersal.getChart().setData("DMR", RepastEssentials.GetTickCount(), C_InspectorCMR.getCurrentDMR());
		}
		// FIS DISPLAY
		if (geneticInspector != null)
			curveFIS.getChart().setData("Fixation Index", RepastEssentials.GetTickCount(), geneticInspector
					.getFixationIndex());
		// RATES DISPLAY
		curveRates.getChart().setData("Birth", RepastEssentials.GetTickCount(), ((double) inspector.getBirthRatio()
				* 100));
		curveRates.getChart().setData("Death", RepastEssentials.GetTickCount(), ((double) inspector.getDeathRatio()
				* 100));
		// if (burrowInspector != null)
		// curveRates.getChart().setData("WanderingRodents", RepastEssentials.GetTickCount(), burrowInspector
		// .getWanderingRodents_Upercent() * 100);
		// CITIES BARS DISPLAY (RODENTS & CARRIERS BY CITY)
		if (transportationInspector != null) {/*
												 * for (C_City oneCity : transportationInspector.getCityList()) {
												 * citiesBars.getChart().setBarData(oneCity.getFullRodentList().size(),
												 * "rodent pop.", oneCity.toString()); // TODO number in source 2015.10 JLF HC
												 * graph multiplier
												 * citiesBars.getChart().setBarData(oneCity.getFullLoad_Ucarrier() * 10,
												 * "carrier pop.", oneCity.toString()); } citiesBars.getChart().setTitle(
												 * transportationInspector.getCarrierList().size() *
												 * C_Parameters.HUMAN_SUPER_AGENT_SIZE + " carriers total; " +
												 * C_InspectorPopulation.getNbRodents() * C_Parameters.RODENT_SUPER_AGENT_SIZE +
												 * "000 rodents total");
												 */}
		// VEGETATION DISPLAY
		if (vegetationInspector != null) {
			int nbVegetationAgents = vegetationInspector.getVegetationList().size();
			nbVegetationAgents = 1;// to compute sum instead of mean, may be commented if needed
			curveVegetation.getChart().setData("ShrubEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getShrubEnergy() / nbVegetationAgents);
			curveVegetation.getChart().setData("CropEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getCropEnergy() / nbVegetationAgents);
			curveVegetation.getChart().setData("GrassEnergy", RepastEssentials.GetTickCount(), vegetationInspector
					.getGrassEnergy() / nbVegetationAgents);
			// TODO number in source 2016.07 JLF rainFall multiplier (arbitrary)
			curveVegetation.getChart().setData("TotalRainFall", RepastEssentials.GetTickCount(), vegetationInspector
					.getTotalPrecipitation_Umm());
		}
		// ORNITHODOROS DISPLAY
		if (ornithodorosInspector != null) {
			curveOrnithodoros.getChart().setData("Births", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getBirthNumber());
			curveOrnithodoros.getChart().setData("Bites", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getBiteNumber());
			curvedesease.getChart().setData("Infected Ticks", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getInfectedNumber());
			curvedesease.getChart().setData("Healthy Ticks", RepastEssentials.GetTickCount(), ornithodorosInspector
					.getHealthyNumber());
			// curveOrnithodoros.getChart().setData("Hibernation Average", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getHibernationAverage());
			// curveOrnithodoros.getChart().setData("Population", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getTicksNumber());
			// curveOrnithodoros.getChart().setData("Male", RepastEssentials.GetTickCount(), ornithodorosInspector.getTickMale());
			// curveOrnithodoros.getChart().setData("Female", RepastEssentials.GetTickCount(),
			// ornithodorosInspector.getTickFemale());
			// INFECTED RODENT DISPLAY
			curvedesease.getChart().setData("Infected Rodent", RepastEssentials.GetTickCount(), inspector
					.getInfectedRodents());
			curvedesease.getChart().setData("Healthy Rodent", RepastEssentials.GetTickCount(), inspector
					.getHealthyRodent());
		}
	}
	public static void addHybridInspector(C_InspectorHybrid inspector) {
		hybridInspector = inspector;
	}
	public static void addGeneticInspector(C_InspectorGenetic inspector) {
		geneticInspector = inspector;
	}
	public static void addTransportationInspector(C_InspectorTransportation inspector) {
		transportationInspector = inspector;
	}
	public static void addBurrowInspector(C_InspectorFossorialRodents inspector) {}
	public static void addCMRInspector(C_InspectorCMR inspector) {
		C_InspectorCMR = inspector;
	}
	public static void addVegetationInspector(C_InspectorVegetation inspector) {
		vegetationInspector = inspector;
	}
	public static void addOrnithodorosInspector(C_InspectorOrnithodorosSonrai inspector) {
		ornithodorosInspector = inspector;
	}
	public static void addBorreliaInspector(C_InspectorBorreliaCrocidurae inspector) {}
}
