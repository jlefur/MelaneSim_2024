package data.constants;

import java.util.ArrayList;

/** Gathers all variables since software specifications requires no numbers in the java sources <br>
 * @author Le Fur & Sall 2015, rev. 2024 */
public interface I_ConstantPNMC_particules extends I_ConstantString {

	// FILES & URLs //
	public final String CSV_PATH = "data_csv/melanesia/";
	public final String RASTER_PATH_MELANESIA = "data_raster/melanesia/";
	public static final String PNMC_PK = "ocean.PNMC_plancton"; // MELANESIM PROTOCOL NAMES - used in context creator
	public static String CHRONO_FILENAME = "/20240314_PNMC.jlf.csv";// CHRONOGRAM FILE NAME
	public final String energyGridvalues = "valueEnergyGrid";// used to display energy

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
	// Others
	public static final int BACKGROUND_COLOR = 38;
	public static final int TERRESTRIAL_MIN_AFFINITY = 8;
	public static final int PLANKTON_CELLS_SPACING = 1; // interval where to post plankton cells see protocol.initpopulations
	public static final int BACKWARD_NB_CELLS = 1;// if particle reach bordure move back nb cells
	public static final int STREAM_DISPLAY_SIZE = 300;// taille des vecteurs courants affichés
	public static final double PARTICLE_RESISTANCE_FACTOR = 1;// freinage des particules vis à vis de la vitesse du courant)
	
	public static final int ENERGY_GREEN = 0;//
	public static final int ENERGY_ORANGE = 1;//
	public static final int ENERGY_RED = 2;//
	public static final int ENERGY_RESET = 3;//
	public static final int ENERGY_LAND = 4;//
	

}
