package presentation.display;

import java.awt.Color;

import org.jfree.chart.plot.XYPlot;

import presentation.epiphyte.C_InspectorPopulationMarine;
import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;

/** Initialise la simulation avec des onglets "programmables"
 * @author JLF 2024 */
public class C_CustomPanelSetMarine extends C_CustomPanelSet {
	//
	// OVERRIDEN METHODS
	//
	@Override
	protected void initWorldSpecificPlots(RunState runState) {
		XYPlot plot;
		// POPULATIONS SIZES
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
		//curvePopSize.getChart().addXYSerie("Plankton ExportImport");
		
		plot = curveEnergy.getChart().getChartPanel().getChart().getXYPlot();
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
		
		curveDispersal.getChart().addXYSerie("Plankton Mean Dispersal");
		curveDispersal.getChart().addXYSerie("Plankton Max Dispersal");
		plot = curveDispersal.getChart().getChartPanel().getChart().getXYPlot();
		plot.getRenderer().setSeriesPaint(0, Color.PINK);
		plot.getRenderer().setSeriesPaint(1, Color.CYAN);
	}
	@Override
	/** Use it for no graphs in the GUI */
	public void execute0() {}

	@Override
	/** Update each series with the corresponding data */
	public void execute() {
		super.execute();
//		curvePopSize.getChart().setData("Plankton ExportImport", RepastEssentials.GetTickCount(),
//				C_InspectorPopulationMarine.planktonExport);
		curveDispersal.getChart().setData("Plankton Mean Dispersal", RepastEssentials.GetTickCount(),
				C_InspectorPopulationMarine.meanDispersalPlankton);
		curveDispersal.getChart().setData("Plankton Max Dispersal", RepastEssentials.GetTickCount(),
				C_InspectorPopulationMarine.maxDispersalPlankton);
	}
}
