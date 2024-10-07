/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data.constants;

/** - Constant names concerning types names shared within all protocols<br>
 * - Strings as well as unnecessary numbers are meant to disappear from the source code (see also I_numeric_constants and other
 * data/I_???_constants.java) <br>
 * - The correspondence is centralized here for string parameters.<br>
 * - Almost all packages implement this interface.
 * @see I_ConstantNumeric
 * @author J.Le Fur 08.2014, rev. JLF 11.2014, 02.2021, 07.2024 */
public interface I_ConstantString {

	// SPATIAL PROJECTIONS NAMES
	// options: 1) ascii (provide a grid.csv), 2) any other string, image (/ provide e.g. one *.gif,.jpg,.bmp).
	public final String RASTER_MODE = "ascii";
	public final String GEOGRAPHY_NAME = "geography";// used in SIMmasto0.rs/context.xml
	public final String CONTINUOUS_SPACE_NAME = "space";// used in SIMmasto0.rs/context.xml
	public final String VALUE_LAYER_NAME = "valuegrid";// used in SIMmasto0.rs/context.xml
	public final String proj_gridvalue2 = "valuegrid2";// used in networked/graphed landscapes

	// CONSOLE OUTPUT
	public final boolean isError = true;
	public final boolean isNotError = false;

	// FILES & URLs //
	public final String CONSOLE_OUTPUT_FILE = "retour_console.txt";
	public final String CSV_PATH = "data_csv";
	public final String OUTPUT_PATH = "data_output/";
	public final String REPAST_PATH = "MelaneSim.rs/";
	public final String EVENT_VALUE2_FIELD_SEPARATOR = ":";// used when event value2 is a list
	public final String HOUR_MINUTE_SEPARATOR = ":";// used when event value2 is a list
	public final String CSV_FIELD_SEPARATOR = ";";
	public final String NAMES_SEPARATOR = "_";// used when event value2 is a list

	// LIFE TRAITS CONSTANTS - used in genomes
	public static final String LITTER_SIZE = "litterSize";
	public static final String WEANING_AGE_Uday = "weaningAge_Uday";
	public static final String MATING_LATENCY_Uday = "matingLatency_Uday";
	public static final String GESTATION_LENGTH_Uday = "gestationLength_Uday";
	public static final String GROWTH_RATE_UkcalPerDay = "growthRate_UgramPerDay";
	public static final String SEXUAL_MATURITY_Uday = "sexualMaturity_Uday";
	public static final String SPEED_UmeterByDay = "speed_UmeterByDay";
	public static final String SENSING_UmeterByDay = "sensing_UmeterByDay";
	public static final String MAX_AGE_Uday = "maxAge_Uday";
}
