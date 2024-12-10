/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import data.constants.I_ConstantString;
import melanesim.protocol.A_Protocol;
import presentation.epiphyte.C_InspectorOrnithodorosSonrai;
import presentation.epiphyte.C_InspectorPopulationRodent;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import repast.simphony.util.ContextUtils;
import thing.A_Animal;
import thing.A_Organism;
import thing.ground.C_SoilCell;
import thing.rodents.C_OrnitodorosSonrai;
import thing.rodents.C_Rodent;

/** The global container of a given protocol.<br>
 * 1/ Owns a grid/matrix with values ('affinity'), landplots of cells with the same affinity values.<br>
 * Values are read from a file. It can be an ASCII text grid or an image raster. (the image must be in grey levels or in 256 or
 * less colors). affinity values are stored in a gridValueLayer (as well as in the cell container attributes) <br>
 * 2/ Owns a continuous space
 * @see A_Protocol
 * @author Baduel 2009.04, Le Fur 2009.12, Longueville 2011.02, Le Fur 02.2011, 07.2012, 04.2015<br>
 *         rev. JLF 10.2015, 11.2015 - was formerly C_Raster <br>
 *         TODO JLF 2020.04 Should be normally in ground package */
public class C_LandscapeRodent extends C_Landscape implements I_ConstantString {
	public C_LandscapeRodent(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
	}
	/** Addition of new agent in simulation. The agent is not sexed in this method. And get the position of it's "parents".
	 * @param parent the parents agent
	 * @param child the new agent it's sex can be provided by its type.<br>
	 *            Author Jean-Emmanuel Longueville 2011-01 */
	@Override
	public void addChildAgent(A_Animal parent, A_Animal child) { // TODO JLF 2015.08 move to Animal (same as digBurrow)
		double[] new_location = new double[2];
		NdPoint p_Ucs = continuousSpace.getLocation(parent);
		new_location[0] = p_Ucs.getX();
		new_location[1] = p_Ucs.getY();
		parent.getCurrentSoilCell().agentIncoming(child);
		Context<Object> context = ContextUtils.getContext(parent);
		context.add(child);
		// TODO JLF 2015.10 epiphyte in business, move to protocol ?
		if (child instanceof C_Rodent) C_InspectorPopulationRodent.addRodentToBirthList((C_Rodent) child);
		// TODO MS 2020.04 Add birth tick in the corresponding inspector list!
		// TODO MS de JLF 2022.01 reference to OrnitodorosSonrai misplaced + epiphyte in business, move to protocol ?
		else
			if (child instanceof C_OrnitodorosSonrai)
				C_InspectorOrnithodorosSonrai.addTickBirthList((C_OrnitodorosSonrai) child);
		continuousSpace.moveTo(child, new_location);
		child.bornCoord_Umeter = getThingCoord_Umeter(child);
	}
	@Override
	protected void updateInspectors(A_Organism incomer) { // JLF 2016.05 cannot add rodent to birthList since it has age of sexual
														// maturity (see above) ?
		if (incomer instanceof C_Rodent) C_InspectorPopulationRodent.addRodentToList((C_Rodent) incomer);
	}
	/** Initialize both (!) gridValueLayer and container(ex: C_SoilCell) matrices
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *            rev. JLF 2015, 2024 */
	@Override
	public void createGround(int[][] matriceLue) {
		for (int i = 0; i<=this.dimension_Ucell.width - 1; i++) {
			for (int j = 0; j<=this.dimension_Ucell.height - 1; j++) {
				this.gridValueLayer.set(matriceLue[i][j], i, j);
				this.grid[i][j] = new C_SoilCell(matriceLue[i][j], i, j);
			}
		}
	}
}