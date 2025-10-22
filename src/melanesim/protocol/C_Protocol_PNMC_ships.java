package melanesim.protocol;

import data.C_Parameters;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.parameter.Parameters;
import thing.C_Ship_cargo;
import thing.dna.C_GenomeAnimalia;
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
		for (int i = 1; i < nbCargos; i++) {
			C_Ship_cargo cargo = createCargoShip();
			this.contextualizeNewThingInContainer(cargo, (I_Container) cargo.getTarget());
			cargo.setNewTarget();
		}
		System.out.println("C_Protocol_PNMC_ships.init: Population of " + nbCargos
				+ " cargo ship(s) created and positioned");
	}
}
