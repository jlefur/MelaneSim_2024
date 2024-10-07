/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.C_ReadRaster;
import data.C_ReadRasterOcean;
import data.constants.I_ConstantPNMC_particules;
import melanesim.protocol.A_Protocol;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import thing.A_VisibleAgent;
import thing.ground.C_MarineCell;
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
	// FIELD
	//
	private static Coordinate moveDistance_Ucs = new Coordinate();// junk used to fasten computation JLF2024
	//
	// CONSTRUCTOR
	//
	public C_LandscapeMarine(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
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
	//
	// OVERRIDEN METHODS
	//
	/** Initialize both (!) gridValueLayer and container(ex: C_SoilCell) matrices
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *            rev. JLF 2015, 2024 */
	@Override
	public void createGround(int[][] matriceLue) {
		for (int i = this.dimension_Ucell.width - 1; i >= 0; i--) {
			for (int j = this.dimension_Ucell.height - 1; j >= 0; j--) {
				this.gridValueLayer.set(matriceLue[i][j], i, j);
				this.grid[i][j] = new C_MarineCell(matriceLue[i][j], i, j);
			}
		}
	}
	@Override
	/** Move the agent on the continuous space, JLF 2015,2024
	 * @param thing A_Animal
	 * @param moveDistance_Umeter Coordinate in meters SI units */
	public void translate(A_VisibleAgent thing, Coordinate moveDistance_Umeter) {
		// 1. MOVE IN CONTINUOUS SPACE: 1) Retrieve coordinates in continuous space, compute move vector
		NdPoint thingLocation_Ucs = this.continuousSpace.getLocation(thing);
		moveDistance_Ucs.x = moveDistance_Umeter.x / C_Parameters.UCS_WIDTH_Umeter;
		moveDistance_Ucs.y = moveDistance_Umeter.y / C_Parameters.UCS_WIDTH_Umeter; // m/m.cs^-1=cs
		// Check the validity of the displacement, if necessary, this function will modify the given value
		if (!this.checkBordure(thingLocation_Ucs, moveDistance_Ucs, thing)) {
			// Move the agent by mean of the projection's methods
			this.continuousSpace.moveByDisplacement(thing, moveDistance_Ucs.x, moveDistance_Ucs.y);
//			this.continuousSpace.moveTo(thing, thingLocation_Ucs.getX()+moveDistance_Ucs.x, thingLocation_Ucs.getY()+moveDistance_Ucs.y);
			this.checkAndMoveToNewCell(thing);
		}
	}

	/** Adapted from C_Landscape.checkGoalPosition<br>
	 * Check next position and backward if leaving the continuous space.
	 * @param currentPosition_Ucs
	 * @param moveDistance_Ucs
	 * @return true if agent is washed on shore or has to leave domain */
	protected boolean checkBordure(NdPoint currentPosition_Ucs, Coordinate moveDistance_Ucs, A_VisibleAgent agent) {
		NdPoint goalPoint_Ucs = new NdPoint(currentPosition_Ucs.getX() + moveDistance_Ucs.x, currentPosition_Ucs.getY()
				+ moveDistance_Ucs.y);

		// if leave world get back BACKWARD_NB_CELLS
		int ix = agent.getCurrentSoilCell().retrieveLineNo();
		int iy = agent.getCurrentSoilCell().retrieveColNo();
		// we test the four cases where the agent is going out back one cell if it is the case
		if (goalPoint_Ucs.getX() < 0) {
			this.moveToContainer(agent, this.grid[ix + BACKWARD_NB_CELLS][iy]);
			this.moveToLocation(agent, this.grid[ix + BACKWARD_NB_CELLS][iy].getCoordinate_Ucs());
			return true;
		}
		if (goalPoint_Ucs.getX() >= continuousSpace.getDimensions().getWidth()) {
			this.moveToContainer(agent, this.grid[ix - BACKWARD_NB_CELLS][iy]);
			this.moveToLocation(agent, this.grid[ix - BACKWARD_NB_CELLS][iy].getCoordinate_Ucs());
			return true;
		}
		if (goalPoint_Ucs.getY() < 0) {
			this.moveToContainer(agent, this.grid[ix][iy + BACKWARD_NB_CELLS]);
			this.moveToLocation(agent, this.grid[ix][iy + BACKWARD_NB_CELLS].getCoordinate_Ucs());
			return true;
		}
		if (goalPoint_Ucs.getY() >= continuousSpace.getDimensions().getHeight()) {
			this.moveToContainer(agent, this.grid[ix][iy - BACKWARD_NB_CELLS]);
			this.moveToLocation(agent, this.grid[ix][iy - BACKWARD_NB_CELLS].getCoordinate_Ucs());
			return true;
		}

		// Do not transport particles to earth ground
		double a = goalPoint_Ucs.getX(), b = goalPoint_Ucs.getY();
		I_Container goalCell = this.grid[(int) a][(int) b];
		// I_Container newCell = grid[(int) getThingCoord_Ucell(thing).x][(int) getThingCoord_Ucell(thing).y];
		if (goalCell instanceof C_MarineCell) {// security
			double speed = ((C_MarineCell) goalCell).getSpeedEastward_UmeterPerSecond() + ((C_MarineCell) goalCell)
					.getSpeedNorthward_UmeterPerSecond();
			if (speed == 0) {
				return true; }
		}
		else System.err.println("C_LandscapeMarine.checkBordure(): " + goalCell + " is not a marine cell");

		return false;
	}
}