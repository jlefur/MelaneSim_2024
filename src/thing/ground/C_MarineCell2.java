package thing.ground;
public class C_MarineCell2 {
	    //
	    // FIELDS
	    //
		public enum TypeElement {//ENUM DES TYPES MÉTIER
			PLANCTON("chlorophylle mg/m3"),
			NECTON("densité g/m2"),
			VAISSEAU("effectif");

			private final String unite;

			TypeElement(String unite) {this.unite = unite;}
			public String getUnite() {return unite;}
		}
	    
	    public enum Champ {INTEGRE,INTEGRE_NORMALISE}//ENUM DES CHAMPS (multi-valeurs par type)
	    private static final class Valeurs {// OBJET VALEUR GÉNÉRIQUE
	        private final double[] champs = new double[Champ.values().length];
	        public double get(Champ champ) {return champs[champ.ordinal()];}
	        public void add(Champ champ, double delta) {champs[champ.ordinal()] += delta;}
	        	@Override
	        	public String toString() {
	        		return "integre=" + champs[Champ.INTEGRE.ordinal()]
	        				+ ", normalise=" + champs[Champ.INTEGRE_NORMALISE.ordinal()];
	        	}
	        }
	    private final Valeurs[] valeurs;// STOCKAGE INTERNE : enum + tableau
	    //
	    // CONSTRUCTOR
	    //
	    public C_MarineCell2() {
	        valeurs = new Valeurs[TypeElement.values().length];
	        for (TypeElement t : TypeElement.values()) {
	            valeurs[t.ordinal()] = new Valeurs();
	        }
	    }
	    // MÉTHODE INTERNE FACTORISÉE
	    private Valeurs valeurs(TypeElement type) {return valeurs[type.ordinal()];}
	    
	    // API GÉNÉRIQUE (Type + Champ)
	    public void add(TypeElement type, Champ champ, double delta) {valeurs(type).add(champ, delta);}
	    public double get(TypeElement type, Champ champ) {return valeurs(type).get(champ);}
	    
	    // API MÉTIER EXPRESSIVE — PLANCTON
	    public void ajouterPlancton(double delta) {add(TypeElement.PLANCTON, Champ.INTEGRE, delta);}
	    public void ajouterPlanctonNormalise(double delta) {add(TypeElement.PLANCTON, Champ.INTEGRE_NORMALISE, delta);}
	    public double getPlancton() {return get(TypeElement.PLANCTON, Champ.INTEGRE);}
	    public double getPlanctonNormalise() {return get(TypeElement.PLANCTON, Champ.INTEGRE_NORMALISE);}
	    // API MÉTIER EXPRESSIVE — NECTON
	    public void ajouterNecton(double delta) {add(TypeElement.NECTON, Champ.INTEGRE, delta);}
	    public double getNecton() {return get(TypeElement.NECTON, Champ.INTEGRE);}
	    // API MÉTIER EXPRESSIVE — VAISSEAU
	    public void ajouterVaisseau(double delta) {add(TypeElement.VAISSEAU, Champ.INTEGRE, delta);}
	    public double getVaisseau() {return get(TypeElement.VAISSEAU, Champ.INTEGRE);}
	    @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder("C_MarineCell {\n");
	        for (TypeElement type : TypeElement.values())
	            sb.append("  ").append(type).append(" (").append(type.unite).append(")").append(" -> ").append(valeurs(type)).append("\n");
	            sb.append("}");
	        return sb.toString();
	    }
	    //
	    // MAIN 
	    //
	    public static void main(String[] args) {
	        C_MarineCell2 cell = new C_MarineCell2();
	        // Ajouts demandés
	        cell.ajouterPlancton(1.2);
	        cell.ajouterPlancton(3.4);
	        cell.ajouterNecton(4.5);
	        cell.ajouterVaisseau(1);
	        cell.ajouterVaisseau(1);
	        cell.ajouterVaisseau(1);
	        // Lecture de la valeur du plancton
	        double valeurPlancton = cell.getPlancton();
	        System.out.println("Valeur du plancton = " + valeurPlancton);
	        System.out.println(cell.toString());
	    }
	}