/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.rodents;

import data.C_Parameters;
import melanesim.protocol.A_Protocol;
import thing.A_Amniote;
import thing.A_Animal;
import thing.dna.C_GenomeAnimalia;
import thing.dna.I_DiploidGenome;

/** @author JEL & AR, rev. J.Le Fur 2012-2013-2014 */
public class C_Rodent extends A_Amniote {
	//
	// CONSTRUCTOR
	//
	public C_Rodent(I_DiploidGenome genome) {
		super(genome);
	}
	//
	// METHODS
	//
	/** generate a new animal : compulsory for every A_Mammal daughter class */
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) {
		if (C_Parameters.VERBOSE)
			A_Protocol.event("C_Rodent.giveBirth", "Birth at " + this.currentSoilCell + " (" + this.currentSoilCell
					.retrieveLoad_Urodent() + ")", false);
		return new C_Rodent(genome);
	}
	//
	// GETTERS
	//

	/** Retrieves death probability
	 * @return double value */
	@Override
	protected double computeDeathProbability_Uday() {// TODO JLF 2014.01 clean this
		return getDeathProbabilityMicrotusArvalis_Uday() / 2.;// TODO number in source JLF 2017.12 mortality tuner;

	}
	@Override
	/** JLF 03.2021 */
	public int getCarryingCapacity_Urodent() {
		return 0;
	}
	/** Retrieves death probability from a table
	 * @return double value */
	protected double getDeathProbabilityMicrotusArvalis_Uday() { // TODO JLF 2014.01 encapsulate with a call with arg=genome & age
		double deathProb = 0.;
		double[][] mortalityTable_Uday = {//
				{0, 30, 60, 90, 120, 150, 180, 210, 240, 270, ((C_GenomeAnimalia) this.genome).getMaxAge_Uday()}, //
				{0.011, 0.010, 0.012, 0.012, 0.013, 0.018, 0.022, 0.025, 0.028, 0.030, .04}};// mortality table from Spitz
																								// excepted last value
																								// extrapolated
		if (getAge_Uday() >= 0) {
			int n = mortalityTable_Uday[0].length - 1, i;
			for (i = 0; i < n; i++) {
				if (getAge_Uday() >= mortalityTable_Uday[0][i] && getAge_Uday() < mortalityTable_Uday[0][i + 1]) {
					deathProb = mortalityTable_Uday[1][i];
					break;
				}
			}
			if (getAge_Uday() >= mortalityTable_Uday[0][n] || getAge_Uday() >= ((C_GenomeAnimalia) this.genome)
					.getMaxAge_Uday()) deathProb = .01;
		}
		return deathProb;
	}
}
