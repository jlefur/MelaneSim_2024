/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.C_ReadRasterOcean;
import data.constants.I_ConstantPNMC;
import melanesim.C_ContextCreator;
import melanesim.protocol.A_Protocol;
import melanesim.util.CaptureEcranPeriodique;
import presentation.epiphyte.C_InspectorPopulationMarine;
import repast.simphony.context.Context;
import repast.simphony.valueLayer.GridValueLayer;
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
public class C_LandscapeMarine extends C_Landscape implements I_ConstantPNMC {
	//
	// FIELDS
	//
	protected GridValueLayer energyValueLayer;

	// energy values are 0-green, 1-orange, 2-red or 3-black forN/A ( viz. land)
	protected int[] energyRanks = new int[3];
	public static double overallEnergy_Ukcal = 0.0;
	public static double overallEnergyMean_Ukcal = 0.0;

	//
	// CONSTRUCTOR
	//
	public C_LandscapeMarine(Context<Object> context, String url, String gridValueName, String continuousSpaceName) {
		super(context, url, gridValueName, continuousSpaceName);
		context.addValueLayer(this.energyValueLayer);
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
	 *            rev. JLF 2015, 2024, 2025 */
	public void createGround(int[][] matriceLue) {
		this.energyValueLayer = new GridValueLayer(energyGridvalues, true,
				new repast.simphony.space.grid.WrapAroundBorders(), dimension_Ucell.width, dimension_Ucell.height);
		for (int i = this.dimension_Ucell.width - 1; i >= 0; i--) {
			for (int j = this.dimension_Ucell.height - 1; j >= 0; j--) {
				// TODO number in source Terrestrial in raster bathymetry JLF 04.2025;
				if (matriceLue[i][j] >= 8) matriceLue[i][j] = TERRESTRIAL_MIN_AFFINITY;
				this.getValueLayer().set(matriceLue[i][j], i, j);
				// energy values are 0-green, 1-orange, 2-red or 3-black forN/A ( viz. land)
				this.energyValueLayer.set((int) (Math.random() * 3), i, j);
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

	@Override
	/** Results in the exit of an agent and the entry of a new one in a random point in the grid
	 * @author Longueville 2011, rev. JLF 11.2015, 04.2025
	 * @param animalLeavingLandscape will be removed and a new one of the same class will enter */
	protected void bordure(A_VisibleAgent animalLeavingLandscape) {// TODO JLF 2015.11 change name (agentLeaving)
		// Identify on which side the new animal will appear
		if (animalLeavingLandscape.isDead()) return;// Could leave landscape in the middle of a step.
		double[] newLocation = new double[2];
		int x = 0, y = 0;
		do {
			int randkey = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * 4);// TODO number in source
			x = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
					.getWidth());
			y = (int) (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() * this.dimension_Ucell
					.getHeight());
		} while (((C_SoilCellMarine) grid[x][y]).isTerrestrial());
		newLocation[0] = x * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
		newLocation[1] = y * C_Parameters.CELL_SIZE_UcontinuousSpace; // cell / cs.cell^-1 -> cs;
		replaceOutcomer(animalLeavingLandscape, newLocation, x, y);
	}

	/** recompute marine cells energy<br>
	 * JLF 03.2025 */
	public void assertCellsEnergy() {
		double cellIntegralEnergy_Ukcal;
		this.rankEnergy();
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				this.energyValueLayer.set(ENERGY_RESET, i, j);// reset cell color
				if (((C_SoilCellMarine) grid[i][j]).isTerrestrial()) this.energyValueLayer.set(ENERGY_LAND, i, j);
				cellIntegralEnergy_Ukcal = ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal();
				if (cellIntegralEnergy_Ukcal >= energyRanks[ENERGY_GREEN])
					this.energyValueLayer.set(ENERGY_GREEN, i, j);
				else
					if (cellIntegralEnergy_Ukcal >= energyRanks[ENERGY_ORANGE])
						this.energyValueLayer.set(ENERGY_ORANGE, i, j);
					else
						if (cellIntegralEnergy_Ukcal >= energyRanks[ENERGY_RED])
							this.energyValueLayer.set(ENERGY_RED, i, j);
				((C_SoilCellMarine) grid[i][j]).setIntegralEnergy_Ukcal(0.);
			}
		}
	}

	/** fill energyRanks map with the current thresholds<br>
	 * The cells energies distribution is heavily asymmetrical (generalized Pareto distribution or truncated Zipf distribution),
	 * hence the ranks are asymmetrical<br>
	 * @author JLF 03.2025 */
	protected void rankEnergy() {
		C_LandscapeMarine.overallEnergy_Ukcal = 0.0;
		// Put each energy rank in keys of a sorted map and fill values with the corresponding energy sum
		TreeMap<Integer, Integer> energyByRank = new TreeMap<>();
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				Integer cellIntegralEnergy_Ukcal = (int) ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal();
				if (energyByRank.get(cellIntegralEnergy_Ukcal) != null)
					energyByRank.put(cellIntegralEnergy_Ukcal, energyByRank.get(cellIntegralEnergy_Ukcal)
							+ cellIntegralEnergy_Ukcal);
				else// If energy key not found, create an entry and set values
					energyByRank.put(cellIntegralEnergy_Ukcal, cellIntegralEnergy_Ukcal);
				// NB: chatGPT suggère de remplacer les 4 lignes ci-dessous par:
				// EnergyByRank.compute(cellEnergy_Ukcal, (k, v) -> (v == null) ? cellEnergy_Ukcal : v + cellEnergy_Ukcal);

				overallEnergy_Ukcal += cellIntegralEnergy_Ukcal;
			}
		}

		// Définition des seuils d'énergie (Temporary stores the threshold to proceed to the coming loop)
		energyRanks[ENERGY_GREEN] = (int) (overallEnergy_Ukcal * GREEN_AREA_Upercent);
		energyRanks[ENERGY_ORANGE] = (int) (overallEnergy_Ukcal * ORANGE_AREA_Upercent);
		energyRanks[ENERGY_RED] = (int) (overallEnergy_Ukcal * RED_AREA_Upercent);

		// Classement des valeurs pour atteindre les seuils
		for (int i = 0; i < 3; i++) {
			int targetSum = energyRanks[i];
			// additionner les valeurs en partant de la clé la plus grande
			double currentSum = 0.0;
			for (Map.Entry<Integer, Integer> entry : energyByRank.descendingMap().entrySet()) {
				if (currentSum >= targetSum) {
					energyRanks[i] = entry.getKey();// the threshold key
					break;// Arrêter si la somme cible est atteinte
				}
				currentSum += entry.getValue();
			}
		}
		C_LandscapeMarine.overallEnergyMean_Ukcal = C_LandscapeMarine.overallEnergy_Ukcal / (this.dimension_Ucell
				.getWidth() * this.dimension_Ucell.getHeight());
	}

	/*
	 * protected void rankEnergy0() { // Put each energy rank in keys of a sorted map and fill values with the corresponding
	 * energy sum double cellEnergy_Ukcal, overallEnergy_Ukcal = 0.; TreeMap<Double, Double> EnergyByRank = new TreeMap<>(); for
	 * (int i = 0; i < this.dimension_Ucell.getWidth(); i++) { for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
	 * cellEnergy_Ukcal = ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal(); if (EnergyByRank.get(cellEnergy_Ukcal) !=
	 * null) { EnergyByRank.put(cellEnergy_Ukcal, EnergyByRank.get(cellEnergy_Ukcal) + cellEnergy_Ukcal); } // If not, create an
	 * entry and set values else EnergyByRank.put(cellEnergy_Ukcal, cellEnergy_Ukcal); overallEnergy_Ukcal += cellEnergy_Ukcal; }
	 * } // Temporary stores the threshold to proceed to the coming loop energyRanks[ENERGY_GREEN] = overallEnergy_Ukcal *
	 * GREEN_AREA_Upercent; energyRanks[ENERGY_ORANGE] = overallEnergy_Ukcal * ORANGE_AREA_Upercent; energyRanks[ENERGY_RED] =
	 * overallEnergy_Ukcal * RED_AREA_Upercent; double targetSum = 0., currentSum = 0.; for (int i = 0; i < 3; i++) {// for each
	 * threshold targetSum = energyRanks[i]; currentSum = 0.0; for (Map.Entry<Double, Double> entry :
	 * EnergyByRank.descendingMap().entrySet()) { if (currentSum >= targetSum) { energyRanks[i] = entry.getKey(); break; }
	 * currentSum += entry.getValue(); } } }
	 */
}