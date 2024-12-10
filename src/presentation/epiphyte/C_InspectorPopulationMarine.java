/* This source code is licensed under a BSD licence as detailed in file license */
package presentation.epiphyte;

import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.engine.environment.RunState;
import thing.C_Plankton;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author JLF 2024 */
public class C_InspectorPopulationMarine extends A_Inspector {
	//
	// FIELDS
	//
	public static TreeSet<C_Plankton> planktonList = new TreeSet<C_Plankton>();
	public static int planktonExport = 0;
	public static double maxDispersal = 0., meanDispersal = 0.;
	//
	// CONSTRUCTOR
	//
	public C_InspectorPopulationMarine() {
		super();
		C_InspectorPopulationMarine.planktonList.clear();
		C_InspectorPopulationMarine.planktonExport = 0;
		this.indicatorsHeader = this.indicatorsHeader + ";PlanktonPopSize;pkExportImport";
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
	//
	// OVERRRIDEN METHOD
	//
	/** compute the number of agent within the context */
	@Override
	public void indicatorsCompute() {
		C_InspectorPopulationMarine.planktonList = new TreeSet<C_Plankton>();
		meanDispersal = 0.;
		maxDispersal = 0.;
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof C_Plankton) {
				C_InspectorPopulationMarine.planktonList.add((C_Plankton) item);
				// compute dispersals
				double dispersalDistance = ((C_Plankton) item).getMaxDispersalDistance_Umeter();
				if (dispersalDistance > maxDispersal) maxDispersal = dispersalDistance;
				meanDispersal += dispersalDistance;
			}
		}
		meanDispersal = meanDispersal / (double) C_InspectorPopulationMarine.planktonList.size();
	}

	@Override
	/** stores the current state of indicators in the field including the super ones / JLF 01.2013 */
	public String indicatorsStoreValues() {
		indicatorsValues = super.indicatorsStoreValues() + CSV_FIELD_SEPARATOR + planktonList.size()
				+ CSV_FIELD_SEPARATOR + C_InspectorPopulationMarine.planktonExport;
		return indicatorsValues;
	}
	@Override
	/** close private files */
	public void closeSimulation() {
		this.SpatialDistributionFile.closeFile();
	}
}
