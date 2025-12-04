package melanesim.protocol;

import java.util.Calendar;
import java.util.Comparator;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertGeographicCoordinates;
import melanesim.util.CaptureEcranPeriodique;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorEnergyMarine;
import presentation.epiphyte.C_InspectorPopulationMarine;
import repast.simphony.context.Context;
import thing.ground.C_LandPlot;
import thing.ground.C_SoilCell;
import thing.ground.C_SoilCellMarine;
import thing.ground.I_Container;
import thing.ground.landscape.C_LandscapeMarine;

public abstract class A_Protocol_PNMC extends A_Protocol implements I_ConstantPNMC {
	//
	// FIELDS
	//
	protected C_ConvertGeographicCoordinates geographicCoordinateConverter = null;
	public static boolean DISPLAY_FACILITY_MAP = false;// used to change plankton color if facility map is on
	//
	// CONSTRUCTOR
	//
	public A_Protocol_PNMC(Context<Object> ctxt) {
		super(ctxt);
		// Position landplots at the barycentre of cells
		for (C_LandPlot lp : this.landscape.getAffinityLandPlots()) {
			double xx = 0., yy = 0.;
			for (C_SoilCell cell : lp.getCells()) {
				xx += cell.getCoordinate_Ucs().x;
				yy += cell.getCoordinate_Ucs().y;
			}
			xx = xx / lp.getCells().size();
			yy = yy / lp.getCells().size();
			this.landscape.moveToLocation(lp, new Coordinate(xx, yy));
			lp.setCurrentSoilCell(this.landscape.getGrid()[(int) xx][(int) yy]);
			lp.bornCoord_Umeter = this.landscape.getThingCoord_Umeter(lp.getCurrentSoilCell());
		}
		// INSPECTORS
		A_Protocol.inspectorPopulation = new C_InspectorPopulationMarine();
		inspectorList.add(inspectorPopulation);
		A_Protocol.inspectorEnergy = new C_InspectorEnergy();
//		A_Protocol.inspectorEnergy = new C_InspectorEnergyMarine();
		this.inspectorList.add(inspectorEnergy);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** set grid content to C_SoilCellMarine, JLF 2024 */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeMarine(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME,
				CONTINUOUS_SPACE_NAME));
		for (int i = 0; i < this.landscape.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.height; j++) {
				C_SoilCellMarine cell = new C_SoilCellMarine(this.landscape.getGrid()[i][j].getAffinity(), i, j);
				// Comment the following line to undisplay soil cells, JLF 10.2015, 11.2015
				context.add(cell);
				this.landscape.setGridCell(i, j, cell);
				this.landscape.moveToLocation(cell, cell.getCoordinate_Ucs());
			}
		}
	}

	@Override
	public void initCalendar() {
		protocolCalendar.set(2020, Calendar.DECEMBER, 31);
	}

	@Override
	/** Save screen each day<br>
	 * Version Authors JEL2011, AR2011, rev. LeFur 2011,2012,2014,2024 */
	public void manageTimeLandmarks() {
//		 ((C_LandscapeMarine) this.landscape).assertCellsEnergy();
		// saveScreen();
		Integer currentMonth = A_Protocol.protocolCalendar.get(Calendar.MONTH);
		Integer currentWeek = A_Protocol.protocolCalendar.get(Calendar.WEEK_OF_MONTH);
		A_Protocol.protocolCalendar.incrementDate();

		// Check if map has to be switched Version JLF 08.2014, rev.10.2015, 05.2017
		boolean displayMapBefore = C_Parameters.DISPLAY_MAP;
		this.readUserParameters();
		if (displayMapBefore != C_Parameters.DISPLAY_MAP) switchDisplayMap();
		// if (C_Parameters.VERBOSE) C_sound.sound("tip.wav");

//		 if (currentMonth != A_Protocol.protocolCalendar.get(Calendar.MONTH)) {
		 if (currentWeek != A_Protocol.protocolCalendar.get(Calendar.WEEK_OF_MONTH)) {
		 ((C_LandscapeMarine) this.landscape).assertCellsEnergy();
		// saveScreen();
		 }

	}

	public void saveScreen() { /** // Uncomment lines below to slightly randomly move the mouse to avoid screen sleep mode (for
								 * recording printscreen) try { Robot robot = new Robot(); // Get the current mouse coordinates
								 * Point mouseLocation = MouseInfo.getPointerInfo().getLocation(); int x = mouseLocation.x; int y
								 * = mouseLocation.y; robot.mouseMove(x + random.nextInt(3), y + random.nextInt(3)); } catch
								 * (AWTException e) { e.printStackTrace(); } */

		// uncomment lines below to save screen // save screen each new day
		Integer currentYear = A_Protocol.protocolCalendar.get(Calendar.YEAR);
		Integer currentMonth = A_Protocol.protocolCalendar.get(Calendar.MONTH);
		Integer currentDay = A_Protocol.protocolCalendar.get(Calendar.DAY_OF_YEAR);
		Integer currentHour = A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY);
		String currentDate = "Energy-" + currentYear + "." + String.format("%02d", currentMonth + 1) + String.format(
				"%03d", currentDay) + "_" + String.format("%02d", currentHour);
		// or String currentDate = currentYear + "." + String.format("%03d", currentDay)
		// + "_"+ String.format("%03d", currentHour);
		CaptureEcranPeriodique.captureEcranPlankton(currentDate);
		// end of uncomment
	}
	@Override
	/** Display the map if on, remove it if off. Only one map object. The switch can only go from on to off and vice versa Version
	 * author J.Le Fur, 09.2014 */
	protected void switchDisplayMap() {
		if (this.context.contains(this.facilityMap)) {
			this.context.remove(this.facilityMap);
			DISPLAY_FACILITY_MAP = false;
		} // Wipe off map
		else {// contextualizeNewThingInSpace(facilityMap, facilityMap.whereX,
				// facilityMap.whereY);
			this.facilityMap.contextualize(this.context, this.landscape);
			DISPLAY_FACILITY_MAP = true;
		}
	}

	@Override
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		if (this.landscape != null) {
			for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++)
				for (int j = 0; j < this.landscape.getDimension_Ucell().getHeight(); j++) {
					if (this.landscape.getValueLayer().get(i, j) < TERRESTRIAL_MIN_AFFINITY) // marine area
						this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
				}
		}
	}

	@Override
	public void readUserParameters() {
		/** Check black map and exclos (metapopulation) */
		super.readUserParameters();
		boolean oldValueBlackMap = C_Parameters.BLACK_MAP;
		C_Parameters.BLACK_MAP = ((Boolean) C_Parameters.parameters.getValue("BLACK_MAP")).booleanValue();
		if (oldValueBlackMap != C_Parameters.BLACK_MAP) {
			if (C_Parameters.BLACK_MAP) this.blackMap();
			else if (this.landscape != null) this.landscape.resetCellsColor();
		}
		boolean oldValueExclos = C_Parameters.EXCLOS;
		C_Parameters.EXCLOS = ((Boolean) C_Parameters.parameters.getValue("EXCLOS")).booleanValue();
		if (oldValueExclos != C_Parameters.EXCLOS)
			A_Protocol.event("C_Protocol_PNMC_drifters.readUserParameters", "meta-population set to "
					+ C_Parameters.EXCLOS, isNotError);
	}
	@Override
	public void step_Utick() {
		super.step_Utick();
		C_SoilCellMarine cell = (C_SoilCellMarine) this.landscape.getGrid()[127][217];
		if (cell.getOccupantList().size() >= 1)
			A_Protocol.event("127-127 ", cell.getOccupantList().size() + "occupants, énergie: " + Math.round(cell
					.getEnergy_Ukcal()) + " (" + C_Parameters.PARTICLE_MULTIPLIER + "), " + ", xphylle: " + Math.round(
							cell.getChlorophyll_U100()) + " (" + C_Parameters.CHLOROPHYLL_MULTIPLIER + "), necton: "
					+ Math.round(cell.getMicroNekton()) + " (" + C_Parameters.NEKTON_MULTIPLIER + ")", isError);
	}
	//
	// SPECIFIC METHODS
	//
	/** Convertit les valeurs d'entrée des paramètres en valeurs sur une échelle de 1 à 100 <br>
	 * @author chatGPT 10.2025 */
	public double convertTo100(double x, double xMin, double xMax) {
		return ((x - xMin) * 99) / (xMax - xMin) + 1;
	}
	/** Convertit les valeurs sur une échelle de 1 à 100 des paramètres en valeurs lues dans les fichiers<br>
	 * @author chatGPT 10.2025 */
	public double convertFrom100(double y, double xMin, double xMax) {
		return ((y - 1) * (xMax - xMin)) / 99 + xMin;
	}

	/** _STREAM_: Un Stream est un “tuyau” dans lequel tu fais passer des éléments pour les transformer, filtrer, trier,
	 * collecter<br>
	 * Il ne stocke pas de données, il permet d’appliquer des opérations comme sur une chaîne de montage. <br>
	 * _FLATMAP_: Comme la grille est un tableau 2D, stream() donne un flux de lignes, pas de cellules; flatMap sert à aplatir un
	 * flux de flux en un seul flux (sans flatMap, tu aurais un Stream<C_SoilCellMarine[]>)<br>
	 */
	@Override
	/** JUNK replace terminate par afficher les cellules les plus occupées JLF 11.2025 */
	protected void haltSimulation() {
		if (C_Parameters.TERMINATE) {
			final int[] rank = {1};
			// convertit la grille de I_Container en grille de C_SoilCellMarine
			I_Container[][] landscapeGrid = this.landscape.getGrid();
			C_SoilCellMarine[][] grid = new C_SoilCellMarine[landscapeGrid.length][landscapeGrid[0].length];
			for (int i = 0; i < landscapeGrid.length; i++) {
				for (int j = 0; j < landscapeGrid[i].length; j++) {
					grid[i][j] = (C_SoilCellMarine) landscapeGrid[i][j]; // cast élément par élément
				}
			}

			java.util.Arrays.stream(grid).flatMap(java.util.Arrays::stream) // transforme la grille 2D en flux de cellules
					.sorted(Comparator.comparingInt(C_SoilCellMarine::getTotalOccupants).reversed()).limit(2000)
					.forEach(cell -> {
						int ix = (int) cell.getCoordinate_Ucs().x;
						int jy = (int) cell.getCoordinate_Ucs().y;
						// cell.setAffinity(11);
						this.landscape.getValueLayer().set(11, ix, jy);
					});
			java.util.Arrays.stream(grid).flatMap(java.util.Arrays::stream) // transforme la grille 2D en flux de cellules
					.sorted(Comparator.comparingInt(C_SoilCellMarine::getTotalOccupants).reversed()).limit(5).forEach(
							cell -> {
								int ix = (int) cell.getCoordinate_Ucs().x;
								int jy = (int) cell.getCoordinate_Ucs().y;
								System.out.println(rank[0] + ". Cell (" + ix + "," + jy + ") = " + cell
										.getTotalOccupants());
								rank[0]++;
							});
			C_Parameters.TERMINATE = false;
			// super.haltSimulation();
		}
	}
}
