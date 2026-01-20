package thing.ground;

import thing.I_MarineActor.TypeActeur;

public class C_SoilCellMarineEnergy extends C_SoilCell {
	//
	// FIELDS
	//
	public enum Champ { VALEUR, INTEGRE, _100 }// ENUM DES CHAMPS (multi-valeurs par type)
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
			return "valeur="+champs[Champ.VALEUR.ordinal()]+", integre="+champs[Champ.INTEGRE.ordinal()]+", _100="
					+champs[Champ._100.ordinal()];
		}
	}
	//
	// CONSTRUCTOR
	//
	public C_SoilCellMarineEnergy(int aff,int lineNo,int colNo) {
		super(aff,lineNo,colNo);
		valeurs = new Valeurs[TypeActeur.values().length];
		for(TypeActeur t:TypeActeur.values()){
			valeurs[t.ordinal()] = new Valeurs();
		}
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("C_SoilCellMarineEnergy {\n");
		for(TypeActeur type:TypeActeur.values()) sb.append("  ").append(type).append(" (").append(type.getUnite())
				.append(")").append(" -> ").append(valeurs(type)).append("\n");
		sb.append("}");
		return sb.toString();
	}
	//
	// METHODS
	//
	private Valeurs valeurs(TypeActeur type) { return valeurs[type.ordinal()]; }
	// API GÉNÉRIQUE (Type + Champ)
	public void add(TypeActeur type,Champ champ,double delta) { valeurs(type).add(champ,delta); }
	public void set(TypeActeur type,Champ champ,double value) { valeurs(type).set(champ,value); }
	public double get(TypeActeur type,Champ champ) { return valeurs(type).get(champ); }
}