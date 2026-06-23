package thing;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;

import thing.dna.C_GenomeAmniota;
import thing.dna.I_DiploidGenome;
import thing.rodents.A_HumanUrban;
/** @author JLF 06.2026 */
public class C_Megaptera extends A_Amniote {
	//
	// CONSTRUCTOR
	//
	public C_Megaptera(I_DiploidGenome genome) { super(genome); }
	public C_Megaptera(String name,String sex) {
		this(new C_GenomeAmniota());
		this.setMyName(name);
		if(sex.equals("M")) this.setMale(true);
		else this.setMale(false);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	public A_Animal giveBirth(I_DiploidGenome genome) { return new C_Megaptera(genome); }
	//
	//METHOD
	//
//	/** Use the activity list to initialize human activities
//	 * @author M.Sall 10.2020 */
//	public void initActivity() {
//		ArrayList<String> activitiesList = this.whaleActivitiesChrono.getFullEvents_Ustring();
//		for (int i = 0; i < this.whaleActivitiesChrono.getChronoLength(); i++) {
//			String[] activities = activitiesList.get(i).split(CSV_FIELD_SEPARATOR);
//			if (this.myName.equals(activities[DATE_COL])) {
//				if (activities[X_COL].contains(".") || activities[X_COL].contains("."))// coordinate in decimal degrees
//					oneHuman.addActivityList(activities[EVENT_COL], this.geographicCoordinateConverter
//							.convertCoordinate_Ucs(Double.parseDouble(activities[X_COL]), Double.parseDouble(
//									activities[Y_COL])), activities[VALUE1_COL], activities[VALUE2_COL] + "/"
//											+ activities[VALUE3_COL]);
//				else
//					oneHuman.addActivityList(activities[EVENT_COL], new Coordinate(Integer.parseInt(activities[X_COL]),
//							Integer.parseInt(activities[Y_COL])), activities[VALUE1_COL], activities[VALUE2_COL] + "-"
//									+ activities[CELL_ID_COL]);
//			}
//		}
//		oneHuman.manageActivities();
//	}
}
