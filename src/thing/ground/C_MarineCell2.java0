package thing.ground;
public class C_MarineCell2 {
	//
	// INTERNAL CLASSES
	//
	public enum TypeActeur {
		PLANKTON("chlorophylle mg/m3"),NEKTON("densité g/m2"),SHIP("effectif");// ENUM DES TYPES MÉTIER
		private final String unite;
		TypeActeur(String unite){ this.unite=unite; }
		public String getUnite() { return unite; }
	}

	private static final class Valeurs {// OBJET VALEUR GÉNÉRIQUE
		private final double[] champs=new double[Champ.values().length];
		public double get(Champ champ) { return champs[champ.ordinal()]; }
		public void add(Champ champ,double delta) { champs[champ.ordinal()]+=delta; }
		@Override
		public String toString() {
			return "valeur="+champs[Champ.VALEUR.ordinal()]+", integre="+champs[Champ.INTEGRE.ordinal()]+", _100="
					+champs[Champ._100.ordinal()];
		}
	}
	//
	// FIELDS
	//
	public enum Champ { VALEUR, INTEGRE, _100 }// ENUM DES CHAMPS (multi-valeurs par type)
	private final Valeurs[] valeurs;// STOCKAGE INTERNE : enum + tableau
	//
	// CONSTRUCTOR
	//
	public C_MarineCell2(){
		valeurs=new Valeurs[TypeActeur.values().length];
		for(TypeActeur t:TypeActeur.values()){
			valeurs[t.ordinal()]=new Valeurs();
		}
	}
	// MÉTHODE INTERNE FACTORISÉE
	private Valeurs valeurs(TypeActeur type) { return valeurs[type.ordinal()]; }

	// API GÉNÉRIQUE (Type + Champ)
	public void add(TypeActeur type,Champ champ,double delta) { valeurs(type).add(champ,delta); }
	public double get(TypeActeur type,Champ champ) { return valeurs(type).get(champ); }

	// API MÉTIER EXPRESSIVE — PLANCTON
	public void ajouterPlancton(double delta) { add(TypeActeur.PLANKTON,Champ.VALEUR,delta); }
	public void ajouterPlanctonNormalise(double delta) { add(TypeActeur.PLANKTON,Champ.INTEGRE,delta); }
	public double getPlancton() { return get(TypeActeur.PLANKTON,Champ.VALEUR); }
	public double getPlanctonNormalise() { return get(TypeActeur.PLANKTON,Champ.INTEGRE); }
	// API MÉTIER EXPRESSIVE — NECTON
	public void ajouterNecton(double delta) { add(TypeActeur.NEKTON,Champ.VALEUR,delta); }
	public double getNecton() { return get(TypeActeur.NEKTON,Champ.VALEUR); }
	// API MÉTIER EXPRESSIVE — VAISSEAU
	public void ajouterVaisseau(double delta) { add(TypeActeur.SHIP,Champ.VALEUR,delta); }
	public double getVaisseau() { return get(TypeActeur.SHIP,Champ.VALEUR); }
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder("C_MarineCell {\n");
		for(TypeActeur type:TypeActeur.values())
			sb.append("  ").append(type).append(" (").append(type.unite).append(")").append(" -> ").append(valeurs(
					type)).append("\n");
		sb.append("}");
		return sb.toString();
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		C_MarineCell2 cell=new C_MarineCell2();
		// Ajouts demandés
		cell.ajouterPlancton(1.2);
		cell.ajouterPlancton(3.4);
		cell.ajouterNecton(4.5);
		cell.ajouterVaisseau(1);
		cell.ajouterVaisseau(1);
		cell.ajouterVaisseau(1);
		System.out.println("Valeur du plancton = "+cell.getPlancton());
		System.out.println(cell.toString());
	}
}