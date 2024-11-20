/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt  */
package thing;

import thing.ground.A_SupportedContainer;
import thing.ground.C_MarineCell;

/** Structure associated with marine cells for current speed to be displayed
 * @author JLF 2024
 * @see C_MarineCell */
public class C_StreamCurrent extends A_SupportedContainer {
	private C_MarineCell myCell;//

	// CONSTRUCTOR
	//
	/** Constructor sets the affinity to 10 */
	public C_StreamCurrent(int affinity, int lineNo, int colNo) {
		super(affinity, lineNo, colNo);
		this.setHasToSwitchFace(true);
	}
	//
	// SETTER and GETTER
	//
	public C_MarineCell getMyCell() {
		return myCell;
	}
	public void setMyCell(C_MarineCell myCell) {
		this.myCell = myCell;
	}
	public double getSpeedEast() {
		return myCell.getSpeedEastward_UmeterPerSecond();
	}
	public double getSpeedNorth() {
		return myCell.getSpeedNorthward_UmeterPerSecond();
	}
}