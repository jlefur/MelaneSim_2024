package presentation.epiphyte;

import repast.simphony.engine.environment.RunState;
import thing.ground.C_SoilCellMarine;
import thing.ground.landscape.C_Landscape;

/** Retrieves mean energy for marine cells in the context J.Le Fur 04.2025 */
public class C_InspectorEnergyMarine extends C_InspectorEnergy {
	/** compute mean energy for each taxon within the context (compute somme and size then divide and replace EnergyBySpecies) */
	@Override
	public void indicatorsCompute() {
		super.indicatorsCompute();
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
//		String integralEnergyName = "Marine cell 3 months integral";
		String marineCellMeanEnergyName = "Marine cell";
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof C_SoilCellMarine) {

				/*
				 * // process Integral Energy name // If key exist, add values if (this.EnergyBySpecies.get(integralEnergyName) !=
				 * null) this.EnergyBySpecies.put(integralEnergyName, this.EnergyBySpecies.get(integralEnergyName) +
				 * ((C_SoilCellMarine) item).getIntegralEnergy_Ukcal()); // If not, create an entry and set values else
				 * this.EnergyBySpecies.put(integralEnergyName, ((C_SoilCellMarine) item).getIntegralEnergy_Ukcal());
				 */

				// Process meanEnergy name
				// If key exist, add values
				if (this.EnergyBySpecies.get(marineCellMeanEnergyName) != null)
					this.EnergyBySpecies.put(marineCellMeanEnergyName, this.EnergyBySpecies.get(
							marineCellMeanEnergyName) + ((C_SoilCellMarine) item).getEnergy_Ukcal());
				// If not, create an entry and set values
				else
					this.EnergyBySpecies.put(marineCellMeanEnergyName, ((C_SoilCellMarine) item).getEnergy_Ukcal());
			}
		}
		// Replace energy values with mean energyvalues
//		this.EnergyBySpecies.put(integralEnergyName, this.EnergyBySpecies.get(integralEnergyName)
//				/ C_Landscape.nbCells);
		this.EnergyBySpecies.put(marineCellMeanEnergyName, this.EnergyBySpecies.get(marineCellMeanEnergyName)
				/ C_Landscape.nbCells);
	}
}