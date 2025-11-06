package melanesim.protocol;

import repast.simphony.context.Context;
import repast.simphony.util.collections.IndexedIterable;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Ship_cargo;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_SoilCellMarine;
import thing.ground.I_Container;

/** Various types of ships navigating within the landscape
 * @author J.Le Fur 10.2025 */
public class C_Protocol_PNMC_ships extends C_Protocol_PNMC_nekton {
	//
	// CONSTRUCTOR
	//
	public C_Protocol_PNMC_ships(Context<Object> ctxt) {
		super(ctxt);
	}
	//
	// SPECIFIC METHODS
	//
	public C_Ship_cargo createCargoShip() {
		return new C_Ship_cargo(new C_GenomeAnimalia());
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	// /**Affiche les icones au step 2 pour éviter le bug de démarrage @author jlf 10.2025*/
	public void step_Utick() {
		A_NDS oneAgent;
		IndexedIterable<Object> it = this.context.getObjects(A_NDS.class);
		for (int i = 0; i < it.size(); i++) {
			oneAgent = (A_NDS) it.get(i);
			if (oneAgent instanceof C_Ship_cargo) {
				C_SoilCellMarine currentCell = (C_SoilCellMarine) ((A_VisibleAgent) oneAgent).getCurrentSoilCell();
				if (currentCell.isTerrestrial()) {
					oneAgent.setDead(true);
					C_Ship_cargo newCargo = createCargoShip();
					this.contextualizeNewThingInContainer(newCargo,  (I_Container) newCargo.getTarget());
					newCargo.setNewTarget();
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
		int nbCargos = 12;// TODO number in source 2025 nombre de cargos
		for (int i = 1; i <= CARGO_POPULATION; i++) {
			C_Ship_cargo cargo = createCargoShip();
			this.contextualizeNewThingInContainer(cargo, (I_Container) cargo.getTarget());
			cargo.setNewTarget();
		}
		System.out.println("C_Protocol_PNMC_ships.init: Population of " + nbCargos
				+ " cargo ship(s) created and positioned");
	}
}
