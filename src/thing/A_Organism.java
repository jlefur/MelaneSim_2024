/* This source code is licensed under a BSD licence as detailed in file license */
package thing;

import java.util.TreeSet;

import thing.dna.C_GenomeEucaryote;
import thing.dna.I_DiploidGenome;
import thing.ground.A_SupportedContainer;
import thing.ground.I_Container;

/** This class accounts for organisms with a genome
 * @see C_GenomeEucaryote
 * @author J. Le Fur 2016-03, 2021-04 */
public abstract class A_Organism extends A_SupportedContainer {
	//
	// FIELDS
	//
	protected I_DiploidGenome genome;
	protected I_Container myHome;// JLF 03.2021
	//
	// CONSTRUCTOR
	//
	public A_Organism(I_DiploidGenome genome) {
		this.genome = genome;
		this.setMyName(genome + NAMES_SEPARATOR + myId);
	}
	//
	// OVERRIDEN METHOD
	//
	/** Remove references to genome */
	@Override
	public void discardThis() {
		this.genome = null;
		super.discardThis();
	}
	//
	// SETTER and GETTERS
	//
	public void setMyHome(I_Container myHome) {
		this.myHome = myHome;
	}
	public String getMyHome() {
		return this.myHome.toString();
	}
	public I_DiploidGenome getGenome() {
		return this.genome;
	}
	/** For display purpose (organism' probe) / JLF 03.2018 */
	public String getAlleles() {
		if (this.genome == null || this.genome.getAlleles().isEmpty()) return "Dead organism";
		else return this.genome.getAlleles().toString();
	}
	/** For display purposes (organism' probe) / JLF 02.2019 */
	public int gretCell2Perception() {
		if (!this.isDead()) {
			TreeSet<I_SituatedThing> perception = this.perception();
			if (perception == null) return 0;// "NULL";
			else return perception.size();// + " / " + perception.toString();
		}
		else return 0;// "NULL";
	}
}
