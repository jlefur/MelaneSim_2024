/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt  */
package thing;
import thing.ground.A_SupportedContainer;
import thing.ground.C_SoilCellMarine;

/** Structure associated with marine cells for current speed to be displayed on GUI
 * @author JLF 2024
 * @see C_SoilCellMarine */
public class C_StreamCurrent extends A_SupportedContainer {
	private C_SoilCellMarine myCell;
	//
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
	public C_SoilCellMarine getMyCell() {
		return myCell;
	}
	public void setMyCell(C_SoilCellMarine myCell) {
		this.myCell = myCell;
	}
	public double getSpeedEast() {
		return myCell.getSpeedEastward_UmeterPerSecond();
	}
	public double getSpeedNorth() {
		return myCell.getSpeedNorthward_UmeterPerSecond();
	}
}