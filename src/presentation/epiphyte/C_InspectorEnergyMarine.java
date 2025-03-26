package presentation.epiphyte;

import thing.ground.landscape.C_LandscapeMarine;

/** Retrieves mean energy for each species in the context J.Le Fur 02.2018 */
public class C_InspectorEnergyMarine extends C_InspectorEnergy {
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		this.EnergyBySpecies.put("MarineCell", C_LandscapeMarine.overallEnergyMean_Ukcal);
	}
}