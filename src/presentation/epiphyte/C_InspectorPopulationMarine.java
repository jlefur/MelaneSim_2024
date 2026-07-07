/* This source code is licensed under a BSD licence as detailed in file license */
package presentation.epiphyte;

import java.util.TreeSet;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.engine.environment.RunState;
import thing.C_Nekton;
import thing.C_Plankton;
import thing.ground.C_SoilCellMarine;
import thing.ground.landscape.C_Landscape;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author JLF 2024 */
public class C_InspectorPopulationMarine extends C_InspectorPopulation {
	//
	// FIELDS
	//
	public static TreeSet<C_Plankton> planktonList = new TreeSet<C_Plankton>();
	public static TreeSet<C_Nekton> nektonList = new TreeSet<C_Nekton>();
	public static int planktonExport = 0,nektonExport=0;
	public static double maxDispersalPlankton = 0., meanDispersalPlankton = 0., maxDispersalNekton = 0., meanDispersalNekton = 0.,maxEnergy_Ukcal = 0., meanEnergy_Ukcal = 0;
	//
	// CONSTRUCTOR
	//
	public C_InspectorPopulationMarine() {
		super();
		C_InspectorPopulationMarine.planktonList.clear();
		C_InspectorPopulationMarine.nektonList.clear();
		C_InspectorPopulationMarine.planktonExport = 0;
		C_InspectorPopulationMarine.nektonExport = 0;
		this.indicatorsHeader = this.indicatorsHeader + ";PlanktonPopSize;pkExportImport;NektonPopSize;nkExportImport";
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
	//
	// OVERRRIDEN METHOD
	//
	/** compute the number of agent within the context */
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		C_InspectorPopulationMarine.planktonList = new TreeSet<C_Plankton>();
		C_InspectorPopulationMarine.nektonList = new TreeSet<C_Nekton>();
		meanDispersalPlankton = 0.;
		maxDispersalPlankton = 0.;
		meanDispersalNekton = 0.;
		maxDispersalNekton = 0.;
		meanEnergy_Ukcal = 0.;
		maxEnergy_Ukcal = 0.;
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			 if (item instanceof C_Nekton) {
				C_InspectorPopulationMarine.nektonList.add((C_Nekton) item);
				// compute dispersals
				double dispersalDistance = ((C_Nekton) item).getMaxDispersalDistance_Umeter();
				if (dispersalDistance > maxDispersalNekton) maxDispersalNekton = dispersalDistance;
				meanDispersalNekton += dispersalDistance;
			}
			else if (item instanceof C_Plankton) {
				C_InspectorPopulationMarine.planktonList.add((C_Plankton) item);
				// compute dispersals
				double dispersalDistance = ((C_Plankton) item).getMaxDispersalDistance_Umeter();
				if (dispersalDistance > maxDispersalPlankton) maxDispersalPlankton = dispersalDistance;
				meanDispersalPlankton += dispersalDistance;
			}
			else if (item instanceof C_SoilCellMarine) {
				meanEnergy_Ukcal += ((C_SoilCellMarine) item).getEnergy_Ukcal();
				if (item instanceof C_SoilCellMarine cell)
					maxEnergy_Ukcal = Math.max(maxEnergy_Ukcal, cell.getEnergy_Ukcal());
			}
		}
		meanDispersalPlankton = meanDispersalPlankton / (double) C_InspectorPopulationMarine.planktonList.size();
		meanDispersalNekton = meanDispersalNekton / (double) C_InspectorPopulationMarine.nektonList.size();
		meanEnergy_Ukcal = meanEnergy_Ukcal / C_Landscape.nbCells;
	}

	@Override
	/** stores the current state of indicators in the field including the super ones / JLF 01.2013 */
	public String indicatorsStoreValues() {
		indicatorsValues = super.indicatorsStoreValues() + CSV_FIELD_SEPARATOR + planktonList.size()
				+ CSV_FIELD_SEPARATOR + C_InspectorPopulationMarine.planktonExport+ CSV_FIELD_SEPARATOR + nektonList.size()
				+ CSV_FIELD_SEPARATOR + C_InspectorPopulationMarine.nektonExport;
		return indicatorsValues;
	}
	@Override
	/** close private files */
	public void closeSimulation() {
		this.SpatialDistributionFile.closeFile();
	}
}
