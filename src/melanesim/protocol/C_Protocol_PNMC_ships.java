package melanesim.protocol;

import repast.simphony.context.Context;
import thing.C_Ship_cargo;
import thing.dna.C_GenomeAnimalia;
import thing.ground.I_Container;

/** Various types of ships navigating within the landscape
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_ships extends C_Protocol_PNMC_nekton {
	//
	// CONSTRUCTOR
	//
	/** Author J.Le Fur 10.2025 */
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
	/** Add cargo ship at any end of the main corridor - JLF 10.2025 */
	protected void initPopulations() {
		super.initPopulations();
		int nbCargos = 12;
		for (int i = 1; i < nbCargos; i++) {
			C_Ship_cargo cargo = createCargoShip();
		this.contextualizeNewThingInContainer(cargo, (I_Container) cargo.getTarget());
		cargo.setNewTarget();
		}
		System.out.println("C_Protocol_PNMC_ships.init: Population of " + nbCargos + " cargo ship(s) created and positioned");
	}
}
