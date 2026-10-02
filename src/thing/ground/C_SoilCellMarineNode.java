package thing.ground;

/**
 * Node Class from A* algorithm computes the optimal path by evaluating nodes
 * based on the standard A* mathematical formula: <br>
 * F=G+H with:<br>
 * • G Cost: The actual cost (distance) to travel from the starting node to the
 * current node.<br>
 * • H Cost (Heuristic): The estimated cost from the current node to the
 * destination. In grid setups, this is usually calculated using straight-line
 * Euclidean distance or Manhattan distance.<br>
 * • F Cost: The total combined cost. The algorithm always prioritizes expanding
 * nodes with the absolute lowest F cost.<br>
 * 
 * @author Marcelo Surriabre 2018, adapted J. Le Fur, 2022, oct.2026
 */
public class C_SoilCellMarineNode extends C_SoilCellMarine {

	private int g;
	private int f;
	private int h;
	private boolean isBlock;
	private C_SoilCellMarineNode parent;

	public C_SoilCellMarineNode(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
	}

	public void calculateHeuristic(C_SoilCellMarineNode finalNode) {
		this.h = Math.abs(finalNode.retrieveLineNo() - this.lineNo) + Math.abs(finalNode.retrieveColNo() - this.colNo);
	}

	public void setNodeData(C_SoilCellMarineNode currentNode, int cost) {
		int gCost = currentNode.g + cost;
		this.parent = currentNode;
		this.g = gCost;
		calculateFinalCost();
	}

	public boolean checkBetterPath(C_SoilCellMarineNode currentNode, int cost) {
		int gCost = currentNode.g + cost;
		if (gCost < this.g) {
			setNodeData(currentNode, cost);
			return true;
		}
		return false;
	}

	private void calculateFinalCost() {
		int finalCost = this.g + this.h;
		this.f = finalCost;
	}

	@Override
	public boolean equals(Object arg0) {
		C_SoilCellMarineNode other = (C_SoilCellMarineNode) arg0;
		return this.retrieveLineNo() == other.retrieveLineNo() && this.retrieveColNo() == other.retrieveColNo();
	}

	@Override
	public String toString() {
		return "Node [row=" + this.lineNo + ", col=" + this.colNo + "]";
	}

	public int getF() {
		return f;
	}

	public C_SoilCellMarineNode getParent() {
		return parent;
	}

	public boolean isBlock() {
		return this.isBlock;
	}
}
