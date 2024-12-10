package melanesim.protocol;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

import org.locationtech.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.C_ReadRasterDouble;
import data.constants.I_ConstantPNMC_particules;
import data.converters.C_ConvertGeographicCoordinates;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet;
import presentation.epiphyte.C_InspectorEnergy;
import presentation.epiphyte.C_InspectorPopulationMarine;
import repast.simphony.context.Context;
import repast.simphony.engine.environment.RunState;
import thing.C_Plankton;
import thing.C_StreamCurrent;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_LandPlot;
import thing.ground.C_MarineCell;
import thing.ground.C_SoilCell;
import thing.ground.landscape.C_LandscapeMarine;

/** Plankton particles moved by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_particules extends A_Protocol implements I_ConstantPNMC_particules {
	//
	// FIELDS
	//
	private C_ConvertGeographicCoordinates geographicCoordinateConverter = null;
	protected C_InspectorPopulationMarine marineInspector;
	Random random = new Random();
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_particules(Context<Object> ctxt) {
		super(ctxt);
		// Create and build the dataFromChrono from the csv file
		this.chronogram = new C_Chronogram(CHRONO_FILENAME);
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
		// INSPECTOR
		A_Protocol.inspector = new C_InspectorPopulationMarine();
		inspectorList.add(inspector);
		A_Protocol.inspectorEnergy = new C_InspectorEnergy();
		this.inspectorList.add(inspectorEnergy);
		C_CustomPanelSet.addEnergyInspector(inspectorEnergy);
		facilityMap = new C_Background(-2.35, 206., 134.);
	}
	//
	// SPECIFIC METHODS
	//
	public C_Plankton createPlankton() {
		return new C_Plankton(new C_GenomeAnimalia());
	}
	/** Add plankton particle in the center of each cell of the grid at a specified interval - JLF 07.2024 */
	protected void initPopulations() {
		int particleCount = 0;
		int countHeight = 0;
		int countWidth = 0;
		java.awt.Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		C_MarineCell cell;
		for (int i = 0; i < grid_width; i++) {
			if (countWidth == PLANKTON_CELLS_SPACING) {
				countHeight = 0;
				for (int j = 0; j < grid_height; j++) {
					if (countHeight == PLANKTON_CELLS_SPACING) {
						cell = (C_MarineCell) this.landscape.getGrid()[i][j];
						if (cell.getAffinity() < TERRESTRIAL_MIN_AFFINITY) {
							this.contextualizeNewThingInContainer(createPlankton(), cell);
							cell.setMyCurrent(new C_StreamCurrent(cell.getAffinity(), i, j));
							contextualizeNewThingInSpace(cell.getMyCurrent(), i, j);
							particleCount++;
						}
						countHeight = 0;
					}
					else countHeight++;
				}
				countWidth = 0;
			}
			else countWidth++;
		}
		System.out.println("C_Protocol_PNMC_particules.init: Population of " + particleCount
				+ " plankton agent(s) created and positioned at the center of each grid cell");
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Initialize the protocol with the raster origin */
	public void initProtocol() {
		this.geographicCoordinateConverter = new C_ConvertGeographicCoordinates(new Coordinate(
				I_ConstantPNMC_particules.rasterLongitudeWest_LatitudeSouth_Udegree.get(0),
				I_ConstantPNMC_particules.rasterLongitudeWest_LatitudeSouth_Udegree.get(1)));
		this.initPopulations();
		super.initProtocol();
		if (C_Parameters.DISPLAY_MAP)
			if (this.facilityMap != null) this.facilityMap.contextualize(this.context, this.landscape);
	}

	@Override
	/** set grid content to C_MarineCell, JLF 2024 */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeMarine(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME,
				CONTINUOUS_SPACE_NAME));
		// Comment the following lines to undisplay soil cells, JLF 10.2015, 11.2015
		for (int i = 0; i < this.landscape.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.landscape.dimension_Ucell.height; j++) {
				C_MarineCell cell = new C_MarineCell(this.landscape.getGrid()[i][j].getAffinity(), i, j);
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
		// // slightly randomly move the mouse to avoid screen sleep mode (for
		// recording printscreen)
		// try {
		// Robot robot = new Robot();
		// // Get the current mouse coordinates
		// Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		// int x = mouseLocation.x;
		// int y = mouseLocation.y;
		// robot.mouseMove(x + random.nextInt(3), y + random.nextInt(3));
		// } catch (AWTException e) {e.printStackTrace();}

		// save screen each new day
		/*
		 * Integer currentYear = A_Protocol.protocolCalendar.get(Calendar.YEAR); Integer currentDay =
		 * A_Protocol.protocolCalendar.get(Calendar.DAY_OF_YEAR); Integer currentHour =
		 * A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY); String currentDate = currentYear + "." + String.format("%03d",
		 * currentDay) + "_" + String.format("%03d",currentHour);
		 */
		A_Protocol.protocolCalendar.incrementDate();
		// reset plankton energy JLF 07.2024
		// if (protocolCalendar.get(Calendar.DAY_OF_YEAR) != currentDay) {
		Object[] contextContent = RunState.getInstance().getMasterContext().toArray();
		for (int i = 0; i < contextContent.length; i++) {
			Object item = contextContent[i];
			if (item instanceof C_Plankton) ((C_Plankton) item).energy_Ukcal = 0.;
			// }
		}
		// uncomment line below to save screen
		// CaptureEcranPeriodique.captureEcranPlankton(currentDate);
		// Check if map has to be switched Version JLF 08.2014, rev.10.2015, 05.2017
		boolean displayMapBefore = C_Parameters.DISPLAY_MAP;
		this.readUserParameters();
		if (displayMapBefore != C_Parameters.DISPLAY_MAP) switchDisplayMap();
		// if (C_Parameters.VERBOSE) C_sound.sound("tip.wav");
	}
	@Override
	public void manageOneEvent(C_Event event) {
		Coordinate coordinateCell_Ucs = null;
		C_MarineCell cell = null;
		if (event.whereX_Ucell == null) {// then: 1) suppose that y is also null, 2) double are values in decimal
											// degrees
			coordinateCell_Ucs = this.geographicCoordinateConverter.convertCoordinate_Ucs(event.whereX_Udouble,
					event.whereY_Udouble);
			event.whereX_Ucell = (int) coordinateCell_Ucs.x;
			event.whereY_Ucell = (int) coordinateCell_Ucs.y;
		}
		if (coordinateCell_Ucs == null) coordinateCell_Ucs = new Coordinate(event.whereX_Ucell, event.whereY_Ucell);
		switch (event.type) {
			case CURRENT_EVENT :// file name example: PNMC_current_2021/202101_North.grd and
								// PNMC_current_2021/202101_East.grd
				String url;
				int imax = this.landscape.getDimension_Ucell().width;
				int jmax = this.landscape.getDimension_Ucell().height;
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				calendar.setTime(event.when_Ucalendar);
				// Month of simulation begin in 0, we need to +1 the month value and put 0
				// before month value between 0 and 8
				if (calendar.get(Calendar.MONTH) < 9)
					url = RASTER_PATH_MELANESIA + "PNMC_current_2021/2021"// + calendar.get(Calendar.YEAR)
							+ "0" + (calendar.get(Calendar.MONTH) + 1);
				else
					url = RASTER_PATH_MELANESIA + "PNMC_current_2021/2021"// + calendar.get(Calendar.YEAR)
							+ (calendar.get(Calendar.MONTH) + 1);
				double[][] matriceLue = C_ReadRasterDouble.doubleRasterLoader(url + "_East.grd");
				for (int i = 0; i < imax; i++) {
					for (int j = 0; j < jmax; j++) {
						double value = matriceLue[i][j];
						((C_MarineCell) this.landscape.getGrid()[i][j]).setSpeedEastward_UmeterPerSecond(value);
					}
				}
				matriceLue = C_ReadRasterDouble.doubleRasterLoader(url + "_North.grd");
				for (int i = 0; i < imax; i++) {
					for (int j = 0; j < jmax; j++) {
						double value = matriceLue[i][j];
						cell = ((C_MarineCell) this.landscape.getGrid()[i][j]);
						cell.setSpeedNorthward_UmeterPerSecond(value);
						if (cell.getMyCurrent() != null) cell.getMyCurrent().setHasToSwitchFace(true);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
	@Override
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++)
			for (int j = 0; j < this.landscape.getDimension_Ucell().getHeight(); j++) {
				if (this.landscape.getValueLayer().get(i, j) < TERRESTRIAL_MIN_AFFINITY) // marine area
					this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
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
			A_Protocol.event("C_Protocol_PNMC_particules.readUserParameters", "meta-population set to "
					+ C_Parameters.EXCLOS, isNotError);
	}

}
