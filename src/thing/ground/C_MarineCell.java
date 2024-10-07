package thing.ground;

import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;

import data.constants.I_ConstantPNMC_particules;
import data.converters.C_ConvertTimeAndSpace;
import thing.A_VisibleAgent;
import thing.C_Plankton;
import thing.I_SituatedThing;

/** Ocean unit used in MelaneSim
 * @author JLF 2024 */
public class C_MarineCell extends C_SoilCell implements I_ConstantPNMC_particules {
	//
	// FIELDS
	//
	private double speedEastward_UmeterPerSecond, speedNorthward_UmeterPerSecond;
	//
	// CONSTRUCTOR
	//
	public C_MarineCell(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
		this.speedEastward_UmeterPerSecond = 0.0;
		this.speedNorthward_UmeterPerSecond = 0.0;
	}
	//
	// METHOD
	//
	@Override
	/** Move planktonic occupants with the surface current speed - JLF 07.2024 */
	public void step_Utick() {
		super.step_Utick();
		double speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick, energy;
		if (this.speedEastward_UmeterPerSecond + this.speedNorthward_UmeterPerSecond == 0.) return;
		else {
			TreeSet<I_SituatedThing> occupants = new TreeSet<>(this.getOccupantList());
			// Object[] occupants = this.getOccupantList().toArray();// To avoid concurrent exception
			for (I_SituatedThing agent : occupants) {
				if (agent instanceof C_Plankton) {
					speedEastward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
							this.speedEastward_UmeterPerSecond, "m", "s");
					speedNorthward_UmeterPerTick = C_ConvertTimeAndSpace.convertSpeed_UspaceByTick(
							this.speedNorthward_UmeterPerSecond, "m", "s");
					A_VisibleAgent.myLandscape.translate((A_VisibleAgent) agent, new Coordinate(
							speedEastward_UmeterPerTick, speedNorthward_UmeterPerTick));
					energy = Math.sqrt(speedEastward_UmeterPerTick * speedEastward_UmeterPerTick
							+ speedNorthward_UmeterPerTick * speedNorthward_UmeterPerTick)/1E3;// TODO number in source JLF 2024 (energy = speed /1E3)
					((C_Plankton) agent).energy_Ukcal += energy;
				}
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
	public double getSpeedEastward_UmeterPerSecond() {
		return speedEastward_UmeterPerSecond;
	}
	public double getSpeedNorthward_UmeterPerSecond() {
		return speedNorthward_UmeterPerSecond;
	}
	public boolean isTerrestrial() {
		return this.getAffinity() >= TERRESTRIAL_MIN_AFFINITY;
	}
}
