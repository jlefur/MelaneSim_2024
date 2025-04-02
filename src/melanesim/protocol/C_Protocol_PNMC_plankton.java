package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Chronogram;
import data.C_Event;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import thing.ground.C_SoilCellMarine;

/** chlorophyll loaded Plankton particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_plankton extends C_Protocol_PNMC_drifters {
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_plankton(Context<Object> ctxt) {
		super(ctxt);
		// TODO number in source 2025.04 JLF CHRONOGRAM FILE NAME
		this.chronogram = new C_Chronogram("/20250402_PNMC.plankton.csv");
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** read chlorophyll values : for year 2021 maximum=0.40059945, minimum = 0.047118366 -> x25 */
	public void manageOneEvent(C_Event event) {
		switch (event.type) {
			case CHLOROPHYLL_EVENT :// file name example: PNMC_chlorophyll_2021/202101.grd
				String url = RASTER_PATH_MELANESIA + "PNMC_chlorophyll_2021/chl-2021";
				int imax = this.landscape.getDimension_Ucell().width;
				int jmax = this.landscape.getDimension_Ucell().height;
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				calendar.setTime(event.when_Ucalendar);
				// Month of simulation begin in 0, we need to +1 the month value and put 0 before month value between 0 and 8
				if (calendar.get(Calendar.MONTH) < 9) url = url + "0" + (calendar.get(Calendar.MONTH) + 1);
				else url = url + (calendar.get(Calendar.MONTH) + 1);
				double[][] matriceLue = C_ReadRasterDouble.doubleRasterLoader(url + ".grd");
				for (int i = 0; i < imax; i++) {
					for (int j = 0; j < jmax; j++) {
						double value = matriceLue[i][j];
						// TODO number in source 2025.04 JLF for xphyl min=1 max=10
						((C_SoilCellMarine) this.landscape.getGrid()[i][j]).setChlorophyll(value * 25);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
}
