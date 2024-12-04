/* This source code is licensed under a BSD licence as detailed in file license */
package presentation.epiphyte;

import presentation.dataOutput.C_FileWriter;
import repast.simphony.engine.environment.RunState;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author A Realini 05.2011 / J.Le Fur 2011-2013, 2024 */
public class C_InspectorPopulation extends A_Inspector {
	//
	// FIELDS
	//
	protected C_FileWriter SpatialDistributionFile;
	public static int agentPopulation = 0;
	//
	// CONSTRUCTOR
	//
	public C_InspectorPopulation() {
		super();
		this.indicatorsHeader = this.indicatorsHeader + ";PopSize;EnergySum";
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
	//
	// METHODS
	//
	/** compute the number of agent within the context */
	@Override
	public void indicatorsCompute() {
		C_InspectorPopulation.agentPopulation = RunState.getInstance().getMasterContext().size();
		/*
		 * C_InspectorPopulation.agentPopulation = 0; Object[] contextContent =
		 * RunState.getInstance().getMasterContext().toArray(); for (int i = 0; i < contextContent.length; i++) { Object item =
		 * contextContent[i]; if (item instanceof A_SupportedContainer) C_InspectorPopulation.agentPopulation++; }
		 */
	}
}
