package thing;
import repast.simphony.essentials.RepastEssentials;
import thing.dna.I_DiploidGenome;
import thing.ground.C_MarineCell;
/** A simple structure containing a diploid genome, owns all properties of A_Organism
 * @author J.LeFur 2024 */
public class C_Plankton extends A_Organism {
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
	@Override
	/** If washed on shore, set dead and recirculated on domain's bordure - JLF 2024 */
	public void step_Utick() {
		if (RepastEssentials.GetTickCount() > 10 && this.getSpeedEast() == 0.0 && this.getSpeedNorth() == 0.0) {
			this.setDead(true);
			this.hasLeftDomain = true;
		}
	}
	//
	// GETTERS
	//
	/** for probe display purpose */
	public double getSpeedEast() {
		return ((C_MarineCell) this.getCurrentSoilCell()).getSpeedEastward_UmeterPerSecond();
	}
	/** for probe display purpose */
	public double getSpeedNorth() {
		return ((C_MarineCell) this.getCurrentSoilCell()).getSpeedNorthward_UmeterPerSecond();
	}

}
