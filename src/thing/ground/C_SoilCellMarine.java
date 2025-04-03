package thing.ground;

import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertTimeAndSpace;
import thing.A_VisibleAgent;
import thing.C_Plankton;
import thing.C_StreamCurrent;
import thing.I_SituatedThing;

/** Ocean unit used in MelaneSim
 * @author JLF 2024 */
public class C_SoilCellMarine extends C_SoilCell implements I_ConstantPNMC {
	//
	// FIELDS
	//
	private double speedEastward_UmeterPerSecond, speedNorthward_UmeterPerSecond;
	private C_StreamCurrent myCurrent;
	/** Sum of energies (situated things) passed through this cell since last resetColors */
	private double integralEnergy_Ukcal = 0.;
	private double chlorophyll = 1.;
	//
	// CONSTRUCTOR
	//
	public C_SoilCellMarine(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
		// TODO number in source OK 2024 JLF speed has to be != from 0 before current read from file in order to avoid plankton
		// bordure
		this.speedEastward_UmeterPerSecond = 1e-10;
		this.speedNorthward_UmeterPerSecond = 1e-10;
	}
	//
	// OVERRIDEN METHOD
	//
	/** Incoming plankton gets the cell's chlorophyll value as energy value
	 * @author JLF 04.2025 */
	@Override
	public boolean agentIncoming(I_SituatedThing thing) {
		if (thing instanceof C_Plankton) ((C_Plankton) thing).energy_Ukcal = this.getChlorophyll();
		return super.agentIncoming(thing);

	}
	@Override
	/** Move planktonic occupants with the surface current speed - JLF 07.2024<br>
	 * set the energy value for plankton<br>
	 * Compute the marineCell's step energy and the integral energy -JLF 03.2025 */
	public void step_Utick() {
		this.energy_Ukcal = 0.;
		super.step_Utick();
		double speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick;
		TreeSet<I_SituatedThing> occupants = new TreeSet<>(this.getOccupantList());
		for (I_SituatedThing agent : occupants) {
			this.energy_Ukcal += agent.getEnergy_Ukcal();
			this.integralEnergy_Ukcal += agent.getEnergy_Ukcal();
			if (agent instanceof C_Plankton) {
				speedEastward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
						this.speedEastward_UmeterPerSecond, "m", "s") / PARTICLE_RESISTANCE_FACTOR;
				speedNorthward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
						this.speedNorthward_UmeterPerSecond, "m", "s") / PARTICLE_RESISTANCE_FACTOR;
				A_VisibleAgent.myLandscape.translate((A_VisibleAgent) agent, new Coordinate(speedEastward_UmeterPerTick,
						speedNorthward_UmeterPerTick));
			}
		}
	}
	//
	// SETTERS & GETTERS
	//
	public void setSpeedEastward_UmeterPerSecond(double currentSpeedEastward_UmeterPerSecond) {
		this.speedEastward_UmeterPerSecond = currentSpeedEastward_UmeterPerSecond;
	}
	public void setSpeedNorthward_UmeterPerSecond(double currentSpeedNorthward_UmeterPerSecond) {
		this.speedNorthward_UmeterPerSecond = currentSpeedNorthward_UmeterPerSecond;
	}
	public void setMyCurrent(C_StreamCurrent myCurrent) {
		this.myCurrent = myCurrent;
		myCurrent.setMyCell(this);
	}
	public void setIntegralEnergy_Ukcal(double d) {
		this.integralEnergy_Ukcal = d;
	}
	public double getIntegralEnergy_Ukcal() {
		return integralEnergy_Ukcal;
	}
	public double getSpeedEastward_UmeterPerSecond() {
		return speedEastward_UmeterPerSecond;
	}
	public double getSpeedNorthward_UmeterPerSecond() {
		return speedNorthward_UmeterPerSecond;
	}
	public boolean isTerrestrial() {
		return this.getAffinity() >= TERRESTRIAL_MIN_AFFINITY;
	}
	public C_StreamCurrent getMyCurrent() {
		return myCurrent;
	}
	public double getChlorophyll() {
		return chlorophyll;
	}
	public void setChlorophyll(double chlorophyll) {
		this.chlorophyll = chlorophyll;
	}
}
