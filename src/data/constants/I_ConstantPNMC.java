package data.constants;

import java.util.ArrayList;

/** Gathers all variables since software specifications requires no numbers in the java sources <br>
 * @author Le Fur & Sall 2015, rev. 2024 */
public interface I_ConstantPNMC extends I_ConstantString {

	// FILES & URLs //
	public final String CSV_PATH = "data_csv/melanesia/";
	public final String RASTER_PATH_MELANESIA = "data_raster/melanesia/";
	public static final String PNMC_DRIFTERS = "ocean.PNMC_drifters"; // MELANESIM PROTOCOL NAMES - used in context creator
	public static final String PNMC_PLANKTON = "ocean.PNMC_plankton"; // MELANESIM PROTOCOL NAMES - used in context creator
	public static final String PNMC_NEKTON = "ocean.PNMC_nekton"; // MELANESIM PROTOCOL NAMES - used in context creator
	public final String energyGridvalues = "valueEnergyGrid";// used to display energy
	public final String NEKTON_GRID = "nektonGrid";// used to display nekton abundance

	public static int CELL_SIZE = 245; // DEFAULT : 15 //Junk Value
	public static final ArrayList<Double> rasterLongitudeWest_LatitudeSouth_Udegree = new ArrayList<Double>() {
		{
			add(156.);
			add(-26.5);
		}
		private static final long serialVersionUID = 1L;
	};
	public static final ArrayList<String> currentSpeed_URL_suffix = new ArrayList<String>() {
		{
			add("");
			add("");
		}
		private static final long serialVersionUID = 1L;
	};

	// EVENT TYPES CONSTANTS - used in chrono events
	public static final String CURRENT_EVENT = "current";
	public static final String CHLOROPHYLL_EVENT = "chlorophyll";
	public static final String NEKTON_EVENT = "microNekton";
	public static final String COMPUTE_ENERGY = "compute_energy";
	// Others
	public static final int BACKGROUND_COLOR = 38;
	public static int TERRESTRIAL_MIN_AFFINITY = 99;
	/** Used to lighten the GUI calculations */
	public static final int PARTICLE_CELLS_SPACING = 3; // interval where to post plankton cells see protocol.initpopulations
	public static final int BACKWARD_NB_CELLS = 1;// if particle reach bordure move back nb cells
	public static final int STREAM_DISPLAY_SIZE = 300;// taille des vecteurs courants affichés
	public static final double PARTICLE_RESISTANCE_FACTOR = 1.;// freinage des particules vis à vis de la vitesse du courant)

	public static final int ENERGY_GREEN = 0;//
	public static final int ENERGY_ORANGE = 1;//
	public static final int ENERGY_RED = 2;//
	public static final int ENERGY_RESET = 3;//
	public static final int ENERGY_LAND = 4;//

	public static final double GREEN_AREA_Upercent = .25;// green lower limit
	public static final double ORANGE_AREA_Upercent = .50;// orange lower limit
	public static final double RED_AREA_Upercent = .75;// red lower limit

	public static final double CHLOROPHYLL_MULTIPLIER = 24.93765586;// for year 2021 maximum=0.40059945, minimum = 0.047118366 -> x24.93765586
	public static final double MICRONEKTON_MULTIPLIER = 2.039093501;// for year 2021 maximum=4.90414,    minimum = 0           -> x2.039093501
	public static final int ENERGY_MULTIPLIER_PLANKTON = 20;// arbitrary (for display use currently JLF 04.2025)
}
