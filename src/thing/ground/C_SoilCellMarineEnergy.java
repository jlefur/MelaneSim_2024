package thing.ground;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import thing.I_MarineActor.DriverType;
/***/
public class C_SoilCellMarineEnergy extends C_SoilCell {
	//
	// FIELDS
	//
	public enum Champ { RAW_VAL, NB_VAL, INTEGRAL_100, _100 }// ENUM DES CHAMPS (multi-valeurs par type)
	private final Valeurs[] valeurs;// STOCKAGE INTERNE : enum + tableau
	//
	// INTERNAL CLASS
	//
	private static final class Valeurs {// OBJET VALEUR GÉNÉRIQUE
		// FIELD
		private final double[] champs = new double[Champ.values().length];
		// SETTER and GETTER
		public void add(Champ champ,double delta) { champs[champ.ordinal()] += delta; }
		public void set(Champ champ,double value) { champs[champ.ordinal()] = value; }
		public double get(Champ champ) { return champs[champ.ordinal()]; }
		// OVERRIDEN METHOD
		@Override
		public String toString() {
			DecimalFormat df = new DecimalFormat("0.00",DecimalFormatSymbols.getInstance(Locale.US));
			return "RAW_VAL="+df.format(champs[Champ.RAW_VAL.ordinal()])+", NB_VAL="+champs[Champ.NB_VAL.ordinal()]
					+", INTEGRAL_100="+df.format(champs[Champ.INTEGRAL_100.ordinal()])+", _100="+df.format(
							champs[Champ._100.ordinal()]);
		}
	}
	//
	// CONSTRUCTOR
	//
	public C_SoilCellMarineEnergy(int aff,int lineNo,int colNo) {
		super(aff,lineNo,colNo);
		valeurs = new Valeurs[DriverType.values().length];
		for(DriverType t:DriverType.values()){
			valeurs[t.ordinal()] = new Valeurs();
		}
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("C_SoilCellMarineEnergy {\n");
		for(DriverType type:DriverType.values()) sb.append("  ").append(type).append(" (").append(type.getUnit())
				.append(")").append(" -> ").append(valeurs(type)).append("\n");
		sb.append("}");
		return sb.toString();
	}
	public String toString0() { DriverType type = DriverType.PLANKTON; return(type.toString()+": "+valeurs(type)); }
	//
	// METHODS
	//
	private Valeurs valeurs(DriverType type) { return valeurs[type.ordinal()]; }
	// API GÉNÉRIQUE (Type + Champ)
	public void add(DriverType type,Champ champ,double delta) { valeurs(type).add(champ,delta); }
	public void set(DriverType type,Champ champ,double value) { valeurs(type).set(champ,value); }
	public double get(DriverType type,Champ champ) { return valeurs(type).get(champ); }
	public double getEnergy_Ukcal() { /** sum_NB_VAL_times_100_overAllTypes() */
		double sum = 0.0;
		for(DriverType type:DriverType.values()) sum += this.get(type,Champ.NB_VAL)*this.get(type,Champ._100);
		return sum;
	}
	/** sum INTEGRAL_100_overAllTypes()
	 * @see A_Protocol_PNMC#computeMinMaxIntegrals */
	public double getIntegralEnergy_Ukcal() {
		double sum = 0.0;
		for(DriverType type:DriverType.values()) sum += this.get(type,Champ.INTEGRAL_100);
		return sum;
	}
	/** Reset all values of INTEGRAL_100 for every driver to value corresponding to the actors still in the cell */
	public void resetIntegralEnergy_Ukcal() {
		for(DriverType type:DriverType.values()) this.set(type,Champ.INTEGRAL_100,this.get(type,Champ.NB_VAL)*this.get(
				type,Champ._100));
	}

}