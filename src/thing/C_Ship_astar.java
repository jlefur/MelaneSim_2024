package thing;

import java.util.List;

import org.locationtech.jts.geom.Coordinate;

import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertTimeAndSpace;
import melanesim.C_ContextCreator;
import melanesim.protocol.A_Protocol;
import melanesim.util.C_PathWandererAstar_Marine;
import thing.dna.I_DiploidGenome;
import thing.ground.C_Harbour;
import thing.ground.C_SoilCellMarineNode;

/** Bateau qui va de port en port en suivant le chemin calculé par A* (il évite la terre).
 * Après une escale, il part vers un autre port choisi au hasard.
 * @see melanesim.util.astar.AStarMarine
 * @see C_Harbour
 * @author MIAGE 2026 */
public class C_Ship_astar extends A_Animal implements I_MarineActor, I_ConstantPNMC {
	//
	// FIELDS
	//
	/** Ports où le bateau peut aller */
	private final List<C_Harbour> harbours;
	private C_Harbour currentHarbour;
	private C_Harbour destinationHarbour = null;
	private C_PathWandererAstar_Marine pathWanderer = new C_PathWandererAstar_Marine();
	private double stopoverLeft_Utick = 0.;
	private int nbTrips = 0;
	//
	// CONSTRUCTOR
	//
	public C_Ship_astar(I_DiploidGenome genome, C_Harbour homeHarbour, List<C_Harbour> harbours) {
		super(genome);
		this.harbours = harbours;
		this.currentHarbour = homeHarbour;
		this.initParameters();
		this.setMyName("ShipAstar-"+this.myId);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	public void step_Utick() {
		if(this.stopoverLeft_Utick>0.){ // en escale au port
			this.stopoverLeft_Utick--;
			return;
		}
		if(!this.pathWanderer.hasPath()) this.leaveForNextHarbour();
		if(this.pathWanderer.hasPath()) this.actionSailOnPath();
	}
	/** Vise la case du chemin s'il y en a un, sinon la cible */
	@Override
	public Coordinate getTargetPoint_Umeter() {
		if(this.pathWanderer!=null&&this.pathWanderer.hasPath()&&!this.pathWanderer.isPathEnd)
		    return this.pathWanderer.getCurrentNodePoint_Umeter();
		return super.getTargetPoint_Umeter();
	}
	/** vitesse : 10 noeuds */
	@Override
	public void initParameters() {
		double speed_UmeterByDay = 1852.*24.*10.;
		this.speed_UmeterByTick = speed_UmeterByDay/C_ConvertTimeAndSpace.oneDay_Utick;
	}
	@Override
	public void discardThis() {
		if(this.pathWanderer!=null) this.pathWanderer.discardThis();
		this.pathWanderer = null;
		this.currentHarbour = null;
		this.destinationHarbour = null;
		super.discardThis();
	}
	//
	// SPECIFIC METHODS
	//
	/** Choisit au hasard un autre port et calcule le chemin pour y aller */
	protected void leaveForNextHarbour() {
		if(this.harbours.size()<2) return;
		C_Harbour next;
		do{
			int index = (int)(C_ContextCreator.randomGeneratorForDestination.nextDouble()*this.harbours.size());
			next = this.harbours.get(Math.min(index,this.harbours.size()-1));
		}while(next==this.currentHarbour);
		this.destinationHarbour = next;
		this.setTarget(next.getBerthCell());
		if(!this.pathWanderer.getPath((C_SoilCellMarineNode)this.currentSoilCell,next.getBerthCell())){
			A_Protocol.event("C_Ship_astar.leaveForNextHarbour",this+" found no sea path from "+this.currentHarbour
			        +" to "+next,isError);
			this.destinationHarbour = null; // on essaie un autre port au prochain tick
		}
		else this.setHasToSwitchFace(true);
	}
	/** Avance de toute la distance du tick, case après case */
	protected void actionSailOnPath() {
		double remainingDistance_Umeter = this.speed_UmeterByTick;
		while(remainingDistance_Umeter>0.&&!this.pathWanderer.isPathEnd){
			double distanceToNode_Umeter = this.getCoordinate_Umeter().distance(this.getTargetPoint_Umeter());
			if(distanceToNode_Umeter<=remainingDistance_Umeter){
				// atteint la case puis vise la suivante
				if(distanceToNode_Umeter>0.){
					this.computeNextMoveToTarget(distanceToNode_Umeter);
					this.actionMove();
				}
				remainingDistance_Umeter -= distanceToNode_Umeter;
				this.pathWanderer.setNextNode();
			}
			else{
				this.computeNextMoveToTarget(remainingDistance_Umeter);
				this.actionMove();
				remainingDistance_Umeter = 0.;
			}
		}
		if(this.pathWanderer.isPathEnd) this.manageArrival();
	}
	/** S'arrête au port pour une escale */
	protected void manageArrival() {
		this.setHasToSwitchFace(true);
		this.nextMove_Umeter.x = 0.0;
		this.nextMove_Umeter.y = 0.0;
		A_VisibleAgent.myLandscape.moveToLocation(this,this.destinationHarbour.getBerthCell().getCoordinate_Ucs());
		this.currentHarbour = this.destinationHarbour;
		this.destinationHarbour = null;
		this.pathWanderer.nodesList = null;
		this.stopoverLeft_Utick = SHIP_ASTAR_STOPOVER_Uday*C_ConvertTimeAndSpace.oneDay_Utick;
		this.nbTrips++;
		A_Protocol.event("C_Ship_astar.manageArrival",this+" berthed in "+this.currentHarbour+" (trip #"+this.nbTrips
		        +")",isNotError);
	}
	//
	// GETTERS
	//
	@Override
	public DriverType getTypeActeur() { return DriverType.SHIP; }
	public C_Harbour getCurrentHarbour() { return this.currentHarbour; }
	public C_Harbour getDestinationHarbour() { return this.destinationHarbour; }
	public int getNbTrips() { return this.nbTrips; }
	public boolean isBerthed() { return this.stopoverLeft_Utick>0.; }
}
