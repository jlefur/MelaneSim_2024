/* This source code is licensed under a BSD licence */
package thing.ground.landscape;

import data.constants.I_ConstantPNMC;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;

/** @author Le Fur 2025 */
public class C_LandscapeMarineNekton extends C_LandscapeMarine implements I_ConstantPNMC {
	//
	// FIELD
	//
	protected GridValueLayer nektonValueLayer;
	//
	// CONSTRUCTOR
	//
	public C_LandscapeMarineNekton(Context<Object> context, String url, String gridValueName,
			String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
		context.addValueLayer(this.nektonValueLayer);
	}
	//
	// OVERRIDEN METHOD
	//
	@Override
	/** Super then randomly Initialize nektonValueLayer @author JLF 2025 */
	public void createGround(int[][] matriceLue) {
		super.createGround(matriceLue);
		this.nektonValueLayer = new GridValueLayer(NEKTON_GRID, true, new repast.simphony.space.grid.WrapAroundBorders(),
				dimension_Ucell.width, dimension_Ucell.height);
		for (int i = this.dimension_Ucell.width - 1; i >= 0; i--)
			for (int j = this.dimension_Ucell.height - 1; j >= 0; j--)
				this.nektonValueLayer.set((int) (Math.random() * 7), i, j);
	}
	//
	// GETTER
	//
	public GridValueLayer getNektonValueLayer() {
		return nektonValueLayer;
	}
}