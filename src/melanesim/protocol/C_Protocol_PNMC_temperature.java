package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Event;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.ground.C_SoilCellMarine;

/** Sea surface temperature displayed on top of nekton layers
 * @author Quoc-Kim BUI & Dan Moulinie 05.2026 */
public class C_Protocol_PNMC_temperature extends C_Protocol_PNMC_nekton {
	//
	// FIELD
	//
	protected GridValueLayer temperatureValueLayer;
	//
	// CONSTRUCTOR
	//
	/** Initialize temperature value layer with random values<br>
	 * Author Quoc-Kim BUI & Dan Moulinie 05.2026 */
	public C_Protocol_PNMC_temperature(Context<Object> ctxt) {
		super(ctxt);
		int gridWidth = this.landscape.dimension_Ucell.width, gridHeight = this.landscape.dimension_Ucell.height;
		// Use NEKTON_GRID name so the existing scenario.xml nekton display finds this
		// layer
		this.temperatureValueLayer = new GridValueLayer(TEMPERATURE_GRID, true,
				new repast.simphony.space.grid.WrapAroundBorders(), gridWidth, gridHeight);
		for (int i = gridWidth - 1; i >= 0; i--)
			for (int j = gridHeight - 1; j >= 0; j--)
				this.temperatureValueLayer.set((int) (Math.random() * 13), i, j);
		context.addValueLayer(this.temperatureValueLayer);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Read monthly sea surface temperature raster, map values (19-30.5°C) to colormap indices 1-12, mask terrestrial cells */
	public void manageOneEvent(C_Event event) {
		switch (event.type) {
		case TEMPERATURE_EVENT:
			C_SoilCellMarine marineCell = null;
			String url = RASTER_PATH_MELANESIA + "PNMC_temperatures_2021/temperature-2021";
			int imax = this.landscape.getDimension_Ucell().width;
			int jmax = this.landscape.getDimension_Ucell().height;
			int colorMapValue;
			Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
			calendar.setTime(event.when_Ucalendar);
			// Month of simulation begin in 0, we need to +1 the month value and put 0
			// before month value between 0 and 8
			if (calendar.get(Calendar.MONTH) < 9)
				url = url + "0" + (calendar.get(Calendar.MONTH) + 1);
			else
				url = url + (calendar.get(Calendar.MONTH) + 1);
			double[][] matriceLue = C_ReadRasterDouble.doubleRasterLoader(url + ".grd");
			for (int i = 0; i < imax; i++) {
				for (int j = 0; j < jmax; j++) {
					double value = matriceLue[i][j];
					marineCell = ((C_SoilCellMarine) this.landscape.getGrid()[i][j]);
					// classement des valeurs pour colorMap
					// temp min and max are 19. & 30.5, provide colormap values from 1 to 12
					colorMapValue = (int) value - 18;
					this.temperatureValueLayer.set(colorMapValue, i, j);
					if (marineCell.isTerrestrial())
						this.temperatureValueLayer.set(0, i, j);
					// Intégration de la valeur normalisée dans marine cells
					value = convertTo100(matriceLue[i][j], TEMPERATURE_MIN, TEMPERATURE_MAX);
					marineCell.setTemperature(value);
				}
			}
			break;
		}
		super.manageOneEvent(event);
	}
}
