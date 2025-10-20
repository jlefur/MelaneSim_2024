package presentation.epiphyte;

import data.constants.rodents.I_ConstantStringRodents;
import melanesim.C_ContextCreator;
import melanesim.protocol.A_Protocol;
import presentation.dataOutput.C_FileWriter;
import repast.simphony.engine.environment.RunState;
import repast.simphony.essentials.RepastEssentials;
import thing.I_SituatedThing;

/** Data inspector: retrieves informations e.g. population sizes and manages lists. stores the values of the indicators to be
 * recorded in the general indicators file.
 * @author A Realini 05.2011 / J.LeFur 09.2011, 07.2012, 01.2013 */

public abstract class A_Inspector implements Comparable<A_Inspector>, I_Inspector, I_ConstantStringRodents {

	protected String indicatorsHeader;
	protected String indicatorsValues;
	protected C_FileWriter SpatialDistributionFile;
	public static int contextSize = 0;
	protected Integer myId;// used to compare objects within sorts and treeSets - LeFur 02.2013
	public A_Inspector() {
		this.myId = C_ContextCreator.INSPECTOR_NUMBER;
		C_ContextCreator.INSPECTOR_NUMBER++;
		this.indicatorsHeader = "Tick;Date;HourDate;objects";
		indicatorsReset();
		SpatialDistributionFile = new C_FileWriter("SpatialDistribution.csv", true);
	}
	/** also store indicators values for all other inspectors */
	public void step_Utick() {
		indicatorsCompute();
		indicatorsStoreValues();
	}

	/** Context size only, extended in daughter classes */
	public void indicatorsCompute() {
		C_InspectorPopulation.contextSize = RunState.getInstance().getMasterContext().size();
	}

	/** store all values as a string in the corresponding field */
	public String indicatorsStoreValues() {// Simultech 2018
		this.indicatorsValues = String.valueOf(RepastEssentials.GetTickCount()) + CSV_FIELD_SEPARATOR
				+ A_Protocol.protocolCalendar.stringShortDate() + CSV_FIELD_SEPARATOR + A_Protocol.protocolCalendar
						.stringHourDate() + CSV_FIELD_SEPARATOR + C_InspectorPopulation.contextSize;
		return this.indicatorsValues;
	}
	/** used for comparison of inspectors within the A_Protocol inspectors field / JLF 02.2013 */
	public int compareTo(A_Inspector other) {
		int nb1 = other.myId;
		int nb2 = this.myId;
		if (nb1 > nb2) return -1;
		else if (nb1 == nb2) return 0;
		else return 1;
	}
	public String getIndicatorsValues() {
		return this.indicatorsValues;
	}
	public String getIndicatorsHeader() {
		return this.indicatorsHeader;
	}
	public void indicatorsReset() {};
	public void closeSimulation() {};
	public void discardDeadThing(I_SituatedThing I_deadThing) {};
}
