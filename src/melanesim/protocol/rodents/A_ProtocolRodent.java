package melanesim.protocol.rodents;

import java.awt.Dimension;
import java.util.TreeSet;

import melanesim.C_ContextCreator;
import melanesim.protocol.A_Protocol;
import presentation.epiphyte.C_InspectorPopulationRodent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.dna.C_GenomeAmniota;

/** author J.Le Fur, A.Comte 03.2012 / J.Le Fur 07.2012, 07.2013, 02.2014, 04.2020 */

public abstract class A_ProtocolRodent extends A_Protocol {
	//
	// FIELD
	//
	public static C_InspectorPopulationRodent inspector = null;
	//
	// CONSTRUCTOR
	//
	/** declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs. Author
	 * J.Le Fur 02.2013 */
	public A_ProtocolRodent(Context<Object> ctxt) {
		super(ctxt);// Init parameters, raster ground and higher level inspectors & displays
		A_ProtocolRodent.inspector = new C_InspectorPopulationRodent();
		this.inspectorList.add(inspector);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	protected int removeDeadThings() {
		Object[] things = this.context.toArray();// needed to avoid concurrent modification exception
		int nbDeadRodents = 0;
		for (Object oneThing : things) {
			if ((oneThing instanceof C_Rodent) && ((C_Rodent) oneThing).isDead()) nbDeadRodents++;
		}
		updateInspectors(nbDeadRodents);
		return super.removeDeadThings();
	}
	@Override
	protected void updateInspectors(I_SituatedThing thing) {
		if (thing instanceof C_Rodent) C_InspectorPopulationRodent.addRodentToList((C_Rodent) thing);
	}
	@Override
	protected void updateInspectors(int nbDeadRodents) {
		A_ProtocolRodent.inspector.setNbDeath_Urodent(nbDeadRodents);
	}
	//
	// METHODS
	//
	/** Check rodent list sizes */
	public void checkRodentLists() {
		TreeSet<C_Rodent> rodentList = C_InspectorPopulationRodent.rodentList;
		int withinContext_Urodent = 0, withinSoilMatrix_Urodent = 0, trapped_Urodent = 0;
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		int contextSize = contextContent.length;
		for (int i = 0; i < contextSize; i++) {
			if (contextContent[i] instanceof C_Rodent) {
				withinContext_Urodent++;
				if (((C_Rodent) contextContent[i]).isTrappedOnBoard()) trapped_Urodent++;
			}
		}
		for (int i = 0; i < this.landscape.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.getHeight(); j++) {
				if (!this.landscape.getGrid()[i][j].getFullRodentList().isEmpty())
					withinSoilMatrix_Urodent += this.landscape.getGrid()[i][j].getFullRodentList().size();
			}
		}
		withinSoilMatrix_Urodent += trapped_Urodent;
		if ((withinContext_Urodent != rodentList.size()) || (withinSoilMatrix_Urodent != rodentList.size())) {
			A_Protocol.event("A_Protocol.checkRodentLists", "List sizes differ: rodentList/context/soilMatrix(trapped)"
					+ rodentList.size() + "/" + withinContext_Urodent + "/" + withinSoilMatrix_Urodent + " ("
					+ trapped_Urodent + ")", isError);
		}
	}
	public C_Rodent createRodent() {
		return new C_Rodent(new C_GenomeAmniota());
	}
	/** Fills the context with simple _wandering_ C_Rodent agents (as opposed to C_RodentFossorial's that dig burrows) <br>
	 * The sex ratio is randomly generated , rev. JLF 07.2014 currently unused */
	public void randomlyAddRodents(int nbAgent) {
		Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		for (int i = 0; i < nbAgent; i++) {
			// BELOW, THREE POSSIBLE PATTERNS OF INITIAL DISTRIBUTION :
			// 1) Random number to produce a sensitivity analysis
			// int randx = (int)(Math.random()*grid_width);
			// int randy = (int)(Math.random()*grid_height);
			// 2) Reproducible random distribution
			double randx = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_width;
			double randy = C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * grid_height;
			// 3) Put all rodents at the middle at init:
			// int randx = (int) (grid_width / 2);
			// int randy = (int) (grid_height / 2);
			C_Rodent agent = createRodent();
			agent.setRandomAge();
			contextualizeNewThingInSpace(agent, randx, randy);
			agent.setNewRandomMove();
		}
	}
}