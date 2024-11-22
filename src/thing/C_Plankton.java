package thing;
import thing.dna.I_DiploidGenome;
/** a simple structure containing a diploid genome, gets all the properties of Animal
 * @author J.LeFur 2024 */
public class C_Plankton extends A_Organism {

	public C_Plankton(I_DiploidGenome genome) {
		super(genome);
		this.setMyName("marine plankton" + NAMES_SEPARATOR + myId);
	}
}
