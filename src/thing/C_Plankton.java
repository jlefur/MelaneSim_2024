package thing;
import thing.dna.I_DiploidGenome;
import thing.ground.C_MarineCell;
/** A simple structure containing a diploid genome, owns all properties of A_Organism
 * @author J.LeFur 2024 */
public class C_Plankton extends A_Organism {
	//
	// FIELD
	//
	private double maxDispersalDistance_Umeter = 0.0;
	//
	// CONSTRUCTOR
	//
	public C_Plankton(I_DiploidGenome genome) {
		super(genome);
		this.setMyName("marine plankton" + NAMES_SEPARATOR + myId);
	}
	//
	// OVERRIDEN METHOD
	//
	public void step_Utick() {
		computeMaxDispersalDistance_Umeter();
		super.step_Utick();
	}
	//
	// GETTERS
	//
	/** for probe display purpose */
	public double getSpeedEast() {
		if (this.isDead()) return 0.0;
		else return ((C_MarineCell) this.getCurrentSoilCell()).getSpeedEastward_UmeterPerSecond();
	}
	/** for probe display purpose */
	public double getSpeedNorth() {
		if (this.isDead()) return 0.0;
		else return ((C_MarineCell) this.getCurrentSoilCell()).getSpeedNorthward_UmeterPerSecond();
	}
	/** Maximum distance (straight line) from its birth location */
	public void computeMaxDispersalDistance_Umeter() {
		double currentDispersalDistance_Umeter = this.getDistance_Umeter(this.bornCoord_Umeter);
		if (currentDispersalDistance_Umeter > this.maxDispersalDistance_Umeter)
			this.maxDispersalDistance_Umeter = currentDispersalDistance_Umeter;
	}
	//
	// GETTER
	//
	public double getMaxDispersalDistance_Umeter() {
		return maxDispersalDistance_Umeter;
	}
}
