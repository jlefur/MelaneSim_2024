/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.C_ReadRaster;
import data.C_ReadRasterOcean;
import data.constants.I_ConstantPNMC_particules;
import melanesim.protocol.A_Protocol;
import repast.simphony.context.Context;
import repast.simphony.space.continuous.NdPoint;
import thing.A_VisibleAgent;
import thing.C_Plankton;
import thing.dna.C_GenomeAnimalia;
import thing.ground.C_MarineCell;
import thing.ground.I_Container;

/**
 * The global container of MelaneSim's protocols<br>
 * Owns a continuous space and a grid/matrix with values ('affinity'), landplots
 * of marine cells with the same affinity values. Values are read from an ascii
 * file. Affinity values are stored in a gridValueLayer (as well as in the cell
 * container attributes) <br>
 * 
 * @see A_Protocol
 * @author Baduel 2009.04, Le Fur 2009.12, Longueville 2011.02, Le Fur
 *         2011,2012,2015,2024<br>
 *         formerly C_Raster
 */
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
	/**
	 * Initialize both (!) gridValueLayer and container(ex: C_SoilCell) matrices
	 * 
	 * @param matriceLue the values read in the raster, bitmap<br>
	 *                   rev. JLF 2015, 2024
	 */
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
	/**
	 * Move the agent on the continuous space, JLF 2015,2024
	 * 
	 * @param thing               A_Animal
	 * @param moveDistance_Umeter Coordinate in meters SI units
	 */
	public void translate(A_VisibleAgent thing, Coordinate moveDistance_Umeter) {
		// 1. MOVE IN CONTINUOUS SPACE: 1) Retrieve coordinates in continuous space,
		// compute move vector
		NdPoint thingLocation_Ucs = this.continuousSpace.getLocation(thing);
		moveDistance_Ucs.x = moveDistance_Umeter.x / C_Parameters.UCS_WIDTH_Umeter;
		moveDistance_Ucs.y = moveDistance_Umeter.y / C_Parameters.UCS_WIDTH_Umeter; // m/m.cs^-1=cs
		// Check the validity of the displacement, if necessary, this function will
		// modify the given value
		if (!this.checkBordure(thingLocation_Ucs, moveDistance_Ucs, thing)) {
			// Move the agent by mean of the projection's methods
			this.continuousSpace.moveByDisplacement(thing, moveDistance_Ucs.x, moveDistance_Ucs.y);
//			this.continuousSpace.moveTo(thing, thingLocation_Ucs.getX()+moveDistance_Ucs.x, thingLocation_Ucs.getY()+moveDistance_Ucs.y);
			this.checkAndMoveToNewCell(thing);
		}
	}

	/**
	 * Adapted from C_Landscape.checkGoalPosition<br>
	 * Check next position and backward if leaving the continuous space.
	 * 
	 * @param currentPosition_Ucs
	 * @param moveDistance_Ucs
	 * @return true if agent is washed on shore or has to leave domain
	 */
	protected boolean checkBordure(NdPoint currentPosition_Ucs, Coordinate moveDistance_Ucs, A_VisibleAgent agent) {
		NdPoint goalPoint_Ucs = new NdPoint(currentPosition_Ucs.getX() + moveDistance_Ucs.x,
				currentPosition_Ucs.getY() + moveDistance_Ucs.y);

		// if leave world get back BACKWARD_NB_CELLS
		int ix = agent.getCurrentSoilCell().retrieveLineNo();
		int iy = agent.getCurrentSoilCell().retrieveColNo();
		// we test the four cases where the agent is going out back one cell if it is
		// the case
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
		// I_Container newCell = grid[(int) getThingCoord_Ucell(thing).x][(int)
		// getThingCoord_Ucell(thing).y];
		if (goalCell instanceof C_MarineCell) {// security
			double speed = ((C_MarineCell) goalCell).getSpeedEastward_UmeterPerSecond()
					+ ((C_MarineCell) goalCell).getSpeedNorthward_UmeterPerSecond();
			if (speed == 0) {
				return true;
			}
		} else
			System.err.println("C_LandscapeMarine.checkBordure(): " + goalCell + " is not a marine cell");

		return false;
	}

// JUNK chatGPT 04.11.2024 
	// as-tu les informations pour générer le code suivant: si l'agent doit sortir
	// du domaine (c'est à dire si checkBordure est true) alors supprimer l'agent
	// puis chercher une cellule au hasard sur la bordure du domaine où la vitesse
	// est entrante (fait rentrer les particules vers l'intérieur du domaine). Une
	// fois que la cellule est trouvée, créer un nouvel agent (qui remplace l'agent
	// sortant), ensuite le positionner dans la cellule sélectionnée et le déplacer
	// de la distance correspondant à la vitesse du courant dans cette cellule

	public void junkTranslateWithBoundaryCheck(A_VisibleAgent thing, Coordinate moveDistance_Umeter) {
		// 1. MOVE IN CONTINUOUS SPACE: 1) Retrieve coordinates in continuous space,
		// compute move vector
		NdPoint thingLocation_Ucs = this.continuousSpace.getLocation(thing);
		moveDistance_Ucs.x = moveDistance_Umeter.x / C_Parameters.UCS_WIDTH_Umeter;
		moveDistance_Ucs.y = moveDistance_Umeter.y / C_Parameters.UCS_WIDTH_Umeter;

		// Check if the agent is trying to leave the domain
		if (this.checkBordure(thingLocation_Ucs, moveDistance_Ucs, thing)) {
			// Agent is leaving the domain, proceed with the following steps

			// 1. Remove the agent from the domain
			this.removeAgent(thing);

			// 2. Find a random border cell with incoming current
			I_Container borderCell = findRandomIncomingBorderCell();

			if (borderCell != null) {
				// 3. Create a new agent in the selected cell
				A_VisibleAgent newAgent = createNewAgent();
				Coordinate borderCellPosition = borderCell.getCoordinate_Ucs();
				this.continuousSpace.moveTo(newAgent, borderCellPosition.getX(), borderCellPosition.getY());

				// 4. Move the new agent according to the current in the border cell
				if (borderCell instanceof C_MarineCell) {
					C_MarineCell marineCell = (C_MarineCell) borderCell;
					double currentEast = marineCell.getSpeedEastward_UmeterPerSecond();
					double currentNorth = marineCell.getSpeedNorthward_UmeterPerSecond();

					// Convert current speed to UCS units
					double moveEast_Ucs = currentEast / C_Parameters.UCS_WIDTH_Umeter;
					double moveNorth_Ucs = currentNorth / C_Parameters.UCS_WIDTH_Umeter;

					// Move new agent by the current vector within the cell
					this.continuousSpace.moveByDisplacement(newAgent, moveEast_Ucs, moveNorth_Ucs);
					this.checkAndMoveToNewCell(newAgent); // Update cell if necessary
				}
			}
		} else {
			// Normal movement within the domain if checkBordure is false
			this.continuousSpace.moveByDisplacement(thing, moveDistance_Ucs.x, moveDistance_Ucs.y);
			this.checkAndMoveToNewCell(thing);
		}
	}

	// Helper method to find a random cell on the border with incoming current
	private I_Container findRandomIncomingBorderCell() {
		List<I_Container> borderCells = getBorderCells();
		Random rand = new Random();

		// Shuffle and iterate through the border cells to find one with incoming
		// current
		Collections.shuffle(borderCells);
		for (I_Container cell : borderCells) {
			if (cell instanceof C_MarineCell) {
				C_MarineCell marineCell = (C_MarineCell) cell;
				double currentEast = marineCell.getSpeedEastward_UmeterPerSecond();
				double currentNorth = marineCell.getSpeedNorthward_UmeterPerSecond();

				// Check if current is incoming (negative for east and/or north)
				if (currentEast <= 0 && currentNorth <= 0) {
					return cell; // Found a cell with incoming current
				}
			}
		}
		return null; // No suitable cell found
	}

	// Placeholder for removing an agent
	private void removeAgent(A_VisibleAgent agent) {
		// Implementation to remove agent from the simulation
	}

	// Placeholder for creating a new agent
	private A_VisibleAgent createNewAgent() {
		// Implementation to create and return a new agent instance
		return new C_Plankton(new C_GenomeAnimalia());
	}

	// Placeholder for obtaining all cells on the border of the domain
	private List<I_Container> getBorderCells() {
		// Implementation to retrieve the list of cells located on the border of the
		// domain
		return new ArrayList<>();
	}

}