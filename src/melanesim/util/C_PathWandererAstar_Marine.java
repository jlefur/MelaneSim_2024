package melanesim.util;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import melanesim.protocol.C_Protocol_PNMC_ships_astar;
import melanesim.util.astar.AStarMarine;
import thing.A_VisibleAgent;
import thing.ground.C_SoilCellMarineNode;

/** @author P.A. Mboup & J. Le Fur 2014, rev. Mboup 04.2016, added Astar, Le Fur 04.2022 */
public class C_PathWandererAstar_Marine {
	//
	// FIELDS
	//
	/** The Astar algorithm */
	private AStarMarine astar = new AStarMarine();
	/** Cases du chemin, du départ à l'arrivée (null si pas de chemin) */
	public C_SoilCellMarineNode[] nodesList;
	/** numéro de la case visée dans nodesList */
	public int indexPath = 0;
	public boolean isPathEnd = false;

	//
	// METHODS
	//
	/** Reprend le chemin s'il est déjà connu, sinon le calcule
	 * @return true si un chemin existe */
	public boolean getPath(C_SoilCellMarineNode currentCell, C_SoilCellMarineNode targetCell) {
		C_SoilCellMarineNode seaStart = closestSea(currentCell);
		C_SoilCellMarineNode seaTarget = closestSea(targetCell);
		if (seaStart == null || seaTarget == null) return false;
		String pathKey = String.format("%03d%03d%03d%03d", seaStart.retrieveLineNo(), seaStart.retrieveColNo(),
				seaTarget.retrieveLineNo(), seaTarget.retrieveColNo());
		this.nodesList = C_Protocol_PNMC_ships_astar.paths.get(pathKey);
		if (this.nodesList == null) {
			ArrayList<C_SoilCellMarineNode> nodes = this.astar.findPath(seaStart, seaTarget);
			if (nodes.isEmpty()) return false;
			this.nodesList = nodes.toArray(new C_SoilCellMarineNode[nodes.size()]);
			C_Protocol_PNMC_ships_astar.paths.put(pathKey, this.nodesList);
			C_Protocol_PNMC_ships_astar.nbPaths++;
		}
		// Dessine le chemin sur la carte
		if (C_Parameters.VERBOSE) for (C_SoilCellMarineNode node : this.nodesList)
			A_VisibleAgent.myLandscape.getValueLayer().set(10, node.retrieveLineNo(), node.retrieveColNo());
		this.indexPath = 0;
		this.isPathEnd = false;
		return true;
	}
	/** Donne la case de mer la plus proche (la case elle-même si c'est de la mer)
	 * @return null si toute la grille est de la terre */
	public static C_SoilCellMarineNode closestSea(C_SoilCellMarineNode cell) {
		if (!cell.isBlock()) return cell;
		int gridLength_x = A_VisibleAgent.myLandscape.getGrid().length;
		int gridLength_y = A_VisibleAgent.myLandscape.getGrid()[0].length;
		int x0 = cell.retrieveLineNo(), y0 = cell.retrieveColNo();
		int maxRadius = Math.max(gridLength_x, gridLength_y);
		for (int radius = 1; radius < maxRadius; radius++) {
			C_SoilCellMarineNode closest = null;
			double closestDistance = Double.MAX_VALUE;
			for (int x = x0 - radius; x <= x0 + radius; x++) {
				for (int y = y0 - radius; y <= y0 + radius; y++) {
					// seulement le bord du carré
					if (Math.abs(x - x0) != radius && Math.abs(y - y0) != radius) continue;
					if (x < 0 || x >= gridLength_x || y < 0 || y >= gridLength_y) continue;
					C_SoilCellMarineNode testCell = (C_SoilCellMarineNode) A_VisibleAgent.myLandscape.getGridCell(x, y);
					if (testCell.isBlock()) continue;
					double distance = Math.hypot(x - x0, y - y0);
					if (distance < closestDistance) {
						closest = testCell;
						closestDistance = distance;
					}
				}
			}
			if (closest != null) return closest;
		}
		return null;
	}
	/** @return la position de la case visée */
	public Coordinate getCurrentNodePoint_Umeter() {
		return this.nodesList[this.indexPath].getCoordinate_Umeter();
	}
	/** Passe à la case suivante, ou note la fin du chemin */
	public void setNextNode() {
		if (this.indexPath >= this.nodesList.length - 1) this.isPathEnd = true;
		else this.indexPath++;
	}
	public boolean hasPath() {
		return this.nodesList != null;
	}
	/** Vide les champs de l'objet */
	public void discardThis() {
		this.astar = null;
		this.nodesList = null;
	}
}
