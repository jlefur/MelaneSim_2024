package melanesim.util.astar;

import java.util.*;

import thing.A_VisibleAgent;
import thing.ground.C_SoilCellMarineNode;
import thing.ground.I_Container;

/** Algorithme A* sur la mer : la terre est un obstacle (@see C_SoilCellMarineNode#isBlock()).<br>
 * Les cases sont remises à zéro à chaque recherche. Une case peut être plusieurs fois dans la liste, on ignore les
 * doublons.
 * @author Marcelo Surriabre 2017, adapted J. Le Fur, april 2022, rev. MIAGE 2026 */
public class AStarMarine {
	private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
	private static int DEFAULT_DIAGONAL_COST = 14;
	private int hvCost;
	private int diagonalCost;
	private I_Container[][] searchArea;
	private PriorityQueue<C_SoilCellMarineNode> openList;
	private Set<C_SoilCellMarineNode> closedSet;
	private C_SoilCellMarineNode finalCellNode;

	public AStarMarine() {
		this.hvCost = DEFAULT_HV_COST;
		this.diagonalCost = DEFAULT_DIAGONAL_COST;
		this.searchArea = A_VisibleAgent.myLandscape.getGrid();
	}

	/** @return les cases du départ à l'arrivée (incluses), liste vide si pas de chemin */
	public ArrayList<C_SoilCellMarineNode> findPath(C_SoilCellMarineNode initialCellNode,
			C_SoilCellMarineNode finalCellNode) {
		this.finalCellNode = finalCellNode;
		this.openList = new PriorityQueue<C_SoilCellMarineNode>(new Comparator<C_SoilCellMarineNode>() {
			@Override
			public int compare(C_SoilCellMarineNode node0, C_SoilCellMarineNode node1) {
				return Integer.compare(node0.getF(), node1.getF());
			}
		});
		this.setCellNodes();
		this.closedSet = new HashSet<>();
		initialCellNode.initAsStart();
		this.openList.add(initialCellNode);
		while (!this.openList.isEmpty()) {
			C_SoilCellMarineNode currentCellNode = this.openList.poll();
			if (this.closedSet.contains(currentCellNode)) continue; // doublon déjà traité
			this.closedSet.add(currentCellNode);
			if (isFinalCellNode(currentCellNode)) return getPath(currentCellNode);
			else addAdjacentCellNodes(currentCellNode);
		}
		return new ArrayList<C_SoilCellMarineNode>();
	}

	/** Remet les cases à zéro et calcule leur distance à l'arrivée */
	private void setCellNodes() {
		for (int i = 0; i < searchArea.length; i++) {
			for (int j = 0; j < searchArea[0].length; j++) {
				((C_SoilCellMarineNode) this.searchArea[i][j]).resetNodeData(this.finalCellNode, this.hvCost,
						this.diagonalCost);
			}
		}
	}

	private ArrayList<C_SoilCellMarineNode> getPath(C_SoilCellMarineNode currentCellNode) {
		ArrayList<C_SoilCellMarineNode> path = new ArrayList<C_SoilCellMarineNode>();
		path.add(currentCellNode);
		C_SoilCellMarineNode parent;
		while ((parent = currentCellNode.getParent()) != null) {
			path.add(0, parent);
			currentCellNode = parent;
		}
		return path;
	}

	private void addAdjacentCellNodes(C_SoilCellMarineNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		for (int dRow = -1; dRow <= 1; dRow++) {
			for (int dCol = -1; dCol <= 1; dCol++) {
				if (dRow == 0 && dCol == 0) continue;
				int newRow = row + dRow, newCol = col + dCol;
				if (newRow < 0 || newRow >= this.searchArea.length || newCol < 0 || newCol >= this.searchArea[0].length)
					continue;
				if (dRow != 0 && dCol != 0) {
					// en diagonale : ne pas couper un coin de terre
					if (((C_SoilCellMarineNode) this.searchArea[row][newCol]).isBlock()
							|| ((C_SoilCellMarineNode) this.searchArea[newRow][col]).isBlock())
						continue;
					checkCellNode(currentCellNode, newCol, newRow, this.diagonalCost);
				}
				else checkCellNode(currentCellNode, newCol, newRow, this.hvCost);
			}
		}
	}

	private void checkCellNode(C_SoilCellMarineNode currentCellNode, int col, int row, int cost) {
		C_SoilCellMarineNode adjacentCellNode = (C_SoilCellMarineNode) this.searchArea[row][col];
		if (!adjacentCellNode.isBlock() && !closedSet.contains(adjacentCellNode)) {
			// première visite ou chemin plus court : on ajoute la case
			if (adjacentCellNode.checkBetterPath(currentCellNode, cost)) this.openList.add(adjacentCellNode);
		}
	}

	private boolean isFinalCellNode(C_SoilCellMarineNode currentCellNode) {
		return currentCellNode.equals(finalCellNode);
	}
}
