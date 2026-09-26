package melanesim.protocol;

import repast.simphony.context.Context;
import thing.C_Ship_astar;
import thing.dna.C_GenomeAnimalia;

/** First step toward A* powered ship navigation: on top of the regular ships activities (with the cargo
 * population trimmed down to keep the scene readable), adds one boat that travels directly (straight line)
 * from a fixed point A to a fixed point B. The A* pathfinding engine will later replace this direct move.
 * @author MIAGE 2026 */
public class C_Protocol_PNMC_ships_astar extends C_Protocol_PNMC_ships {
	//
	// CONSTRUCTOR
	//
	public C_Protocol_PNMC_ships_astar(Context<Object> ctxt) { super(ctxt); }
	//
	// SPECIFIC METHOD
	//
	public C_Ship_astar createAstarShip() { return new C_Ship_astar(new C_GenomeAnimalia()); }
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** TEMP 0 to isolate the Astar ship for visual testing (Noumea->Lifou) - set back to 1 once confirmed - MIAGE 2026 */
	protected double getCargoPopulation() { return 0.; }
	@Override
	/** Add the single A-to-B ship at Noumea - MIAGE 2026 */
	protected void initPopulations() {
		super.initPopulations();
		C_Ship_astar boat = createAstarShip();
		this.contextualizeNewThingInContainer(boat,C_Ship_astar.getPointA());
		System.out.println("C_Protocol_PNMC_ships_astar.init: 1 Astar-ready ship created and positioned");
	}
}
