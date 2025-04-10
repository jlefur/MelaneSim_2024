package presentation.epiphyte;

import repast.simphony.engine.environment.RunState;
import thing.ground.C_SoilCellMarine;

/** Retrieves mean energy for marine cells in the context J.Le Fur 04.2025 */
public class C_InspectorEnergyMarine extends C_InspectorEnergy {
	/** compute mean energy for each taxon within the context (compute somme and size then divide and replace EnergyBySpecies) */
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		String speciesName = "Marine cell 3 months integral";
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof C_SoilCellMarine) {
				// If key exist, add values
				if (this.EnergyBySpecies.get(speciesName) != null) {
					this.EnergyBySpecies.put(speciesName, this.EnergyBySpecies.get(speciesName)
							+ ((C_SoilCellMarine) item).getIntegralEnergy_Ukcal());
					this.sizeBySpecies.put(speciesName, this.sizeBySpecies.get(speciesName) + 1);
				}
				// If not, create an entry and set values
				else {
					this.EnergyBySpecies.put(speciesName, ((C_SoilCellMarine) item).getIntegralEnergy_Ukcal());
					this.sizeBySpecies.put(speciesName, 1);
				}
			}
		}
		// Replace energy values with mean energyvalues
		this.EnergyBySpecies.put(speciesName, this.EnergyBySpecies.get(speciesName) / this.sizeBySpecies.get(
				speciesName));
	}
}
/*
 * public void indicatorsCompute() { super.indicatorsCompute(); this.EnergyBySpecies.put("MarineCell",
 * C_LandscapeMarine.overallEnergyMean_Ukcal); }
 */