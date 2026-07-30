package melanesim.protocol;

import java.util.Calendar;
import java.util.TimeZone;

import data.C_Event;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.ground.C_SoilCellMarine;
import thing.ground.C_SoilCellMarineEnergy.Champ;
import thing.I_MarineActor.DriverType;

/** Chlorophyll loaded Plankton particles drifted by surface currents
 * @author J.Le Fur 06.2024 */
public class C_Protocol_PNMC_plankton extends C_Protocol_PNMC_drifters {
	//
	// FIELD
	//
	protected GridValueLayer planktonValueLayer;
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declare them to the panelInitializer for indicators
	 * graphs<br>
	 * Author J.Le Fur 02.2013 */
	public C_Protocol_PNMC_plankton(Context<Object> ctxt) {
		super(ctxt);
		int gridWidth = this.landscape.dimension_Ucell.width, gridHeight = this.landscape.dimension_Ucell.height;
		this.planktonValueLayer = new GridValueLayer(PLANKTON_GRID,true,
		        new repast.simphony.space.grid.WrapAroundBorders(),gridWidth,gridHeight);
		for(int i = gridWidth-1; i>=0; i--) for(int j = gridHeight-1; j>=0; j--) this.planktonValueLayer.set((int)(Math
		        .random()*10),i,j);// TODO JLF 06.2026 GRAVE random generator not managed
		context.addValueLayer(this.planktonValueLayer);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Color the map in black as an alternate view of particles<br>
	 * Author J.Le Fur 10.2014 TODO JLF 2014.10 should be in presentation package ? */
	protected void blackMap() {
		super.blackMap();
		if(this.landscape!=null){
			for(int i = 0; i<this.landscape.getDimension_Ucell().getWidth(); i++) for(int j = 0; j<this.landscape
			        .getDimension_Ucell().getHeight(); j++){
				        C_SoilCellMarine cell = (C_SoilCellMarine)this.landscape.getGrid()[i][j];
				        if(!cell.isTerrestrial()) // marine area
				            this.planktonValueLayer.set(BLACK_MAP_COLOR,i,j);
			        }
		}
	}
	@Override
	/** read chlorophyll values */
	public void manageOneEvent(C_Event event) {
		switch(event.type){
			case CHLOROPHYLL_EVENT:// file name example: PNMC_chlorophyll_2021/202101.grd
				C_SoilCellMarine marineCell = null;
				String url = RASTER_PATH_MELANESIA+"PNMC_chlorophyll_2021/chl-2021";
				int imax = this.landscape.getDimension_Ucell().width;
				int jmax = this.landscape.getDimension_Ucell().height;
				Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
				calendar.setTime(event.when_Ucalendar);
				// Month of simulation begin in 0, we need to +1 the month value and put 0 before month value between 0
				// and 8
				if(calendar.get(Calendar.MONTH)<9) url = url+"0"+(calendar.get(Calendar.MONTH)+1);
				else url = url+(calendar.get(Calendar.MONTH)+1);
				double[][] matriceLue = C_ReadRasterDouble.doubleRasterLoader(url+".grd");
				for(int i = 0; i<imax; i++){
					for(int j = 0; j<jmax; j++){
						double rawValue = matriceLue[i][j];
						double value_100 = convertTo100(rawValue,CHLORO_MIN,CHLORO_MAX);
						marineCell = ((C_SoilCellMarine)this.landscape.getGrid()[i][j]);
						if(!marineCell.isTerrestrial()){
							marineCell.set(DriverType.PLANKTON,Champ.RAW_VAL,rawValue);
							marineCell.set(DriverType.PLANKTON,Champ._100,value_100);
							this.planktonValueLayer.set(value_100/10.,i,j);
						}
						else this.planktonValueLayer.set(TERRESTRIAL_MIN_AFFINITY,i,j);
					}
				}
				break;
		}
		super.manageOneEvent(event);
	}
}
