package melanesim.protocol.rodents;

import java.util.TreeSet;

import melanesim.protocol.A_Protocol;
import presentation.epiphyte.A_Inspector;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorPopulation;
import presentation.epiphyte.C_InspectorPopulationRodent;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import thing.A_NDS;
import thing.A_Organism;
import thing.A_VisibleAgent;
import thing.C_Rodent;
import thing.I_SituatedThing;
import thing.ground.I_Container;

/** author J.Le Fur, A.Comte 03.2012 / J.Le Fur 07.2012, 07.2013, 02.2014, 04.2020 */

public abstract class A_ProtocolRodent extends A_Protocol {
	//
	// FIELD
	//
	public static C_InspectorPopulationRodent inspector = null;
	//
	// CONSTRUCTOR
	//
	/** declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs. Author J.Le Fur 02.2013 */
	public A_ProtocolRodent(Context<Object> ctxt) {
		super(ctxt);// Init parameters, raster ground and higher level inspectors & displays
		A_ProtocolRodent.inspector = new C_InspectorPopulationRodent();
		this.inspectorList.add(inspector);}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Declare a new object in the context and positions it within the raster ground */
	public void contextualizeNewThingInContainer(I_SituatedThing thing, I_Container container) {
		if (!context.contains(thing)) {
			context.add(thing);
			this.landscape.moveToLocation(thing, container.getCoordinate_Ucs());
			container.agentIncoming(thing);
			if (thing instanceof A_VisibleAgent)
				((A_VisibleAgent) thing).bornCoord_Umeter = this.landscape.getThingCoord_Umeter(thing);
			if (thing instanceof A_Organism) ((A_Organism) thing).setMyHome(container);
		}
		else
			A_Protocol.event("A_Protocol.contextualizeNewAgentInCell", ((A_NDS) thing).retrieveMyName() + "/"
					+ ((A_NDS) thing).retrieveId() + " already exist in context", isError);
	}
	@Override
	protected void updateInspectors(I_SituatedThing thing) {
		if (thing instanceof C_Rodent) C_InspectorPopulationRodent.addRodentToList((C_Rodent) thing);
	}
	protected void updateInspectors(int nbDeadRodents) {
		A_ProtocolRodent.inspector.setNbDeath_Urodent(nbDeadRodents);}
	
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
}