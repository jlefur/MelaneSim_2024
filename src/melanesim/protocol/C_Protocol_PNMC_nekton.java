package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Event;
import data.C_Parameters;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.ground.C_SoilCellMarine;
import thing.ground.landscape.C_LandscapeMarineNekton;

/** Chlorophyll loaded Plankton particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_nekton extends C_Protocol_PNMC_plankton {
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_nekton(Context<Object> ctxt) {
		super(ctxt);
		// int gridWidth=this.landscape.dimension_Ucell.width, gridHeight=this.landscape.dimension_Ucell.height;
		// this.nektonValueLayer = new GridValueLayer(NEKTON_GRID, true, new repast.simphony.space.grid.WrapAroundBorders(),
		// gridWidth, gridHeight);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Init #nektonLandscape @author JLF 2025 */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeMarineNekton(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME,
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
						marineCell = ((C_SoilCellMarine) this.landscape.getGrid()[i][j]);
						marineCell.setMicroNekton(value);
						if (value == 0.) value = 0;
						else if (value > 0 && value <= 0.2) value = 1;
						else if (value > 0.2 && value <= 0.5) value = 2;
						else if (value > 0.5 && value <= 1) value = 3;
						else if (value > 1 && value <= 1.5) value = 4;
						else if (value > 1.5 && value <= 2) value = 5;
						else if (value > 2 && value <= 3) value = 6;
						else value = 7; // value > 3
						((C_LandscapeMarineNekton) this.landscape).getNektonValueLayer().set(value, i, j);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
	/*
	 * @Override public void step_Utick() { super.step_Utick(); C_SoilCellMarine cell = (C_SoilCellMarine)
	 * this.landscape.getGrid()[127][217]; if (cell.getOccupantList().size() >= 1) A_Protocol.event("occupants: ",
	 * cell.getOccupantList().size() + ", énergie: " + Math.round(cell .getEnergy_Ukcal() * 100.0) / 100.0 + ", xphylle: " +
	 * Math.round(cell.getChlorophyll() * 100.0) / 100.0, isError); }
	 */
}
