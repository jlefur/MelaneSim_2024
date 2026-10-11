package thing.ground;

import data.constants.I_ConstantPNMC;
import thing.A_VisibleAgent;

/** Port de la zone PNMC où s'arrêtent les bateaux A*. Il est placé sur la case de mer la plus proche
 * @see I_ConstantPNMC#HARBOURS
 * @see thing.C_Ship_astar
 * @author MIAGE 2026 */
public class C_Harbour extends A_SupportedContainer implements I_ConstantPNMC {
	//
	// FIELDS
	//
	private final String harbourName;
	/** case de mer où les bateaux s'arrêtent */
	private C_SoilCellMarineNode berthCell;
	//
	// CONSTRUCTOR
	//
	public C_Harbour(String harbourName, C_SoilCellMarineNode berthCell) {
		super(berthCell.getAffinity(), berthCell.retrieveLineNo(), berthCell.retrieveColNo());
		this.harbourName = harbourName;
		this.berthCell = berthCell;
		this.setMyName(harbourName);
		this.setHasToSwitchFace(true);
	}
	//
	// STATIC METHOD
	//
	/** Donne la case de la grille qui correspond à une longitude et une latitude (en degrés) */
	public static C_SoilCellMarineNode getCell(double longitude_Udegree, double latitude_Udegree) {
		I_Container[][] grid = A_VisibleAgent.myLandscape.getGrid();
		int line = (int) ((longitude_Udegree - RASTER_LONGITUDE_WEST_Udegree) / RASTER_CELL_LONGITUDE_Udegree);
		int col = (int) ((latitude_Udegree - RASTER_LATITUDE_SOUTH_Udegree) / RASTER_CELL_LATITUDE_Udegree);
		line = Math.max(0, Math.min(grid.length - 1, line));
		col = Math.max(0, Math.min(grid[0].length - 1, col));
		return (C_SoilCellMarineNode) grid[line][col];
	}
	//
	// GETTERS
	//
	public String getHarbourName() { return this.harbourName; }
	public C_SoilCellMarineNode getBerthCell() { return this.berthCell; }
}
