package melanesim.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Megaptera;
import thing.C_Ship_cargo;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_LandPlot;
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
	public void step_Utick() {
		List<C_Megaptera> tmp = new ArrayList<C_Megaptera>();
		for(C_Megaptera obj:this.whalesOutOfDomain) tmp.add(obj);
		for(C_Megaptera oneWhale:tmp){
			oneWhale.manageActivities();
			if(oneWhale.hasEnteredDomain){
				this.context.add(oneWhale);
				this.whalesOutOfDomain.remove(oneWhale);
				A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()","whale "+oneWhale.toString()
				        +" has entered domain",isNotError);
				oneWhale.manageActivities();
			}
		}

		List<A_NDS> agents = new ArrayList<>();
		for(Object obj:context.getObjects(A_NDS.class)) agents.add((A_NDS)obj);
		for(A_NDS oneAgent:agents){
			if(oneAgent instanceof C_Ship_cargo){
				C_SoilCellMarine currentCell = (C_SoilCellMarine)((A_VisibleAgent)oneAgent).getCurrentSoilCell();
				if(currentCell.isTerrestrial()){
					oneAgent.setDead(true);
					C_Ship_cargo newCargo = createCargoShip();
					this.contextualizeNewThingInContainer(newCargo,(I_Container)newCargo.getTarget());
					newCargo.setNewTarget();
				}
			}
			else if(oneAgent instanceof C_Megaptera){
				if(((C_Megaptera)oneAgent).hasLeftDomain){
					A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()","whale "+oneAgent.toString()
					        +" has left domain",isNotError);
					this.context.remove(oneAgent);
					this.whalesOutOfDomain.add((C_Megaptera)oneAgent);
				}
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
