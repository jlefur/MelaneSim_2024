package melanesim.protocol;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import data.C_Chronogram;
import data.C_Event;
import data.C_Parameters;
import data.C_ReadRasterDouble;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
import thing.A_Animal;
import thing.A_NDS;
import thing.A_VisibleAgent;
import thing.C_Megaptera;
import thing.C_Ship_cargo;
import thing.I_SituatedThing;
import thing.ground.C_SoilCellMarine;
import thing.ground.I_Container;

/** Sea surface temperature displayed on top of nekton layers Account for whales (Megaptera) JLF 06.2026
 * @author Quoc-Kim BUI & Dan Moulinie 05.2026 */
public class C_Protocol_PNMC_temperature extends C_Protocol_PNMC_ships {
	//
	// FIELDS
	//
	protected GridValueLayer temperatureValueLayer;
	protected C_Chronogram whaleActivitiesChrono;
	protected ArrayList<String> whaleActivitiesList;
	protected TreeSet<C_Megaptera> whalesOutOfDomain = new TreeSet<C_Megaptera>();
	//
	// CONSTRUCTOR
	//
	/** Initialize temperature value layer with random values<br>
	 * Author Quoc-Kim BUI & Dan Moulinie 05.2026 */
	public C_Protocol_PNMC_temperature(Context<Object> ctxt) {
		super(ctxt);
		this.chronogram = new C_Chronogram("/20260603_PNMC.megaptera.csv");
		this.whaleActivitiesChrono = new C_Chronogram(WHALE_ACTIVITY_CHRONO);
		this.whaleActivitiesList = this.whaleActivitiesChrono.getFullEvents_Ustring();
		int gridWidth = this.landscape.dimension_Ucell.width, gridHeight = this.landscape.dimension_Ucell.height;
		// Use TEMPERATURE_GRID name so the existing scenario.xml nekton display finds this
		// layer
		this.temperatureValueLayer = new GridValueLayer(TEMPERATURE_GRID,true,
		        new repast.simphony.space.grid.WrapAroundBorders(),gridWidth,gridHeight);
		for(int i = gridWidth-1; i>=0; i--) for(int j = gridHeight-1; j>=0; j--) this.temperatureValueLayer.set(
		        (int)(Math.random()*13),i,j);
		context.addValueLayer(this.temperatureValueLayer);
		C_Megaptera.init(temperatureValueLayer);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Breeding season for whales JLF 08.206 */
	protected void initFixedParameters() {
		C_Parameters.REPRO_START_Umonth = 7;
		C_Parameters.REPRO_END_Umonth = 10;
		super.initFixedParameters();
	}
	@Override
	// public void initCalendar() { protocolCalendar.set(2021,Calendar.AUGUST,18); }// for whale development
	public void initCalendar() { protocolCalendar.set(2021,Calendar.JULY,15); }// for whale development
	@Override
	/** Specifically manage ships bouncing at the limits and whales entering or leaving domain */
	public void step_Utick() {
		List<A_NDS> agents = new ArrayList<>();
		for(Object obj:context.getObjects(A_NDS.class)) agents.add((A_NDS)obj);
		for(A_NDS oneAgent:agents){
			// Manage whales leaving World
			if(oneAgent instanceof C_Megaptera) checkWhaleLeavingWorld((C_Megaptera)oneAgent);
		}
		// Manage whales reentering World
		List<C_Megaptera> tmp = new ArrayList<C_Megaptera>();
		for(C_Megaptera obj:this.whalesOutOfDomain) tmp.add(obj);
		for(C_Megaptera oneWhale:tmp){
			oneWhale.manageActivities();// check if whale has reentered or left domain
			if(oneWhale.hasEnteredDomain){
				this.whalesOutOfDomain.remove(oneWhale);
				this.contextualizeNewThingInContainer(oneWhale,oneWhale.retrieveMyHome());
				if(C_Parameters.VERBOSE)
				    A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneWhale.toString()+" has ENTERED domain",
				            isNotError);
				for(A_Animal follower:oneWhale.getAnimalsTargetingMe()) //
				    if((follower instanceof C_Megaptera) && this.whalesOutOfDomain.remove(follower)){
					    // this.context.add(follower);
					    follower.hasEnteredDomain = true;
					    follower.hasLeftDomain = false;
					    this.contextualizeNewThingInContainer(follower,follower.retrieveMyHome());
					    if(C_Parameters.VERBOSE)
					        A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",follower.toString()
					                +" has ENTERED domain",isNotError);
				    }
				oneWhale.hasEnteredDomain = false;
				oneWhale.setEnergy_Ukcal(WHALE_ENERGY_Ukcal);
				oneWhale.manageActivities();
			}
		}
		super.step_Utick();
	}
	protected void checkWhaleLeavingWorld(C_Megaptera oneWhale) {
		if(oneWhale.hasLeftDomain && !this.whalesOutOfDomain.contains(oneWhale)){
			A_Protocol.event("C_Protocol_PNMC_ships.step_Utick()",oneWhale.toString()+" has left domain",isNotError);
			I_Container currentCell;// temp variable
			currentCell = oneWhale.getCurrentSoilCell();
			currentCell.agentLeaving((I_SituatedThing)oneWhale);
			if(oneWhale.getTarget() instanceof C_Megaptera) checkWhaleLeavingWorld((C_Megaptera)oneWhale.getTarget());
			oneWhale.discardCellTarget();
			oneWhale.setMyHome(currentCell);
			this.context.remove(oneWhale);
			this.whalesOutOfDomain.add((C_Megaptera)oneWhale);
			for(A_Animal follower:oneWhale.getAnimalsTargetingMe()) if(follower instanceof C_Megaptera && !follower
			        .isa_Tag())
			// && ((C_Megaptera)follower).getActivityList().isEmpty()){
			{
				follower.hasEnteredDomain = false;
				follower.hasLeftDomain = true;
				checkWhaleLeavingWorld((C_Megaptera)follower);
			}
		}
	}
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
				            this.temperatureValueLayer.set(BLACK_MAP_COLOR,i,j);
			        }
		}
	}
	@Override
	/** Read monthly sea surface temperature raster, map values (19-30.5°C) to colormap indices 1-12, mask terrestrial
	 * cells DATE EVENT X Y EVENT VALUES_1 VALUES_2 VALUES_3 17/07/2021 168.063 -23.406 Megaptera 2018-34350 F:J 00:13
	 * 09/11/2016 -14.431 16.4853 Human H0101_Ismaïla_S M:33:FALSE */
	public void manageOneEvent(C_Event event) {
		// Convert coordinates from degree to cell if any
		Coordinate coordinateCell_Ucs = null;
		if(event.whereX_Ucell==null){// then: 1) suppose that y is also null, 2) double are values in decimal degrees
			coordinateCell_Ucs = this.convertCoordinate_Ucs(event.whereX_Udouble,event.whereY_Udouble);
			event.whereX_Ucell = (int)coordinateCell_Ucs.x;
			event.whereY_Ucell = (int)coordinateCell_Ucs.y;
		}
		// Check if event within the domain
		if(coordinateCell_Ucs==null) coordinateCell_Ucs = new Coordinate(event.whereX_Ucell,event.whereY_Ucell);
		Dimension dim = this.landscape.getDimension_Ucell();
		if((coordinateCell_Ucs.x<dim.getWidth()) && (coordinateCell_Ucs.y<dim.getHeight())){
			switch(event.type){
				case WHALE_EVENT:
					String[] whaleData = event.value2.split(EVENT_VALUE2_FIELD_SEPARATOR);
					C_Megaptera oneWhale = new C_Megaptera(event.value1,whaleData[0],"tagged");
					oneWhale.seta_Tag(true);
					C_SoilCellMarine homeCell = (C_SoilCellMarine)this.landscape
					        .getGrid()[event.whereX_Ucell][event.whereY_Ucell];
					oneWhale.setMyHome(homeCell);
					contextualizeNewThingInContainer(oneWhale,homeCell);
					this.initWhaleActivity(oneWhale);
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
					for(int i = 0; i<imax; i++){
						for(int j = 0; j<jmax; j++){
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
	// METHODS
	//
	/** Use the activity list to initialize human activities columns of the file are : ID X Y date time
	 * @author Sall 2020, Le Fur 2026 */
	public void initWhaleActivity(C_Megaptera oneWhale) {
		String[] activities;
		for(int i = 0; i<this.whaleActivitiesChrono.getChronoLength(); i++){
			activities = this.whaleActivitiesList.get(i).split(CSV_FIELD_SEPARATOR);
			String whaleID = oneWhale.retrieveMyId();
			if(whaleID.equals(activities[2]))
			    oneWhale.addActivityList(//
			            this.convertCoordinate_Ucs(//
			                    Double.parseDouble(activities[3]),//
			                    Double.parseDouble(activities[4]))//
			            ,activities[0],activities[1]);
		}
		oneWhale.manageActivities();
	}
	protected void ManageWhaleGroup0(C_Megaptera oneWhale, String groupCode) {}
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
		String sex = oneWhale.testMale()?"M":"F";
		String alterSex = sex.equals("M")?"F":"M";
		C_Megaptera otherWhale;
		switch(groupCode){
			case "K":// K pair -> 1 male, 1 female
				otherWhale = createFollower(oneWhale,alterSex,1);
				if(oneWhale.testMale()) oneWhale.setTarget(otherWhale);
				else otherWhale.setTarget(oneWhale);
				break;
			case "D":// D group of 4 -> 1 female + 3 males
				if(oneWhale.testMale()){
					otherWhale = createFollower(oneWhale,alterSex,0);// female
					oneWhale.setTarget(otherWhale);
					for(int i = 1; i<3; i++) createFollower(oneWhale,sex,i).setTarget(otherWhale);
				}
				else for(int i = 1; i<4; i++) createFollower(oneWhale,alterSex,i).setTarget(oneWhale);// three males
				break;
		}
	}
	protected C_Megaptera createFollower(C_Megaptera oneWhale, String sex, int index) {
		C_Megaptera otherWhale;
		otherWhale = new C_Megaptera(oneWhale.retrieveMyId()+"_"+index,sex,"follower");
		otherWhale.setMyHome(oneWhale.getCurrentSoilCell());
		contextualizeNewThingInContainer(otherWhale,(C_SoilCellMarine)oneWhale.getCurrentSoilCell());
		return otherWhale;
	}
}
