package melanesim.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
//import org.locationtech.jts.geom.Coordinate;
import data.C_Parameters;
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
		List<A_NDS> agents = new ArrayList<>();
		for(Object obj:context.getObjects(A_NDS.class)) agents.add((A_NDS)obj);
		for(A_NDS oneAgent:agents){
			// Manage ships bouncing World limits
			if(oneAgent instanceof C_Ship_cargo){
				if(((C_SoilCellMarine)((A_VisibleAgent)oneAgent).getCurrentSoilCell()).isTerrestrial()){
					oneAgent.setDead(true);
					C_Ship_cargo newCargo = createCargoShip();
					this.contextualizeNewThingInContainer(newCargo,(I_Container)newCargo.getTarget());
					newCargo.setNewTarget();
				}
			}
			// Manage whales leaving World
			else if(oneAgent instanceof C_Megaptera) checkWhaleLeavingWorld((C_Megaptera)oneAgent);
		}
		// Manage whales reentering World
		List<C_Megaptera> tmp = new ArrayList<C_Megaptera>();
		for(C_Megaptera obj:this.whalesOutOfDomain) tmp.add(obj);
		for(C_Megaptera oneWhale:tmp){
			oneWhale.manageActivities();// check if whale has reentered or left domain
			if(oneWhale.hasEnteredDomain){
				this.whalesOutOfDomain.remove(oneWhale);
				this.contextualizeNewThingInContainer(oneWhale,oneWhale.retrieveMyHome());
				if(C_Parameters.VERBOSE)
				    A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneWhale.toString()+" has ENTERED domain",
				            isNotError);
				for(A_Animal follower:oneWhale.getAnimalsTargetingMe()) //
				    if((follower instanceof C_Megaptera) && this.whalesOutOfDomain.remove(follower)){
					    // this.context.add(follower);
					    follower.hasEnteredDomain = true;
					    follower.hasLeftDomain = false;
					    this.contextualizeNewThingInContainer(follower,follower.retrieveMyHome());
					    if(C_Parameters.VERBOSE)
					        A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",follower.toString()
					                +" has ENTERED domain",isNotError);
				    }
				oneWhale.hasEnteredDomain = false;
				oneWhale.setEnergy_Ukcal(WHALE_ENERGY_Ukcal);
				oneWhale.manageActivities();
			}
		}
		super.step_Utick();
	}
	protected void checkWhaleLeavingWorld(C_Megaptera oneWhale) {
		if(oneWhale.hasLeftDomain && !this.whalesOutOfDomain.contains(oneWhale)){
			A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneWhale.toString()+" has left domain",isNotError);
			I_Container currentCell;// temp variable
			currentCell = oneWhale.getCurrentSoilCell();
			currentCell.agentLeaving((I_SituatedThing)oneWhale);
			if(oneWhale.getTarget() instanceof C_Megaptera) checkWhaleLeavingWorld((C_Megaptera)oneWhale.getTarget());
			oneWhale.discardCellTarget();
			oneWhale.setMyHome(currentCell);
			this.context.remove(oneWhale);
			this.whalesOutOfDomain.add((C_Megaptera)oneWhale);
			for(A_Animal follower:oneWhale.getAnimalsTargetingMe()) if(follower instanceof C_Megaptera && !follower
			        .isa_Tag())
			// && ((C_Megaptera)follower).getActivityList().isEmpty()){
			{
				follower.hasEnteredDomain = false;
				follower.hasLeftDomain = true;
				checkWhaleLeavingWorld((C_Megaptera)follower);
			}
		}
	}
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
