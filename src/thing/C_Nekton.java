package thing;

import data.constants.I_ConstantPNMC;
import thing.dna.I_DiploidGenome;
import thing.ground.C_SoilCellMarine;

/** micronekton particle are not submitted to surface current half of day */
public class C_Nekton extends C_Plankton implements I_ConstantPNMC{
	//
	// CONSTRUCTOR
	//
	public C_Nekton(I_DiploidGenome genome) {
		super(genome);
		this.setMyName("micro-Nekton" + NAMES_SEPARATOR + myId);
	}
	//
	// OVERRIDEN METHOD
	//
	/** for probe display purpose */
	@Override
	public double getSpeedEast() {
		return super.getSpeedEast() / NEKTON_RESISTANCE_FACTOR;
	}

	/** for probe display purpose */
	@Override
	public double getSpeedNorth() {
		return super.getSpeedNorth() / NEKTON_RESISTANCE_FACTOR;
	}
	//
	// SPECIFIC METHOD
	//
	/** to display on GUI @see thing.C_Nekton.style.xml */
	public double getSize() {
		C_SoilCellMarine mycell = (C_SoilCellMarine) this.currentSoilCell;
		return mycell.getTotalNektonDensity()/2.;
	}
}
