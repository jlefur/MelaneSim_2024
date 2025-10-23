package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Event;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import thing.ground.C_SoilCellMarine;

/** Chlorophyll loaded Plankton particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_plankton extends C_Protocol_PNMC_drifters {
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_plankton(Context<Object> ctxt) {
		super(ctxt);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** read chlorophyll values */
	public void manageOneEvent(C_Event event) {
		switch (event.type) {
			case CHLOROPHYLL_EVENT :// file name example: PNMC_chlorophyll_2021/202101.grd
				C_SoilCellMarine marineCell = null;
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
						value = convertTo100(value, CHLORO_MIN, CHLORO_MAX);
						marineCell = ((C_SoilCellMarine) this.landscape.getGrid()[i][j]);
						if (!marineCell.isTerrestrial()) {
							marineCell.setChlorophyll(value);
							marineCell.setAffinity((int) (value / 10.));// for xphyl min=0 max=9
						}
						// this.landscape.getGridValueLayer().set( value - 1, i, j);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
}
