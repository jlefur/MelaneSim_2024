/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground;

import org.locationtech.jts.geom.Coordinate;

import melanesim.C_ContextCreator;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.I_SituatedThing;

/** Container that can be put within a C_SoilCell
 * @author J.Le Fur, 03.2015, rev. 09.2015 */
public abstract class A_SupportedContainer extends C_SoilCell {
	//
	// FIELD
	//
	protected boolean a_tag = false;
	//
	// CONSTRUCTORS
	//
	public A_SupportedContainer() {}
	public A_SupportedContainer(int affinity, int lineNo, int colNo) {
		super(affinity, lineNo, colNo);
	}
	//
	// METHODS
	//
	/** Disperse animal if container is full TODO JLF 2016.01 should be valid for any I_container ? <br>
	 * rev M.S 2017.02, JLF 03.2021 remove target container of thing after remove it in animalsTargetingMe */
	@Override
	public boolean agentIncoming(I_SituatedThing thing) {
		boolean test = super.agentIncoming(thing);
		if (test && (thing instanceof A_Animal)) {
			A_Animal oneAnimal = (A_Animal) thing;
			if (oneAnimal.getTarget() == this) oneAnimal.discardTarget();
			if (!oneAnimal.getDesire().equals(NONE)) oneAnimal.setHasToLeaveFullContainer(this.isFull());
		}
		return test;
	}
	@Override
	public String toString() {
		return this.retrieveMyName();// + "(" + this.currentSoilCell.getLineNo() + "," + this.currentSoilCell.getColNo() + ")";
	}
	@Override
	public Coordinate getCoordinate_Ucs() {
		return A_VisibleAgent.myLandscape.getThingCoord_Ucs(this);
	}
	/** JLF 03.2021 */
	@Override
	public C_LandPlot getMyLandPlot() {
		return ((C_SoilCell) this.currentSoilCell).getMyLandPlot();
	}
	@Override
	/** set Dead if random number is lower than death probability passed in arg <br>
	 * same as A_NDS since C_Soilcell do not check death
	 * @see A_NDS#checkDeath */
	public void checkDeath(double deathProbability) {
		if (C_ContextCreator.randomGeneratorForDeathProb.nextDouble() <= deathProbability) this.dead = true;
	}
	//
	// GETTER AND SETTER
	//
	public boolean isa_Tag() {
		return a_tag;
	}
	public void seta_Tag(boolean tagged) {
		this.a_tag = tagged;
		this.setHasToSwitchFace(true);
	}
}