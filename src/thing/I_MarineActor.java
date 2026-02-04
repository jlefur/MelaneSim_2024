package thing;

/** ENUM OF CONSERVATION TARGETS */
public interface I_MarineActor {
	enum TypeActeur {
		PARTICLES ("size"), PLANKTON("chlorophylle mg/m3"),NEKTON("densité g/m2"),SHIP("effectif");
		// FIELD
		private final String unite;
		// CONSTRUCTOR
		TypeActeur(String unite) { this.unite = unite; }
		// GETTER
		public String getUnite() { return unite; }
	}

	TypeActeur getTypeActeur();
}
