package thing.ground.landscape;

import repast.simphony.context.Context;
import thing.ground.C_SoilCellMarineNode;

public class C_LandscapeMarineGraphed extends C_LandscapeMarine {
	//
	// CONSTRUCTOR
	//
	public C_LandscapeMarineGraphed(Context<Object> context, String url, String gridValueName,
			String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
	}

	//
	// OVERRIDDEN METHOD
	//
	/**
	 * @param matriceLue the values read in the raster, bitmap / rev. JLF 02.2022
	 */
	@Override
	public void createGround(int[][] matriceLue) {
		super.createGround(matriceLue);
		for (int i = 0; i < this.dimension_Ucell.width; i++) {
			for (int j = 0; j < this.dimension_Ucell.height; j++) {
				int affinity = matriceLue[i][j];
				this.grid[i][j] = new C_SoilCellMarineNode(affinity, i, j);
			}
		}
	}
}
