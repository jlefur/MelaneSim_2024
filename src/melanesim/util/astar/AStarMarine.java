package melanesim.util.astar;

import java.util.*;

import thing.A_VisibleAgent;
import thing.ground.C_SoilCellMarineNode;
import thing.ground.I_Container;

/** A Star Algorithm
 * @author Marcelo Surriabre 2017, adapted J. Le Fur, april 2022 */
public class AStarMarine {
	private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
	private static int DEFAULT_DIAGONAL_COST = 14;
	private int hvCost;
	private int diagonalCost;
	private I_Container[][] searchArea;
	private PriorityQueue<C_SoilCellMarineNode> openList;
	private Set<C_SoilCellMarineNode> closedSet;
//	private C_SoilCellMarineNode initialCellNode;
	private C_SoilCellMarineNode finalCellNode;

	public AStarMarine() {
		this.hvCost = DEFAULT_HV_COST;
		this.diagonalCost = DEFAULT_DIAGONAL_COST;
		this.searchArea = A_VisibleAgent.myLandscape.getGrid();
	}

	public ArrayList<C_SoilCellMarineNode> findPath(C_SoilCellMarineNode initialCellNode, C_SoilCellMarineNode finalCellNode) {
//		this.initialCellNode = initialCellNode;
		this.finalCellNode = finalCellNode;
		this.openList = new PriorityQueue<C_SoilCellMarineNode>(new Comparator<C_SoilCellMarineNode>() {
			@Override
			public int compare(C_SoilCellMarineNode node0, C_SoilCellMarineNode node1) {
				return Integer.compare(node0.getF(), node1.getF());
			}
		});
		this.setCellNodes();
		this.closedSet = new HashSet<>();
		this.openList.add(initialCellNode);
		while (!isEmpty(this.openList)) {
			C_SoilCellMarineNode currentCellNode = this.openList.poll();
			this.closedSet.add(currentCellNode);
			if (isFinalCellNode(currentCellNode)) return getPath(currentCellNode);
			else addAdjacentCellNodes(currentCellNode);
		}
		return new ArrayList<C_SoilCellMarineNode>();
	}

	private void setCellNodes() {
		for (int i = 0; i < searchArea.length; i++) {
			for (int j = 0; j < searchArea[0].length; j++) {
				((C_SoilCellMarineNode) this.searchArea[i][j]).calculateHeuristic(this.finalCellNode);
			}
		}
	}

	private ArrayList<C_SoilCellMarineNode> getPath(C_SoilCellMarineNode currentCellNode) {
		ArrayList<C_SoilCellMarineNode> path = new ArrayList<C_SoilCellMarineNode>();
		path.add(currentCellNode);
		C_SoilCellMarineNode parent;
		while ((parent = currentCellNode.getParent()) != null //
				&& parent.getParent() != currentCellNode) {// added to avoid loops JLF 05.2022
			path.add(0, parent);
			currentCellNode = parent;
		}
		return path;
	}

	private void addAdjacentCellNodes(C_SoilCellMarineNode currentCellNode) {
		addAdjacentUpperRow(currentCellNode);
		addAdjacentMiddleRow(currentCellNode);
		addAdjacentLowerRow(currentCellNode);
	}

	private void addAdjacentLowerRow(C_SoilCellMarineNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int lowerRow = row + 1;
		if (lowerRow < this.searchArea.length) {
			if (col - 1 >= 0) {
				// Comment this line if diagonal movements are not allowed
				checkCellNode(currentCellNode, col - 1, lowerRow, this.diagonalCost);
			}
			if (col + 1 < this.searchArea[0].length) {
				// Comment this line if diagonal movements are not allowed
				checkCellNode(currentCellNode, col + 1, lowerRow, this.diagonalCost);
			}
			checkCellNode(currentCellNode, col, lowerRow, this.hvCost);
		}
	}

	private void addAdjacentMiddleRow(C_SoilCellMarineNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int middleRow = row;
		if (col - 1 >= 0) {
			checkCellNode(currentCellNode, col - 1, middleRow, this.hvCost);
		}
		if (col + 1 < this.searchArea[0].length) {
			checkCellNode(currentCellNode, col + 1, middleRow, this.hvCost);
		}
	}

	private void addAdjacentUpperRow(C_SoilCellMarineNode currentCellNode) {
		int row = currentCellNode.retrieveLineNo();
		int col = currentCellNode.retrieveColNo();
		int upperRow = row - 1;
		if (upperRow >= 0) {
			if (col - 1 >= 0) {
				// Comment this if diagonal movements are not allowed
				checkCellNode(currentCellNode, col - 1, upperRow, this.diagonalCost);
			}
			if (col + 1 < this.searchArea[0].length) {
				// Comment this if diagonal movements are not allowed
				checkCellNode(currentCellNode, col + 1, upperRow, this.diagonalCost);
			}
			checkCellNode(currentCellNode, col, upperRow, this.hvCost);
		}
	}

	private void checkCellNode(C_SoilCellMarineNode currentCellNode, int col, int row, int cost) {
		C_SoilCellMarineNode adjacentCellNode = (C_SoilCellMarineNode) this.searchArea[row][col];
		if (!adjacentCellNode.isBlock() && !closedSet.contains(adjacentCellNode)) {
			if (!getOpenList().contains(adjacentCellNode)) {
				adjacentCellNode.setNodeData(currentCellNode, cost);
				getOpenList().add(adjacentCellNode);
			}
			else {
				boolean changed = adjacentCellNode.checkBetterPath(currentCellNode, cost);
				if (changed) {
					// Remove and Add the changed node, so that the PriorityQueue can sort again its
					// contents with the modified "finalCost" value of the modified node
					getOpenList().remove(adjacentCellNode);
					getOpenList().add(adjacentCellNode);
				}
			}
		}
	}

	private boolean isFinalCellNode(C_SoilCellMarineNode currentCellNode) {
		return currentCellNode.equals(finalCellNode);
	}

	private boolean isEmpty(PriorityQueue<C_SoilCellMarineNode> openList) {
		return openList.size() == 0;
	}

	public PriorityQueue<C_SoilCellMarineNode> getOpenList() {
		return openList;
	}

	public void setOpenList(PriorityQueue<C_SoilCellMarineNode> openList) {
		this.openList = openList;
	}

//	private void setDiagonalCost(int diagonalCost) {
//		this.diagonalCost = diagonalCost;
//	}
}
