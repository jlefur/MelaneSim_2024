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
 * La terre est un obstacle.
 *
 * @author Marcelo Surriabre 2018, adapted J. Le Fur, 2022, oct.2026, rev. MIAGE 2026
 */
public class C_SoilCellMarineNode extends C_SoilCellMarine {

	private int g;
	private int f;
	private int h;
	private C_SoilCellMarineNode parent;

	public C_SoilCellMarineNode(int aff, int lineNo, int colNo) {
		super(aff, lineNo, colNo);
	}

	/** Efface la recherche d'avant et calcule la distance jusqu'à finalNode */
	public void resetNodeData(C_SoilCellMarineNode finalNode, int hvCost, int diagonalCost) {
		this.parent = null;
		this.g = Integer.MAX_VALUE;
		int dx = Math.abs(finalNode.retrieveLineNo() - this.lineNo);
		int dy = Math.abs(finalNode.retrieveColNo() - this.colNo);
		this.h = hvCost * Math.max(dx, dy) + (diagonalCost - hvCost) * Math.min(dx, dy);
		this.f = Integer.MAX_VALUE;
	}

	/** Case de départ : pas de parent, coût zéro */
	public void initAsStart() {
		this.parent = null;
		this.g = 0;
		calculateFinalCost();
	}

	public void setNodeData(C_SoilCellMarineNode currentNode, int cost) {
		this.parent = currentNode;
		this.g = currentNode.g + cost;
		calculateFinalCost();
	}

	/** @return true si passer par currentNode est plus court (toujours vrai à la première visite) */
	public boolean checkBetterPath(C_SoilCellMarineNode currentNode, int cost) {
		int gCost = currentNode.g + cost;
		if (gCost < this.g) {
			setNodeData(currentNode, cost);
			return true;
		}
		return false;
	}

	private void calculateFinalCost() {
		this.f = this.g + this.h;
	}

	@Override
	public boolean equals(Object arg0) {
		if (this == arg0) return true;
		if (!(arg0 instanceof C_SoilCellMarineNode)) return false;
		C_SoilCellMarineNode other = (C_SoilCellMarineNode) arg0;
		return this.retrieveLineNo() == other.retrieveLineNo() && this.retrieveColNo() == other.retrieveColNo();
	}

	@Override
	public int hashCode() {
		return 31 * this.lineNo + this.colNo;
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

	/** La terre bloque les bateaux */
	public boolean isBlock() {
		return this.isTerrestrial();
	}
}
