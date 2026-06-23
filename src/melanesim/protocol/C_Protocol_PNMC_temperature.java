package melanesim.protocol;

import java.awt.Dimension;
import java.util.Calendar;
import java.util.TimeZone;

import org.locationtech.jts.geom.Coordinate;

import data.C_Event;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.C_Megaptera;
import thing.ground.C_SoilCellMarine;

/** Sea surface temperature displayed on top of nekton layers Account for whales (Megaptera) JLF 06.2026
 * @author Quoc-Kim BUI & Dan Moulinie 05.2026 */
public class C_Protocol_PNMC_temperature extends C_Protocol_PNMC_ships {
	//
	// FIELDS
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
		this.temperatureValueLayer = new GridValueLayer(TEMPERATURE_GRID,true,
		        new repast.simphony.space.grid.WrapAroundBorders(),gridWidth,gridHeight);
		for(int i = gridWidth-1;i>=0;i--) for(int j = gridHeight-1;j>=0;j--) this.temperatureValueLayer.set((int)(Math
		        .random()*13),i,j);
		context.addValueLayer(this.temperatureValueLayer);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Read monthly sea surface temperature raster, map values (19-30.5°C) to colormap indices 1-12, mask terrestrial
	 * cells DATE EVENT X Y EVENT VALUES_1 VALUES_2 VALUES_3 17/07/2021 168.063 -23.406 Megaptera 2018-34350 F:J 00:13
	 * 09/11/2016 -14.431 16.4853 Human H0101_Ismaïla_S M:33:FALSE */
	public void manageOneEvent(C_Event event) {
		// convert coordinates from degree to cell if any
		Coordinate coordinateCell_Ucs = null;
		if(event.whereX_Ucell==null){// then: 1) suppose that y is also null, 2) double are values in decimal degrees
			coordinateCell_Ucs = this.geographicCoordinateConverter.convertCoordinate_Ucs(event.whereX_Udouble,
			        event.whereY_Udouble);
			event.whereX_Ucell = (int)coordinateCell_Ucs.x;
			event.whereY_Ucell = (int)coordinateCell_Ucs.y;
		}
		// check if event withih the domain
		if(coordinateCell_Ucs==null) coordinateCell_Ucs = new Coordinate(event.whereX_Ucell,event.whereY_Ucell);
		Dimension dim = this.landscape.getDimension_Ucell();
		if((coordinateCell_Ucs.x<dim.getWidth())&&(coordinateCell_Ucs.y<dim.getHeight())){
			switch(event.type){
				case WHALE_EVENT:
					String[] whaleData = event.value2.split(EVENT_VALUE2_FIELD_SEPARATOR);
					C_Megaptera oneWhale = new C_Megaptera(event.value1,whaleData[0]);
					C_SoilCellMarine homeCell = (C_SoilCellMarine)this.landscape
					        .getGrid()[event.whereX_Ucell][event.whereY_Ucell];
					oneWhale.setMyHome(homeCell);
					contextualizeNewThingInContainer(oneWhale,homeCell);
					String groupCode = whaleData[1];
					ManageWhaleGroup(oneWhale,groupCode);
					break;
				case TEMPERATURE_EVENT:
					C_SoilCellMarine marineCell = null;
					String url = RASTER_PATH_MELANESIA+"PNMC_temperatures_2021/temperature-2021";
					int imax = this.landscape.getDimension_Ucell().width;
					int jmax = this.landscape.getDimension_Ucell().height;
					int colorMapValue;
					Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
					calendar.setTime(event.when_Ucalendar);
					// Month of simulation begin in 0, we need to +1 the month value and put 0
					// before month value between 0 and 8
					if(calendar.get(Calendar.MONTH)<9) url = url+"0"+(calendar.get(Calendar.MONTH)+1);
					else url = url+(calendar.get(Calendar.MONTH)+1);
					double[][] matriceLue = C_ReadRasterDouble.doubleRasterLoader(url+".grd");
					for(int i = 0;i<imax;i++){
						for(int j = 0;j<jmax;j++){
							double value = matriceLue[i][j];
							marineCell = ((C_SoilCellMarine)this.landscape.getGrid()[i][j]);
							// classement des valeurs pour colorMap
							// temp min and max are 19. & 30.5, provide colormap values from 1 to 12
							colorMapValue = (int)value-18;
							if(marineCell.isTerrestrial()) this.temperatureValueLayer.set(0,i,j);
							else{
								this.temperatureValueLayer.set(colorMapValue,i,j);
								// Intégration de la valeur normalisée dans marine cells
								value = convertTo100(matriceLue[i][j],TEMPERATURE_MIN,TEMPERATURE_MAX);
								marineCell.setTemperature(value);
							}
						}
					}
					break;
			}
		}
		super.manageOneEvent(event);
	}
	//
	// METHOD
	//
	protected void ManageWhaleGroup(C_Megaptera oneWhale, String groupCode) {
		// G solitary -> 1
		// K pair -> 1 male, 1 female
		// D group of 4 -> 1 female + 3 males
		// H group of 3 -> 0 females + 2 males
		// C group of 2 to 3 -> 1 female + 2 males
		// F competitor group with female and calf - -> 1 female + 1 young (sex random) + 2 males
		// J competitor group (+ female) -> 1 female + 2 males
		// E female and calf with escort -> 1 female + 1 young (sex random) + 2 males
		// A female and calf in competitor group -> 1 female + 1 young (sex random) + 2 males
		// I female and calf -> 1 female + 1 young (sex random)
		// B escort of a female and calf -> 1 female + 2 males

	}
}
