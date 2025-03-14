/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import org.locationtech.jts.geom.Coordinate;

import data.C_ReadRasterOcean;
import data.constants.I_ConstantPNMC_particules;
import melanesim.protocol.A_Protocol;
import presentation.epiphyte.C_InspectorPopulationMarine;
import repast.simphony.context.Context;
import thing.A_Organism;
import thing.A_VisibleAgent;
import thing.ground.C_SoilCellMarine;
import thing.ground.I_Container;

/** The global container of MelaneSim's protocols<br>
 * Owns a continuous space and a grid/matrix with values ('affinity'), landplots of marine cells with the same affinity values.
 * Values are read from an ascii file. Affinity values are stored in a gridValueLayer (as well as in the cell container
 * attributes) <br>
 * @see A_Protocol
 * @author Baduel 2009.04, Le Fur 2009.12, Longueville 2011.02, Le Fur 2011,2012,2015,2024<br>
 *         formerly C_Raster */
public class C_LandscapeMarine extends C_Landscape implements I_ConstantPNMC_particules {
	//
	// CONSTRUCTOR
	//
	public C_LandscapeMarine(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Remove plankton washed along the shores JLF 2024 */
	public void translate(A_VisibleAgent thing, Coordinate moveDistance_Umeter) {
		C_SoilCellMarine cell = (C_SoilCellMarine) thing.getCurrentSoilCell();
		if (cell.getSpeedEastward_UmeterPerSecond() == 0.0 && cell.getSpeedNorthward_UmeterPerSecond() == 0.0) {
			if (((C_SoilCellMarine) thing.getCurrentSoilCell()).isTerrestrial()) bordure((A_Organism) thing);
			else {
				boolean washedOnShore = false;
				for (I_Container oneNeighbour : this.getCellNeighbours(cell))
					if (((C_SoilCellMarine) oneNeighbour).isTerrestrial()) {
						washedOnShore = true;
					}
				if (washedOnShore) bordure((A_Organism) thing);
			}
		}
		else super.translate(thing, moveDistance_Umeter);
	}

	@Override
	/** Read raster in true surfer format LeFur 07.2024 */
	protected int[][] readRasterFile(String url) {
		int[][] matriceLue;
		System.out.println();
		matriceLue = C_ReadRasterOcean.txtRasterLoader(url);
		A_Protocol.event("C_LandscapeMarine constructor", "ASCII grid", isNotError);
		return matriceLue;
	}

	@Override
	/** Initialize both (!) gridValueLayer and container(ex: C_SoilCell) matrices
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *            rev. JLF 2015, 2024 */
	public void createGround(int[][] matriceLue) {
		for (int i = this.dimension_Ucell.width - 1; i >= 0; i--) {
			for (int j = this.dimension_Ucell.height - 1; j >= 0; j--) {
				this.gridValueLayer.set(matriceLue[i][j], i, j);
				this.grid[i][j] = new C_SoilCellMarine(matriceLue[i][j], i, j);
			}
		}
	}
	@Override
	/** Inform inspector then super, JLF 2024 */
	protected void replaceOutcomer(A_VisibleAgent animalLeavingLandscape, double[] newLocation, int x, int y) {
		C_InspectorPopulationMarine.planktonExport++;
		super.replaceOutcomer(animalLeavingLandscape, newLocation, x, y);
	}
}