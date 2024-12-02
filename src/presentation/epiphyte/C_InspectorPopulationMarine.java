/* This source code is licensed under a BSD licence as detailed in file license */
package presentation.epiphyte;

import java.util.TreeSet;
import presentation.dataOutput.C_FileWriter;
import thing.A_Organism;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author JLF 2024 */
public class C_InspectorPopulationMarine extends C_InspectorPopulation {
	//
	// FIELDS
	//
	public static TreeSet<A_Organism> agentList = new TreeSet<A_Organism>();
	public static TreeSet<A_Organism> agentBirthList = new TreeSet<A_Organism>();
	//
	// CONSTRUCTOR
	//
	public C_InspectorPopulationMarine() {
		super();
		C_InspectorPopulationMarine.agentList.clear();
		C_InspectorPopulationMarine.agentBirthList.clear();
		this.indicatorsHeader = this.indicatorsHeader + ";PopSize;nbBirth;nbDeath";
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
}
