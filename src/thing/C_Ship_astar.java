package thing;

import data.converters.C_ConvertTimeAndSpace;
import thing.dna.I_DiploidGenome;
import thing.ground.I_Container;

/** First step of the Ships_Astar protocol: a ship moves in a straight line from a fixed point A to a fixed point B.
 * The direct move performed here in step_Utick() is meant to be replaced by the A* pathfinding engine
 * (@see melanesim.util.astar.AStar) in a later iteration.
 * @author MIAGE 2026 */
public class C_Ship_astar extends A_Animal implements I_MarineActor {
	//
	// FIELDS
	//
	/** Noumea coordinates from C_Ship_cargo */
	public static final int[] POINT_A_Ucell = { 231, 94 };
	/** Lifou - MIAGE 2026 */
	public static final int[] POINT_B_Ucell = { 250, 125 };
	private boolean arrivedAtDestination = false;
	//
	// CONSTRUCTOR
	//
	public C_Ship_astar(I_DiploidGenome genome) {
		super(genome);
		this.initParameters();
		this.setMyName("ShipAstar-"+this.myId);
		this.setTarget(getPointB());
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public void step_Utick() {
		if(!this.arrivedAtDestination){
			if(this.isArrived(this.speed_UmeterByTick)) this.manageArrival();
			else{
				this.computeNextMoveToTarget();
				this.actionMove();
			}
		}
	}
	/** vitesse : 10 noeuds */
	@Override
	public void initParameters() {
		double speed_UmeterByDay = 1852.*24.*10.;
		this.speed_UmeterByTick = speed_UmeterByDay/C_ConvertTimeAndSpace.oneDay_Utick;
	}
	//
	// SPECIFIC METHOD
	//
	protected void manageArrival() {
		this.arrivedAtDestination = true;
		this.setHasToSwitchFace(true);
		this.nextMove_Umeter.x = 0.0;
		this.nextMove_Umeter.y = 0.0;
		A_VisibleAgent.myLandscape.moveToLocation(this,this.target.getCoordinate_Ucs());
	}
	//
	// GETTERS
	//
	@Override
	public DriverType getTypeActeur() { return DriverType.SHIP; }
	public boolean isArrivedAtDestination() { return arrivedAtDestination; }
	public static I_Container getPointA() { return myLandscape.getGrid()[POINT_A_Ucell[0]][POINT_A_Ucell[1]]; }
	public static I_Container getPointB() { return myLandscape.getGrid()[POINT_B_Ucell[0]][POINT_B_Ucell[1]]; }
}
