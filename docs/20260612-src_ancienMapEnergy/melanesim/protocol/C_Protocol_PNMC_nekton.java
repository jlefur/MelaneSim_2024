package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Event;
import data.C_Parameters;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.ground.C_SoilCellMarine;

/** Chlorophyll loaded Plankton particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_nekton extends C_Protocol_PNMC_plankton {
	//
	// FIELD
	//
	protected GridValueLayer nektonValueLayer;
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_nekton(Context<Object> ctxt) {
		super(ctxt);
		int gridWidth = this.landscape.dimension_Ucell.width, gridHeight = this.landscape.dimension_Ucell.height;
		this.nektonValueLayer = new GridValueLayer(NEKTON_GRID, true,
				new repast.simphony.space.grid.WrapAroundBorders(), gridWidth, gridHeight);
		for (int i = gridWidth - 1; i >= 0; i--)
			for (int j = gridHeight - 1; j >= 0; j--)
				this.nektonValueLayer.set((int) (Math.random() * 7), i, j);
		context.addValueLayer(this.nektonValueLayer);
	}

	@Override
	/** Read microNekton values */
	public void manageOneEvent(C_Event event) {
		switch (event.type) {
			case NEKTON_EVENT :
				C_SoilCellMarine marineCell = null;
				String url = RASTER_PATH_MELANESIA + "PNMC_microNekton_2021/microNekton-2021";
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
						// classement des valeurs pour colorMap
						if (value == 0.) value = 0;
						else if (value > 0 && value <= 0.2) value = 1;
						else if (value > 0.2 && value <= 0.5) value = 2;
						else if (value > 0.5 && value <= 1) value = 3;
						else if (value > 1 && value <= 1.5) value = 4;
						else if (value > 1.5 && value <= 2) value = 5;
						else if (value > 2 && value <= 3) value = 6;
						else value = 7; // value > 3
						this.nektonValueLayer.set(value, i, j);
						// Intégration de la valeur normalisée dans marine cells
						value = convertTo100(matriceLue[i][j], NEKTON_MIN, NEKTON_MAX);
						marineCell = ((C_SoilCellMarine) this.landscape.getGrid()[i][j]);
						marineCell.setMicroNekton(value);
						marineCell.setTotalNektonDensity(value * marineCell.getNektonPopulation());
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}

}
