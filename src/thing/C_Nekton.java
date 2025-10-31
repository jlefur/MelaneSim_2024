package thing;

import thing.dna.I_DiploidGenome;

/** micronekton particle are not submitted to surface current half of day */
public class C_Nekton extends C_Plankton {
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
		return super.getSpeedEast() / 2.;
	}

	/** for probe display purpose */
	@Override
	public double getSpeedNorth() {
		return super.getSpeedNorth() / 2.;
	}

}
