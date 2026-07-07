package thing.ground;

import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertTimeAndSpace;
import thing.A_VisibleAgent;
import thing.C_Nekton;
import thing.C_Plankton;
import thing.C_StreamCurrent;
import thing.I_MarineActor;
import thing.I_MarineActor.DriverType;
import thing.I_SituatedThing;

/** Ocean unit used in MelaneSim
 * @author JLF 2024, rev. 2025 2026 */
public class C_SoilCellMarine extends C_SoilCellMarineEnergy implements I_ConstantPNMC {
	//
	// FIELDS
	//
	private double speedEastward_UmeterPerSec, speedNorthward_UmeterPerSec;
	private C_StreamCurrent myCurrent;
	private double myTemperature;
	public int totalOccupants = 0;
	//
	// CONSTRUCTOR
	//
	public C_SoilCellMarine(int aff,int lineNo,int colNo) {
		super(aff,lineNo,colNo);
		// TODO number in source OK 2024 JLF speed has to be != from 0 before read from file in order to avoid bordure
		this.speedEastward_UmeterPerSec = 1e-10;
		this.speedNorthward_UmeterPerSec = 1e-10;
		this.set(DriverType.SHIP,Champ.RAW_VAL,CARGO_ENERGY_Ukcal);// default value for ships
		this.set(DriverType.SHIP,Champ._100,100./CARGO_POPULATION);
		this.set(DriverType.PARTICLES,Champ.RAW_VAL,1.0);// default value for particles
		this.set(DriverType.PARTICLES,Champ._100,1.0E-5);// TODO number in source JLF 06.2026 24000 pk+nk 
	}
	//
	// OVERRIDEN METHODS
	//
	/** Incoming plankton gets the cell's chlorophyll value as energy value, update cell's meanEnergy_Ukcal
	 * @author JLF 04.2025 */
	@Override
	public boolean agentIncoming(I_SituatedThing thing) {
		if(thing instanceof I_MarineActor actor){
			this.add(DriverType.PARTICLES,Champ.INTEGRAL_100,1.0);
			this.add(DriverType.PARTICLES,Champ.NB_VAL,1.0);
			this.add(actor.getTypeActeur(),Champ.INTEGRAL_100,this.get(actor.getTypeActeur(),Champ._100));
			this.add(actor.getTypeActeur(),Champ.NB_VAL,1.0);
			thing.setEnergy_Ukcal(this.get(actor.getTypeActeur(),Champ._100));
		}
		return super.agentIncoming(thing);
	}

	/** Leaving nekton decreases nektonDensity, leaving plankton decrease cell's total chlorophyll, all particle
	 * decrease cell's energy
	 * @author JLF 04,10.2025 */
	@Override
	public boolean agentLeaving(I_SituatedThing thing) {
		if(thing instanceof I_MarineActor actor){
			this.add(DriverType.PARTICLES,Champ.NB_VAL,-1.0);
			this.add(actor.getTypeActeur(),Champ.NB_VAL,-1.0);
		}
		return super.agentLeaving(thing);
	}

	@Override
	/** Move planktonic occupants with the surface current speed - JLF 07.2024<br>
	 * Compute the marineCell's integral energy -JLF 03,10 2025 */
	public void step_Utick() {
		super.step_Utick();
		double speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick;
		TreeSet<I_SituatedThing> occupants = new TreeSet<>(this.getOccupantList());
		for(I_SituatedThing agent:occupants){
			speedEastward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
			        this.speedEastward_UmeterPerSec,"m","s")/PARTICLE_RESISTANCE_FACTOR;
			speedNorthward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
			        this.speedNorthward_UmeterPerSec,"m","s")/PARTICLE_RESISTANCE_FACTOR;
			if(agent instanceof C_Nekton)// micronekton particle are not submitted to surface current half of day
			    A_VisibleAgent.myLandscape.translate((A_VisibleAgent)agent,new Coordinate(speedEastward_UmeterPerTick
			            /NEKTON_RESISTANCE_FACTOR,speedNorthward_UmeterPerTick/NEKTON_RESISTANCE_FACTOR));
			else
			    if(agent instanceof C_Plankton)
			        A_VisibleAgent.myLandscape.translate((A_VisibleAgent)agent,new Coordinate(
			                speedEastward_UmeterPerTick,speedNorthward_UmeterPerTick));
		}
		// if(get(TypeActeur.PARTICLES,Champ.NB_VAL)>90.)System.err.println(RepastEssentials.GetTickCount()+","+this.lineNo+","+this.colNo+","+this.toString());
	}
	//
	// SETTERS
	//
	public void setSpeedEastward_UmeterPerSecond(double currentSpeedEastward_UmeterPerSec) {
		this.speedEastward_UmeterPerSec = currentSpeedEastward_UmeterPerSec;
	}
	public void setSpeedNorthward_UmeterPerSecond(double currentSpeedNorthward_UmeterPerSec) {
		this.speedNorthward_UmeterPerSec = currentSpeedNorthward_UmeterPerSec;
	}
	public void setMyCurrent(C_StreamCurrent myCurrent) { this.myCurrent = myCurrent; myCurrent.setMyCell(this); }
	public void setTemperature(double temperature) { this.myTemperature = temperature; }
	//
	// GETTERS
	//
	public double getSpeedEastward_UmeterPerSec() { return speedEastward_UmeterPerSec; }
	public double getSpeedNorthward_UmeterPerSec() { return speedNorthward_UmeterPerSec; }
	public boolean isTerrestrial() { return this.getAffinity()>=TERRESTRIAL_MIN_AFFINITY; }
	public C_StreamCurrent getMyCurrent() { return myCurrent; }
	public double getTotalChlorophyll_U100() {
		return this.get(DriverType.PLANKTON,Champ.NB_VAL)*this.get(DriverType.PLANKTON,Champ._100);
	}
	public double getTotalNektonDensity_U100() {
		return this.get(DriverType.NEKTON,Champ.NB_VAL)*this.get(DriverType.NEKTON,Champ._100);
	}
	public double getIntegralOccupants() { //
		return this.get(DriverType.PARTICLES,Champ.INTEGRAL_100);
	}
}
