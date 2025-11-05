package thing.ground;

import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import data.converters.C_ConvertTimeAndSpace;
import thing.A_Animal;
import thing.A_VisibleAgent;
import thing.C_Nekton;
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
	private double chlorophyll_U100 = 0.;
	private double totalChlorophyll_U100 = 0.;
	private double totalNektonDensity = 0.;
	/** microNekton is not moved by currents */
	private double microNekton = 0.;
	//
	// CONSTRUCTOR
	//
	public C_SoilCellMarine(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
		// TODO number in source OK 2024 JLF speed has to be != from 0 before read from file in order to avoid plankton bordure
		this.speedEastward_UmeterPerSecond = 1e-10;
		this.speedNorthward_UmeterPerSecond = 1e-10;
	}
	//
	// OVERRIDEN METHODS
	//
	/** Incoming plankton gets the cell's chlorophyll value as energy value, update cell's meanEnergy_Ukcal
	 * @author JLF 04.2025 */
	@Override
	public boolean agentIncoming(I_SituatedThing thing) {
		if (!(thing instanceof C_StreamCurrent)) {// stream current agent are not counted
			this.energy_Ukcal += C_Parameters.PARTICLE_MULTIPLIER;
			if (thing instanceof C_Nekton) {
				this.totalNektonDensity += this.microNekton;
				((C_Nekton) thing).energy_Ukcal = this.microNekton * C_Parameters.NEKTON_MULTIPLIER;
			}
			else if (thing instanceof C_Plankton) {
				this.totalChlorophyll_U100 += this.chlorophyll_U100;
				((C_Plankton) thing).energy_Ukcal = this.chlorophyll_U100 * C_Parameters.CHLOROPHYLL_MULTIPLIER;
			}
			this.energy_Ukcal += thing.getEnergy_Ukcal();
		}
		return super.agentIncoming(thing);
	}

	/** Leaving nekton decreases nektonDensity, leaving plankton decrease cell's total chlorophyll, all particle decrease cell's
	 * energy
	 * @author JLF 04,10.2025 */
	@Override
	public boolean agentLeaving(I_SituatedThing thing) {
		if (thing instanceof C_Nekton) this.totalNektonDensity -= this.microNekton;
		else if (thing instanceof C_Plankton) {
			this.totalChlorophyll_U100 -= this.chlorophyll_U100;
		}
		this.energy_Ukcal -= thing.getEnergy_Ukcal();
		if (!(thing instanceof C_StreamCurrent)) {
			this.energy_Ukcal -= C_Parameters.PARTICLE_MULTIPLIER;
		}
		return super.agentLeaving(thing);
	}

	@Override
	/** Move planktonic occupants with the surface current speed - JLF 07.2024<br>
	 * Compute the marineCell's integral energy -JLF 03,10 2025 */
	public void step_Utick() {
		super.step_Utick();
		this.integralEnergy_Ukcal += this.energy_Ukcal;
		double speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick;
		TreeSet<I_SituatedThing> occupants = new TreeSet<>(this.getOccupantList());
		for (I_SituatedThing agent : occupants) {
			speedEastward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
					this.speedEastward_UmeterPerSecond, "m", "s") / PARTICLE_RESISTANCE_FACTOR;
			speedNorthward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
					this.speedNorthward_UmeterPerSecond, "m", "s") / PARTICLE_RESISTANCE_FACTOR;
			if (agent instanceof C_Nekton)
				// micronekton particle are not submitted to surface current half of day
				A_VisibleAgent.myLandscape.translate((A_VisibleAgent) agent, new Coordinate(speedEastward_UmeterPerTick
						/ NEKTON_RESISTANCE_FACTOR, speedNorthward_UmeterPerTick / NEKTON_RESISTANCE_FACTOR));
			else
				if (agent instanceof C_Plankton)
					A_VisibleAgent.myLandscape.translate((A_VisibleAgent) agent, new Coordinate(
							speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick));

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
	public double getChlorophyll_U100() {
		return chlorophyll_U100;
	}
	public void setChlorophyll_U100(double chlorophyll) {
		this.chlorophyll_U100 = chlorophyll;
	}
	public double getMicroNekton() {
		return microNekton;
	}
	public void setMicroNekton(double microNekton) {
		this.microNekton = microNekton;
	}
	public double getTotalChlorophyll_U100() {
		return totalChlorophyll_U100;
	}
	public void setTotalChlorophyll_U100(double totalChlorophyll_U100) {
		this.totalChlorophyll_U100 = totalChlorophyll_U100;
	}
	public int getNektonPopulation() {
		int size = 0;
		for (Object occupant : this.getOccupantList().toArray()) {
			if (occupant instanceof C_Nekton) size++;
		}
		return size;
	}
	public int getPlanktonPopulation() {
		int size = 0;
		for (Object occupant : this.getOccupantList().toArray()) {
			if (occupant instanceof C_Plankton && !(occupant instanceof C_Nekton)) size++;
		}
		return size;
	}
	public double getTotalNektonDensity() {
		return totalNektonDensity;
	}
	public void setTotalNektonDensity(double totalNektonDensity) {
		this.totalNektonDensity = totalNektonDensity;
	}
}
