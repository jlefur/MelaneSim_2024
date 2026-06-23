package thing;

/** ENUM OF CONSERVATION TARGETS */
public interface I_MarineActor {
	enum DriverType {
		PARTICLES ("size"), PLANKTON("chlorophylle mg/m3"),NEKTON("densité g/m2"),SHIP("effectif"), WHALE("effectif");
		// FIELD
		private final String unit;
		// CONSTRUCTOR
		DriverType(String unite) { this.unit = unite; }
		// GETTER
		public String getUnit() { return unit; }
	}

	DriverType getTypeActeur();
}
