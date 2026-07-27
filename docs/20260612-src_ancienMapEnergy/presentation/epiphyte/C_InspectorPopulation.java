/* This source code is licensed under a BSD licence as detailed in file license */
package presentation.epiphyte;
import java.util.HashMap;
import java.util.Map;

import melanesim.util.C_VariousUtilities;
import repast.simphony.engine.environment.RunState;
import thing.A_NDS;

/** Data inspector: retrieves informations e.g. population sizes and manages lists.
 * @author A Realini 05.2011 / J.Le Fur 2011-2013, 2024, 2025 */
public class C_InspectorPopulation extends A_Inspector {
	//
	// FIELD
	//
	public static Map<String, Double> populationBySpecies = new HashMap<String, Double>();// the map [NDS type, size]
	//
	// METHODS
	//
	/** compute the number of agent within the context */
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		String speciesName = "";
		C_InspectorPopulation.populationBySpecies.clear();
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof A_NDS) {
				speciesName = C_VariousUtilities.getShortClassName(item.getClass()).substring(2);
				// If key exist, add values
				if (C_InspectorPopulation.populationBySpecies.get(speciesName) != null) {
					C_InspectorPopulation.populationBySpecies.put(speciesName, C_InspectorPopulation.populationBySpecies
							.get(speciesName) + 1);
				}
				// If not, create an entry and set values
				else C_InspectorPopulation.populationBySpecies.put(speciesName, 1.);
			}
		}
	}
	//
	// GETTER
	//
	public static Map<String, Double> getPopulationBySpecies() {
		return populationBySpecies;
	}
}
