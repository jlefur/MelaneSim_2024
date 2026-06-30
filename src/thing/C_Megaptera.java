package thing;

import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeMegaptera;
/** @author JLF 06.2026 */
public class C_Megaptera extends A_Amniote {
	//
	// CONSTRUCTOR
	//
	public C_Megaptera(I_DiploidGenome genome) { super(new C_GenomeMegaptera()); }
	public C_Megaptera(String name,String sex) {
		this(new C_GenomeMegaptera());
		this.setMyName(name);
		if(sex.equals("M")) this.setMale(true);
		else this.setMale(false);
		this.setAge_Uday(4380.);// 12 years TODO jlf 06.2026number in source age at creation adult whales
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public void step_Utick() { super.step_Utick(); }
	@Override
	protected void checkDanger() {}
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) { return new C_Megaptera(genome); }
}
