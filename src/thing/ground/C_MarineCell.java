package thing.ground;
public class C_MarineCell {
	//
	// FIELDS
	//
	public enum TypeElement { PLANCTON, NECTON, VAISSEAU }// ENUM DES TYPES

	private static final class Valeurs {// OBJET VALEUR
		private double valeur;
		public double get() { return valeur; }
		public void ajouter(double delta) { valeur += delta; }
	}

	private final Valeurs[] valeurs;// STOCKAGE INTERNE
	//
	// CONSTRUCTOR
	//
	public C_MarineCell() {
		valeurs = new Valeurs[TypeElement.values().length];
		for (TypeElement t : TypeElement.values()) {
			valeurs[t.ordinal()] = new Valeurs();
		}
	}
	//
	// SETTERS and GETTERS
	//
	public void ajouterPlancton(double delta) { valeurs[TypeElement.PLANCTON.ordinal()].ajouter(delta); }
	public void ajouterNecton(double delta) { valeurs[TypeElement.NECTON.ordinal()].ajouter(delta); }
	public void ajouterVaisseau(double delta) { valeurs[TypeElement.VAISSEAU.ordinal()].ajouter(delta); }
	public double getPlancton() { return valeurs[TypeElement.PLANCTON.ordinal()].get(); }
	public double getNecton() { return valeurs[TypeElement.NECTON.ordinal()].get(); }
	public double getVaisseau() { return valeurs[TypeElement.VAISSEAU.ordinal()].get(); }

	public static void main(String[] args) {
		C_MarineCell cell = new C_MarineCell();
		// Ajouts
		cell.ajouterPlancton(1.2);
		cell.ajouterPlancton(3.4);
		cell.ajouterNecton(4.5);
		cell.ajouterVaisseau(1);
		cell.ajouterVaisseau(1);
		cell.ajouterVaisseau(1);

		// Lecture de la valeur du plancton
		double valeurPlancton = cell.getPlancton();
		System.out.println("Valeur du plancton = " + valeurPlancton);
	}
}
