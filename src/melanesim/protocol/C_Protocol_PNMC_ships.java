package melanesim.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import repast.simphony.context.Context;
import thing.A_Animal;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Megaptera;
import thing.C_Ship_cargo;
import thing.I_SituatedThing;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_SoilCellMarine;
import thing.ground.I_Container;

/** Various types of ships navigating within the landscape
 * @author J.Le Fur 10.2025 */
public class C_Protocol_PNMC_ships extends C_Protocol_PNMC_nekton {
	//
	// FIELD
	//
	protected TreeSet<C_Megaptera> whalesOutOfDomain = new TreeSet<C_Megaptera>();
	//
	// CONSTRUCTOR
	//
	public C_Protocol_PNMC_ships(Context<Object> ctxt) { super(ctxt); }
	//
	// SPECIFIC METHODS
	//
	public C_Ship_cargo createCargoShip() { return new C_Ship_cargo(new C_GenomeAnimalia()); }
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Specifically manage ships bouncing at the limits and whales entering or leaving domain */
	public void step_Utick() {
		I_Container currentCell;// temp variable
		List<A_NDS> agents = new ArrayList<>();
		for(Object obj:context.getObjects(A_NDS.class)) agents.add((A_NDS)obj);
		for(A_NDS oneAgent:agents){
			if(oneAgent instanceof C_Ship_cargo){
				currentCell = (C_SoilCellMarine)((A_VisibleAgent)oneAgent).getCurrentSoilCell();
				if(((C_SoilCellMarine)currentCell).isTerrestrial()){
					oneAgent.setDead(true);
					C_Ship_cargo newCargo = createCargoShip();
					this.contextualizeNewThingInContainer(newCargo,(I_Container)newCargo.getTarget());
					newCargo.setNewTarget();
				}
			}
			else if(oneAgent instanceof C_Megaptera){
				if(((C_Megaptera)oneAgent).hasLeftDomain && !this.whalesOutOfDomain.contains(oneAgent)){
					A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneAgent.toString()+" has left domain",
					        isNotError);
					currentCell = ((C_Megaptera)oneAgent).getCurrentSoilCell();
					currentCell.agentLeaving((I_SituatedThing)oneAgent);
					((C_Megaptera)oneAgent).discardCellTarget();
					((C_Megaptera)oneAgent).setMyHome(currentCell);
					this.context.remove(oneAgent);
					this.whalesOutOfDomain.add((C_Megaptera)oneAgent);
					for(A_Animal follower:((C_Megaptera)oneAgent).getAnimalsTargetingMe())
					    if(follower instanceof C_Megaptera && !this.whalesOutOfDomain.contains(oneAgent)){
						    currentCell = ((C_Megaptera)follower).getCurrentSoilCell();
						    if(currentCell==null){
							    @SuppressWarnings("unused")
							    int i = 1;
						    }
						    currentCell.agentLeaving((I_SituatedThing)follower);
						    ((C_Megaptera)oneAgent).discardCellTarget();
						    ((C_Megaptera)follower).setMyHome(currentCell);
						    this.context.remove(follower);
						    follower.hasEnteredDomain = false;
						    follower.hasLeftDomain = true;
						    this.whalesOutOfDomain.add((C_Megaptera)follower);
					    }
				}
			}
		}
		// Manage whales reentering the domain
		List<C_Megaptera> tmp = new ArrayList<C_Megaptera>();
		for(C_Megaptera obj:this.whalesOutOfDomain) tmp.add(obj);
		for(C_Megaptera oneWhale:tmp){
			oneWhale.manageActivities();// check if whale has reentered or left domain
			if(oneWhale.hasEnteredDomain){
				this.whalesOutOfDomain.remove(oneWhale);
				this.contextualizeNewThingInContainer(oneWhale,oneWhale.retrieveMyHome());
				A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneWhale.toString()+" has ENTERED domain",
				        isNotError);
				for(A_Animal follower:oneWhale.getAnimalsTargetingMe()) //
				    if((follower instanceof C_Megaptera) && this.whalesOutOfDomain.remove(follower)){
					    // this.context.add(follower);
					    follower.hasEnteredDomain = true;
					    follower.hasLeftDomain = false;
					    this.contextualizeNewThingInContainer(follower,follower.retrieveMyHome());
					    A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",follower.toString()+" has ENTERED domain",
					            isNotError);
				    }
				oneWhale.hasEnteredDomain = false;
				oneWhale.setEnergy_Ukcal(WHALE_ENERGY_Ukcal);
				oneWhale.manageActivities();
			}
		}
		super.step_Utick();
	}
	// @Override
	// /**Affiche les icones au step 2 pour éviter le bug de démarrage @author jlf 10.2025*/
	// public void step_Utick() {
	// super.step_Utick();
	// if (RepastEssentials.GetTickCount() == 2) {
	// Parameters params = RunEnvironment.getInstance().getParameters();
	// params.setValue("IMAGE", true);
	// C_Parameters.IMAGE = true;
	// }
	// }
	@Override
	/** Add cargo ship at any end of the main corridor - JLF 10.2025 */
	protected void initPopulations() {
		super.initPopulations();
		for(int i = 1; i<=CARGO_POPULATION; i++){
			C_Ship_cargo cargo = createCargoShip();
			this.contextualizeNewThingInContainer(cargo,(I_Container)cargo.getTarget());
			cargo.setNewTarget();
		}
		System.out.println("C_Protocol_PNMC_ships.init: Population of "+CARGO_POPULATION
		        +" cargo ship(s) created and positioned");
	}
}
