package presentation.display;

import java.awt.BasicStroke;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.jfree.chart.plot.XYPlot;

import melanesim.protocol.A_Protocol;
//import presentation.epiphyte.C_InspectorPopulation;
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

	protected static C_InspectorEnergy energyInspector = null;
	protected static C_InspectorPopulation populationInspector = null;
	protected Map<String, Double> energyMap = null;// used for energy graph
	protected Map<String, Double> populationMap = null;// used for population graph
	protected Map<String, Double> dispersalMap = null;// used for dispersal graph
	public static ArrayList<String> energyCurves = new ArrayList<String>();// used for energy graph (must be static to be invoked
																			// and cleared in
	public static ArrayList<String> populationCurves = new ArrayList<String>();// used for energy graph (must be static to be
																				// invoked and cleared in
	protected IAction action = this; // Récupère l'implémentation de IDisplay
	protected C_CustomPanelFactory curveEnergy, curvePopSize, curveDispersal;
	/** This is ran after the model has been loaded. This is only ran once, but the settings set through the
	 * {@link repast.simphony.scenario.Scenario} will apply to every run of the simulation.
	 * @param scen the {@link repast.simphony.scenario.Scenario} object that hold settings for the run */
	public void initialize(Scenario scen, RunEnvironmentBuilder builder) {
		scen.addMasterControllerAction(new NullAbstractControllerAction<Object>() {
			/** Ajoute des onglets à la simulation et les initialise */
			public void runInitialize(RunState runState, Context<?> context, Parameters runParams) {
				/*
				 * Initialisation des onglets: On crée un nouveau display puis on l'ajoute au registre des GUI de RunState. On
				 * recommence ces deux étapes autant de fois que l'on souhaite ajouter d'onglets.
				 */
				XYPlot plot;

				// ENERGY JLF 02.2021
				if (A_Protocol.inspectorEnergy != null) {
					energyMap = A_Protocol.inspectorEnergy.getEnergyBySpecies();
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
				populationMap = C_InspectorPopulation.populationBySpecies;
				curvePopSize = new C_CustomPanelFactory("Populations sizes", C_Chart.LINE, "Ticks", "Population size");
				runState.getGUIRegistry().addDisplay("Populations sizes", GUIRegistryType.OTHER, curvePopSize);

				// DISPERSAL
				curveDispersal = new C_CustomPanelFactory("Dispersals", C_Chart.LINE, "Ticks", "Dispersal (m)");
				runState.getGUIRegistry().addDisplay("Dispersals", GUIRegistryType.OTHER, curveDispersal);

				initWorldSpecificPlots(runState);

				/** Ajoute SimMastoInitializer au registre des plannings pour que la fonction execute() soit appel�e �
				 * l'intervalle demandé. La dernière variable ne peut pas être this à cause de la double implmentation. C'est
				 * pourquoi on utilise une variable action initialis�e avec this<br>
				 * The action with greater value is activated first */
				runState.getScheduleRegistry().getModelSchedule().schedule(ScheduleParameters.createRepeating(0, 1, 1),
						action);
			}

			/** Ferme les flux de la console du user panel (exécuté lorsqu'on réinitialise une simulation sans couper l'exécution
			 * du programme) */
			public void runCleanup(RunState runState, Context<?> context) {
				C_CustomPanelSet.energyCurves.clear();
				C_CustomPanelSet.populationCurves.clear();
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
			@Override
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
		energyInspector = A_Protocol.inspectorEnergy;
		populationInspector = A_Protocol.inspectorPopulation;
		// ENERGY jlf 02.2021
		if (A_Protocol.inspectorEnergy != null) {
			this.energyMap = A_Protocol.inspectorEnergy.getEnergyBySpecies();
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
		// POPULATION jlf 2025
		if (A_Protocol.inspectorPopulation != null) {
			this.populationMap = C_InspectorPopulation.getPopulationBySpecies();
			// Provide an energy curve for each type or organism
			for (String key : this.populationMap.keySet()) {
				if (!key.contains("SoilCell")) {
					// if species not yet registered, create the XYSerie
					if (!C_CustomPanelSet.populationCurves.contains(key)) {
						this.curvePopSize.getChart().addXYSerie(key);
						C_CustomPanelSet.populationCurves.add(key);
					}
					// if (!key.equals("C_GenomeGerbillusNigeriae"))
					curvePopSize.getChart().setData(key, RepastEssentials.GetTickCount(), this.populationMap.get(key));
				}
			}
		}
	}
}
