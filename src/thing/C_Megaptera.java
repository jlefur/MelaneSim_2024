package thing;

import java.util.Calendar;
import java.util.TreeMap;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertTimeAndSpace;
import melanesim.protocol.A_Protocol;
import repast.simphony.valueLayer.GridValueLayer;
import thing.dna.I_DiploidGenome;
import thing.dna.species.C_GenomeMegaptera;
import thing.ground.C_SoilCellMarine;
/** @author JLF 06.2026 */
public class C_Megaptera extends A_Amniote implements I_MarineActor, I_ConstantPNMC {
	//
	// FIELD
	//
	private TreeMap<String,Coordinate> activityList = new TreeMap<String,Coordinate>();
	protected static GridValueLayer MyvalueLayer;
	//
	// CONSTRUCTOR
	//
	public C_Megaptera(I_DiploidGenome genome) {
		super(new C_GenomeMegaptera());
		this.setAge_Uday(1.);
	}
	public C_Megaptera(String myID,String sex,String tagged) {
		this(new C_GenomeMegaptera());
		this.setMyName("whale."+sex+"-"+tagged+NAMES_SEPARATOR+myID);
		if(sex.equals("M")) this.setMale(true);
		else this.setMale(false);
		this.setAge_Uday(4380.);// 12 years TODO jlf 06.2026 number in source age at creation adult whales
		this.sexualMature = true;
		if(this.testFemale()) this.setDesire(WANDER);
		this.energy_Ukcal = WHALE_ENERGY_Ukcal;// TODO number in source NOT OK JLF 08.2026 energy whales
		if(C_Parameters.VERBOSE) A_Protocol.event("C_Megaptera.C_Megaptera(): ",this.toString()+" CREATED",isNotError);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public boolean actionMateWithMale(I_ReproducingThing male) { return false; }
	@Override
	public void step_Utick() {
		super.step_Utick();
		this.manageActivities();
	}
	@Override
	protected void checkDanger() {}
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) { return new C_Megaptera(genome); }
	@Override
	public void discardThis() {
		this.activityList = null;
		super.discardThis();
	}
	/** Compute next move to target, move on the GUI */
	@Override
	protected void actionMoveToDestination() {
		super.actionMoveToDestination();
		// if (C_Parameters.VERBOSE)
		// A_VisibleAgent.myLandscape.getValueLayer().set(10,this.currentSoilCell.retrieveLineNo(),this.currentSoilCell.retrieveColNo());//
		// @@vert bouteille
		// MyvalueLayer.set(10,this.currentSoilCell.retrieveLineNo(),this.currentSoilCell.retrieveColNo());// @@vert
		// bouteille
	}
	/** Get a random one; then actionMove, then reset nextMove <br>
	 * JLF 02.2018 */
	public void actionWander() {
		this.setNewRandomMove(this.speed_UmeterByTick/SLOW_FACTOR);
		this.actionMove();
	}
	//
	// METHODS
	//
	/** Whales leaving domain have to remove their cell target but not the whale they target if any */
	public void discardCellTarget() {
		if((this.target!=null) && (this.target instanceof C_SoilCellMarine)){
			if(!((A_VisibleAgent)this.target).animalsTargetingMe.remove(this))
			    A_Protocol.event("C_Megaptera.discardCellTarget()","could not remove ref. to "+this+" in "+this.target,
			            isError);
			this.target = null;// continue anyway
		} // else target is already null
	}
	/** Declare the temperature valueLayer
	 * @author JLF 07.2026 */
	public static void init(GridValueLayer valueLayer) { MyvalueLayer = valueLayer; }
	public void addActivityList(Coordinate location, String date, String time) {
		String iMonth = date.substring(3,5);
		String iDay = date.substring(0,2);
		String dateKey = iMonth+iDay+"-"+time;
		this.activityList.put(dateKey,location);
	}
	/** Move the tracked whale<br>
	 * @author MS 08.2021, JLF 07.2026 */
	public void manageActivities() {
		Object[] keys = this.activityList.keySet().toArray();
		int targetX, targetY;
		for(int i = 0; i<this.activityList.size(); i++){
			if(this.isIncludedInTimeStepInterval((String)keys[i])){
				Coordinate target = this.activityList.get(keys[i]);
				targetX = (int)target.getX();
				targetY = (int)target.getY();
				if(targetX>=0 && targetX<A_VisibleAgent.myLandscape.getDimension_Ucell().getWidth()// If within domain
				        && targetY>=0 && targetY<A_VisibleAgent.myLandscape.getDimension_Ucell().getHeight()){
					if(this.hasLeftDomain){ // reentering the domain (only switch tags in/out then return to protocol)
						this.hasEnteredDomain = true;
						for(A_Animal follower:this.animalsTargetingMe){
							follower.hasLeftDomain = false;
							follower.hasEnteredDomain = true;
						}
						this.hasLeftDomain = false;
						return;
					}
					else{ // if within domain
						A_VisibleAgent.myLandscape.moveToLocation(this,target);
						A_VisibleAgent.myLandscape.translate(this,new Coordinate(.01,.01));// Position whale in its cell
						this.setTarget((C_SoilCellMarine)A_VisibleAgent.myLandscape.getGrid()[targetX][targetY]);
						C_Megaptera.MyvalueLayer.set(BLACK_MAP_COLOR,this.currentSoilCell.retrieveLineNo(),
						        this.currentSoilCell.retrieveColNo());
						A_VisibleAgent.myLandscape.getValueLayer().set(BLACK_MAP_COLOR,this.currentSoilCell
						        .retrieveLineNo(),this.currentSoilCell.retrieveColNo());
						this.computeMaxDispersalDistance_Umeter();
					}
				}
				else{ // is out of domain; switch tags in/out
					this.hasLeftDomain = true;
					for(A_Animal follower:this.animalsTargetingMe){
						follower.hasLeftDomain = true;
						follower.hasEnteredDomain = false;
					}
					this.hasEnteredDomain = false;
				}
			}
		}
	}
	//
	// GETTERS
	//
	public String retrieveMyId() { return this.myName.split(NAMES_SEPARATOR,2)[1]; }

	/** key is in the form "MMDD-HH:MM" @author JLF 07.2026 */
	public boolean isIncludedInTimeStepInterval(String key) {
		Boolean flag = false;
		int iMonth = Integer.parseInt(key.substring(0,2))-1;
		int iDay = Integer.parseInt(key.substring(2,4));
		if((A_Protocol.protocolCalendar.get(Calendar.MONTH)==iMonth) && (A_Protocol.protocolCalendar.get(
		        Calendar.DAY_OF_MONTH)==iDay)){
			key = key.substring(5);
			String[] keyTime = key.split(HOUR_MINUTE_SEPARATOR);// get hour, minute
			String tickUnit = C_ConvertTimeAndSpace.tick_UcalendarUnit;
			double simulationTime_Utick = this.getCurrentSimulationTime_Utick(tickUnit);
			switch(tickUnit){
				case "H":{
					double activityTime = Double.parseDouble(keyTime[0])+Double.parseDouble(keyTime[1])/60;
					if(((simulationTime_Utick-activityTime)>=0) && ((simulationTime_Utick
					        -activityTime)<=C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
				}
					break;
				case "M":{
					double activityTime = Double.parseDouble(keyTime[0])*60+Double.parseDouble(keyTime[1]);
					if(((simulationTime_Utick-activityTime)>=0) && ((simulationTime_Utick
					        -activityTime)<=C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
				}
					break;
				case "S":{
					double activityTime = Double.parseDouble(keyTime[0])*3600+(Double.parseDouble(keyTime[1])*60);
					if(((simulationTime_Utick-activityTime)>=0) && ((simulationTime_Utick
					        -activityTime)<=C_ConvertTimeAndSpace.tick_Ucalendar)) flag = true;
				}
					break;
			}
		}
		return flag;
	}
	/** TODO MS de JLF 03.2021 inclure MS, D, MON, Y */
	public double getCurrentSimulationTime_Utick(String unit) {
		double simulationTime = .0;
		switch(unit){
			case "H":
				simulationTime = A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY)+(A_Protocol.protocolCalendar.get(
				        Calendar.MINUTE)/60);
				break;
			case "M":
				simulationTime = (A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY)*60)+A_Protocol.protocolCalendar
				        .get(Calendar.MINUTE);
				break;
			case "S":
				simulationTime = (A_Protocol.protocolCalendar.get(Calendar.HOUR_OF_DAY)*3600)
				        +(A_Protocol.protocolCalendar.get(Calendar.MINUTE)*60)+A_Protocol.protocolCalendar.get(
				                Calendar.SECOND);
				break;
		}
		return simulationTime;
	}
	@Override
	public DriverType getTypeActeur() { return DriverType.WHALE; }
}
