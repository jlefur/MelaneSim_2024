package presentation.display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.plot.XYPlot;

import melanesim.protocol.A_Protocol;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorPopulation;
import repast.simphony.context.Context;
import repast.simphony.engine.controller.NullAbstractControllerAction;
import repast.simphony.engine.environment.GUIRegistryType;
import repast.simphony.engine.environment.RunEnvironmentBuilder;
import repast.simphony.engine.environment.RunState;
import repast.simphony.engine.schedule.IAction;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.parameter.Parameters;
import repast.simphony.scenario.ModelInitializer;
import repast.simphony.scenario.Scenario;

/** Initialise la simulation avec des onglets "programmables"
 * @author A. Realini, rev. J. Le Fur 02.2013, O2.2017, 02.2021 */
public class C_CustomPanelSet implements IAction, ModelInitializer {

	protected Map<String, Double> energyMap = null;// used for energy graph
	public static ArrayList<String> energyCurves = new ArrayList<String>();// used for energy graph (must be static to be invoked
																			// and cleared in
	protected IAction action = this; // R�cup�re l'impl�mentation de IDisplay
	protected C_CustomPanelFactory curveEnergy, curvePopSize, curveDispersal;
	// the following are meant to avoid multiple calls to ContextCreator (see execute()) // JLF 02.2013
	protected C_InspectorPopulation inspector;
	protected static C_InspectorEnergy inspectorEnergy = null;
	/** This is ran after the model has been loaded. This is only ran once, but the settings set through the
	 * {@link repast.simphony.scenario.Scenario} will apply to every run of the simulation.
	 * @param scen the {@link repast.simphony.scenario.Scenario} object that hold settings for the run */
	public void initialize(Scenario scen, RunEnvironmentBuilder builder) {
		scen.addMasterControllerAction(new NullAbstractControllerAction<Object>() {
			/** Ajoute des onglets � la simulation et les initialise */
			public void runInitialize(RunState runState, Context<?> context, Parameters runParams) {
				/*
				 * Initialisation des onglets: On cr�e un nouveau display puis on l'ajoute au registre des GUI de RunState. On
				 * recommence ces deux �tapes autant de fois que l'on souhaite ajouter d'onglets.
				 */
				XYPlot plot;

				// ENERGY JLF 02.2021
				if (inspectorEnergy != null) {
					energyMap = inspectorEnergy.getEnergyBySpecies();
					curveEnergy = new C_CustomPanelFactory("Energy (mean)", C_Chart.LINE, "Tick",
							"mean energy (pseudo-kcal)");
					runState.getGUIRegistry().addDisplay("Energy", GUIRegistryType.OTHER, curveEnergy);
					plot = curveEnergy.getChart().getChartPanel().getChart().getXYPlot();
					// for (int i = 0; i < energyMap.size(); i++)
					// plot.getRenderer().setSeriesPaint(i, Color.PINK);
					plot.getRenderer().setSeriesStroke(3, new BasicStroke(.5f, BasicStroke.CAP_ROUND,
							BasicStroke.JOIN_ROUND, 1.0f, new float[]{10.0f, 6.0f}, 0.0f));
				}

				// POPULATION SIZES
				curvePopSize = new C_CustomPanelFactory("Populations sizes", C_Chart.LINE, "Ticks", "Population size");
				runState.getGUIRegistry().addDisplay("Rodent Pop. sizes", GUIRegistryType.OTHER, curvePopSize);
				curvePopSize.getChart().addXYSerie("PopMales");
				curvePopSize.getChart().addXYSerie("PopFemales");
				curvePopSize.getChart().addXYSerie("WanderingRodents");
				curvePopSize.getChart().addXYSerie("LoadedRodents(x10)");
				plot = curvePopSize.getChart().getChartPanel().getChart().getXYPlot();
				plot.getRenderer().setSeriesPaint(0, Color.BLUE);
				plot.getRenderer().setSeriesPaint(1, Color.RED);
				plot.getRenderer().setSeriesPaint(2, Color.GREEN);
				plot.getRenderer().setSeriesPaint(3, Color.BLACK);
				plot.getRenderer().setSeriesPaint(4, Color.MAGENTA);
				plot.getRenderer().setSeriesPaint(5, Color.CYAN);
				plot.getRenderer().setSeriesPaint(6, Color.ORANGE);
				plot.getRenderer().setSeriesPaint(7, Color.GREEN);
				plot.getRenderer().setSeriesPaint(8, Color.darkGray);
				plot.getRenderer().setSeriesPaint(9, Color.LIGHT_GRAY);

				// DISPERSAL
				curveDispersal = new C_CustomPanelFactory("dispersals (home range size)", C_Chart.LINE, "Ticks",
						"Dispersal (m)");
				runState.getGUIRegistry().addDisplay("dispersals (home range size)", GUIRegistryType.OTHER,
						curveDispersal);
				curveDispersal.getChart().addXYSerie("MaxFemaleDispersal");
				curveDispersal.getChart().addXYSerie("MaxMaleDispersal");
				curveDispersal.getChart().addXYSerie("MeanFemaleDispersal");
				curveDispersal.getChart().addXYSerie("MeanMaleDispersal");
				plot = curveDispersal.getChart().getChartPanel().getChart().getXYPlot();
				plot.getRenderer().setSeriesPaint(0, Color.PINK);
				plot.getRenderer().setSeriesPaint(1, Color.CYAN);
				plot.getRenderer().setSeriesPaint(2, Color.MAGENTA);
				plot.getRenderer().setSeriesPaint(3, Color.BLUE);
				plot.getRenderer().setSeriesPaint(4, Color.DARK_GRAY);
				plot.getRenderer().setSeriesPaint(5, Color.GREEN);

				initWorldSpecificPlots(runState);

				/** Ajoute SimMastoInitializer au registre des plannings pour que la fonction execute() soit appel�e �
				 * l'intervalle demand�. La derni�re variable ne peut pas �tre this � cause de la double impl�mentation. C'est
				 * pourquoi on utilise une variable action initialis�e avec this<br>
				 * The action with greater value is activated first */
				runState.getScheduleRegistry().getModelSchedule().schedule(ScheduleParameters.createRepeating(0, 1, 1),
						action);
			}

			/** Ferme les flux de la console du user panel (ex�cut� lorsque l'on r�initialise une simulation sans couper
			 * l'ex�cution du programme) */
			public void runCleanup(RunState runState, Context<?> context) {
				C_CustomPanelSet.energyCurves.clear();
				// La console n'existe pas en batch car C_TableauDeBord n'est pas initialis�
				if (C_UserPanel.consoleOut != null) {
					System.out.println("SimMastoInitializer.runCleanup : closing flow to dismiss display");
					try {
						C_UserPanel.consoleOut.getIn().close();
						C_UserPanel.consoleErr.getIn().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					C_UserPanel.consoleOut.getOut().close();
					C_UserPanel.consoleErr.getOut().close();
				}
			}

			public String toString() {
				return "PanelSetInitializer";
			}
		});
	}
	protected void initWorldSpecificPlots(RunState runstate) {}

	/** Use it for no graphs in the GUI */
	public void execute0() {}

	/** Update each series with the corresponding data */
	public void execute() {
		inspector = A_Protocol.inspector;
		inspectorEnergy = A_Protocol.inspectorEnergy;

		// POPULATION DISPLAY
		curvePopSize.getChart().setData("PopMales", RepastEssentials.GetTickCount(), C_InspectorPopulation
				.getNbMales());
		curvePopSize.getChart().setData("PopFemales", RepastEssentials.GetTickCount(), C_InspectorPopulation
				.getNbFemales());

		// DISPERSAL DISPLAY
		curveDispersal.getChart().setData("MaxFemaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMaxFemaleDispersal());
		curveDispersal.getChart().setData("MaxMaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMaxMaleDispersal());
		curveDispersal.getChart().setData("MeanMaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMeanMaleDispersal());
		curveDispersal.getChart().setData("MeanFemaleDispersal", RepastEssentials.GetTickCount(), inspector
				.getMeanFemaleDispersal());

		// ENERGY jlf 02.2021
		if (C_CustomPanelSet.inspectorEnergy != null) {
			this.energyMap = C_CustomPanelSet.inspectorEnergy.getEnergyBySpecies();
			// Provide an energy curve for each type or organism
			for (String key : this.energyMap.keySet()) {
				// if species not yet registered, create the XYSerie
				if (!C_CustomPanelSet.energyCurves.contains(key)) {
					this.curveEnergy.getChart().addXYSerie(key);
					C_CustomPanelSet.energyCurves.add(key);
				}
				// if (!key.equals("C_GenomeGerbillusNigeriae"))
				curveEnergy.getChart().setData(key, RepastEssentials.GetTickCount(), this.energyMap.get(key));
			}
		}
	}
	public static void addEnergyInspector(C_InspectorEnergy inspector) {
		inspectorEnergy = inspector;
	}
}
