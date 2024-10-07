package thing;
import thing.dna.I_DiploidGenome;
import thing.ground.A_SupportedContainer;
/** a simple structure containing a diploid genome, gets all the properties of Animal
 * @author J.LeFur 2024 */
public class C_Plankton extends A_SupportedContainer {

	public C_Plankton(I_DiploidGenome genome) {
		super();
		this.setMyName("marine plankton" + NAMES_SEPARATOR + myId);
	}
}
