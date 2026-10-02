package melanesim.protocol;

import java.util.TreeMap;

import data.C_Parameters;
import repast.simphony.context.Context;
import thing.C_Ship_astar;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_SoilCellMarineNode;
import thing.ground.landscape.C_LandscapeMarineGraphed;

/** First step toward A* powered ship navigation: on top of the regular ships activities (with the cargo
 * population trimmed down to keep the scene readable), adds one boat that travels directly (straight line)
 * from a fixed point A to a fixed point B. The A* pathfinding engine will later replace this direct move.
 * @author MIAGE 2026 */
public class C_Protocol_PNMC_ships_astar extends C_Protocol_PNMC_ships {
	//
	// FIELDS
	//
	/** Stored list of paths already computed */
	public static TreeMap<String, C_SoilCellMarineNode[]> paths;
	public static int nbPaths = 0;
	//
	// CONSTRUCTOR
	//
	public C_Protocol_PNMC_ships_astar(Context<Object> ctxt) { super(ctxt); }
	//
	// SPECIFIC METHOD
	//
	public C_Ship_astar createAstarShip() { return new C_Ship_astar(new C_GenomeAnimalia()); }
	//
	// OVERRIDEN METHODS
	//
	@Override
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeMarineGraphed(context,C_Parameters.RASTER_URL,VALUE_LAYER_NAME,
		        CONTINUOUS_SPACE_NAME));
		for(int i = 0; i<this.landscape.dimension_Ucell.width; i++){
			for(int j = 0; j<this.landscape.dimension_Ucell.height; j++){
				C_SoilCellMarineNode cell = new C_SoilCellMarineNode(this.landscape.getGrid()[i][j].getAffinity(),i,j);
				// Comment the following line to undisplay soil cells, JLF 10.2015, 11.2015
				context.add(cell);
				this.landscape.setGridCell(i,j,cell);
				this.landscape.moveToLocation(cell,cell.getCoordinate_Ucs());
			}
		}
	}
	@Override
	/** TEMP 0 to isolate the Astar ship for visual testing (Noumea->Lifou) - set back to 1 once confirmed - MIAGE 2026 */
	protected double getCargoPopulation() { return 0.; }
	@Override
	/** Add the single A-to-B ship at Noumea - MIAGE 2026 */
	protected void initPopulations() {
		super.initPopulations();
		C_Ship_astar boat = createAstarShip();
		this.contextualizeNewThingInContainer(boat,C_Ship_astar.getPointA());
		System.out.println("C_Protocol_PNMC_ships_astar.init: 1 Astar-ready ship created and positioned");
	}
}
