package melanesim.protocol;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import data.C_Parameters;
import melanesim.C_ContextCreator;
import melanesim.util.C_PathWandererAstar_Marine;
import repast.simphony.context.Context;
import thing.C_Ship_astar;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_Harbour;
import thing.ground.C_SoilCellMarineNode;
import thing.ground.landscape.C_LandscapeMarineGraphed;

/**
 * Déplacement des bateaux entre les ports avec A*.
 * Les bateaux évitent la terre et suivent les chemins calculés.
 * Les chemins sont gardés dans {@link #paths}.
 * @see I_ConstantPNMC#HARBOURS
 * @author MIAGE 2026 */
public class C_Protocol_PNMC_ships_astar extends C_Protocol_PNMC_ships {
	//
	// FIELDS
	//
	/** Stored list of paths already computed */
	public static TreeMap<String, C_SoilCellMarineNode[]> paths;
	public static int nbPaths = 0;
	protected ArrayList<C_Harbour> harbours = new ArrayList<C_Harbour>();
	//
	// CONSTRUCTOR
	//
	public C_Protocol_PNMC_ships_astar(Context<Object> ctxt) {
		super(ctxt);
		C_Protocol_PNMC_ships_astar.paths = new TreeMap<String, C_SoilCellMarineNode[]>();
		C_Protocol_PNMC_ships_astar.nbPaths = 0;
	}
	//
	// SPECIFIC METHOD
	//
	public C_Ship_astar createAstarShip(C_Harbour homeHarbour) {
		return new C_Ship_astar(new C_GenomeAnimalia(),homeHarbour,this.harbours);
	}
	/** Récupère la cellule marine la plus proche de chaque port - MIAGE 2026 */
	protected void initHarbours() {
		this.harbours.clear();
		for(Entry<String,double[]> entry:HARBOURS.entrySet()){
			C_SoilCellMarineNode berth = C_PathWandererAstar_Marine.closestSea(C_Harbour.getCell(entry.getValue()[0],
			        entry.getValue()[1]));
			if(berth==null) continue;
			C_Harbour harbour = new C_Harbour(entry.getKey(),berth);
			this.contextualizeNewThingInContainer(harbour,berth);
			this.harbours.add(harbour);
		}
		System.out.println("C_Protocol_PNMC_ships_astar.init: "+this.harbours.size()+" harbour(s) created");
	}
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
	protected double getCargoPopulation() { return 0.; }
	@Override
	protected void initPopulations() {
		super.initPopulations();
		this.initHarbours();
		if(this.harbours.isEmpty()) return;
		for(int i = 0; i<SHIPS_ASTAR_POPULATION; i++){
			int index = (int)(C_ContextCreator.randomGeneratorForInitialisation.nextDouble()*this.harbours.size());
			C_Harbour home = this.harbours.get(Math.min(index,this.harbours.size()-1));
			this.contextualizeNewThingInContainer(createAstarShip(home),home.getBerthCell());
		}
		System.out.println("C_Protocol_PNMC_ships_astar.init: "+SHIPS_ASTAR_POPULATION
		        +" A* ship(s) created and berthed in harbours");
	}
}
