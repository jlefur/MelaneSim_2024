package melanesim.protocol;

import java.awt.Toolkit;
import java.util.Calendar;
import java.util.TimeZone;

import org.locationtech.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.C_ReadRasterDouble;
import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertGeographicCoordinates;
import presentation.display.C_Background;
import repast.simphony.context.Context;
import thing.C_Nekton;
import thing.C_Plankton;
import thing.C_StreamCurrent;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_SoilCellMarine;
import thing.ground.landscape.C_LandscapeMarine;

/** Particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_drifters extends A_Protocol_PNMC {
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_drifters(Context<Object> ctxt) {
		super(ctxt);
		// Create and build the dataFromChrono from the csv file
		// TODO number in source 2025.04 JLF CHRONOGRAM FILE NAME
		// if (this.chronogram == null) chronogram = new
		// C_Chronogram("/20240314_PNMC.drifters.csv");
		this.chronogram = new C_Chronogram("/20250701_PNMC.microNekton.csv");
		facilityMap = new C_Background(-2.35, 206., 134.);
	}
	//
	// SPECIFIC METHODS
	//
	public C_Plankton createPlankton() {
		return new C_Plankton(new C_GenomeAnimalia());
	}
	public C_Nekton createNekton() {
		return new C_Nekton(new C_GenomeAnimalia());
	}

	/** Add plankton and micronekton particle in the center of each cell of the grid at a specified interval - JLF 07.2024 */
	protected void initPopulations() {
		int particleCount = 0;
		int countHeight = 1;
		int countWidth = 1;
		java.awt.Dimension dim = this.landscape.getDimension_Ucell();
		int grid_width = (int) dim.getWidth();
		int grid_height = (int) dim.getHeight();
		C_SoilCellMarine cell;
		for (int i = 1; i < grid_width + 1; i++) {
			if (countWidth == PARTICLE_CELLS_SPACING) {
				countHeight = 1;
				for (int j = 1; j < grid_height + 1; j++) {
					if (countHeight == PARTICLE_CELLS_SPACING) {
						cell = (C_SoilCellMarine) this.landscape.getGrid()[i - 1][j - 1];
						if (cell.getAffinity() < TERRESTRIAL_MIN_AFFINITY) {
							this.contextualizeNewThingInContainer(createPlankton(), cell);
							// TODO 10.2025 patch pas très robuste
							if (C_Parameters.PROTOCOL.equals(PNMC_NEKTON) || C_Parameters.PROTOCOL.equals(PNMC_SHIPS))
								this.contextualizeNewThingInContainer(createNekton(), cell);
							cell.setMyCurrent(new C_StreamCurrent(cell.getAffinity(), i - 1, j - 1));
							contextualizeNewThingInSpace(cell.getMyCurrent(), i - 1, j - 1);
							particleCount++;
						}
						countHeight = 1;
					}
					else countHeight++;
				}
				countWidth = 1;
			}
			else countWidth++;
		}
		System.out.println("C_Protocol_PNMC_drifters.init: Population of " + particleCount
				+ " plankton agent(s) created and positioned at the center of each grid cell");
		if (C_Parameters.PROTOCOL.equals(PNMC_NEKTON) || C_Parameters.PROTOCOL.equals(PNMC_SHIPS))
			System.out.println("C_Protocol_PNMC_drifters.init: Population of " + particleCount
					+ " micronekton agent(s) created and positioned at the center of each grid cell");
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Initialize the protocol with the raster origin */
	public void initProtocol() {
		this.geographicCoordinateConverter = new C_ConvertGeographicCoordinates(new Coordinate(
				I_ConstantPNMC.rasterLongitudeWest_LatitudeSouth_Udegree.get(0),
				I_ConstantPNMC.rasterLongitudeWest_LatitudeSouth_Udegree.get(1)));
		this.initPopulations();
		super.initProtocol();
		if (C_Parameters.DISPLAY_MAP && this.facilityMap != null) DISPLAY_FACILITY_MAP = true;
	}

	@Override
	public void manageOneEvent(C_Event event) {
		C_SoilCellMarine cell = null;
		switch (event.type) {
			case COMPUTE_ENERGY :// Permet de suivre l'évolution saisonnière par exemple
				// ((C_LandscapeMarine) this.landscape).assertCellsEnergy();
				// saveScreen();
				// Toolkit.getDefaultToolkit().beep();
				break;
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
						((C_SoilCellMarine) this.landscape.getGrid()[i][j]).setSpeedEastward_UmeterPerSecond(value);
					}
				}
				matriceLue = C_ReadRasterDouble.doubleRasterLoader(url + "_North.grd");
				for (int i = 0; i < imax; i++) {
					for (int j = 0; j < jmax; j++) {
						double value = matriceLue[i][j];
						cell = ((C_SoilCellMarine) this.landscape.getGrid()[i][j]);
						cell.setSpeedNorthward_UmeterPerSecond(value);
						if (cell.getMyCurrent() != null) cell.getMyCurrent().setHasToSwitchFace(true);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
}
