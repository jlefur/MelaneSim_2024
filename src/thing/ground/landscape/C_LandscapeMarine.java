/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package thing.ground.landscape;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.locationtech.jts.geom.Coordinate;

import data.C_Parameters;
import data.C_ReadRasterOcean;
import data.constants.I_ConstantPNMC;
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
	protected Double[] energyRanks = new Double[3];
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
				this.gridValueLayer.set(matriceLue[i][j], i, j);
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
	public void resetCellsColor() {
		super.resetCellsColor();
		this.assertCellsEnergy();
		// uncomment lines below to save screen
		/** save screen each new day */
		// Integer currentYear = A_Protocol.protocolCalendar.get(Calendar.YEAR);
		// Integer currentMonth = A_Protocol.protocolCalendar.get(Calendar.MONTH);
		// String currentDate = "Energy-" + currentYear + "." + String.format("%02d", currentMonth + 1);
		// CaptureEcranPeriodique.captureEcranPlankton(currentDate);
		// end of uncomment
		C_Parameters.BLACK_MAP = false;
	}

	/** recompute marine cells energy<br>
	 * JLF 03.2025 */
	protected void assertCellsEnergy() {
		double cellEnergy_Ukcal;
		this.rankEnergy();
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				this.energyValueLayer.set(ENERGY_RESET, i, j);// reset cell color
				if (((C_SoilCellMarine) grid[i][j]).isTerrestrial()) this.energyValueLayer.set(ENERGY_LAND, i, j);
				cellEnergy_Ukcal = ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal();
				if (cellEnergy_Ukcal >= energyRanks[ENERGY_GREEN]) this.energyValueLayer.set(ENERGY_GREEN, i, j);
				else if (cellEnergy_Ukcal >= energyRanks[ENERGY_ORANGE]) this.energyValueLayer.set(ENERGY_ORANGE, i, j);
				else if (cellEnergy_Ukcal >= energyRanks[ENERGY_RED]) this.energyValueLayer.set(ENERGY_RED, i, j);
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
		TreeMap<Double, Double> EnergyByRank = new TreeMap<>();
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				final double cellEnergy_Ukcal = ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal(); // Finalisé

				if (EnergyByRank.get(cellEnergy_Ukcal) != null)
					EnergyByRank.put(cellEnergy_Ukcal, EnergyByRank.get(cellEnergy_Ukcal) + cellEnergy_Ukcal);
				else// If energy key not found, create an entry and set values
					EnergyByRank.put(cellEnergy_Ukcal, cellEnergy_Ukcal);

				// NB: chatGPT suggère de remplacer les 4 lignes ci-dessous par:
				// EnergyByRank.compute(cellEnergy_Ukcal, (k, v) -> (v == null) ? cellEnergy_Ukcal : v + cellEnergy_Ukcal);

				overallEnergy_Ukcal += cellEnergy_Ukcal;
			}
		}

		// Définition des seuils d'énergie (Temporary stores the threshold to proceed to the coming loop)
		energyRanks[ENERGY_GREEN] = overallEnergy_Ukcal * GREEN_AREA_Upercent;
		energyRanks[ENERGY_ORANGE] = overallEnergy_Ukcal * ORANGE_AREA_Upercent;
		energyRanks[ENERGY_RED] = overallEnergy_Ukcal * RED_AREA_Upercent;

		// Classement des valeurs pour atteindre les seuils
		for (int i = 0; i < 3; i++) {
			double targetSum = energyRanks[i];
			// additionner les valeurs en partant de la clé la plus grande
			double currentSum = 0.0;
			for (Map.Entry<Double, Double> entry : EnergyByRank.descendingMap().entrySet()) {
				if (currentSum >= targetSum) {
					energyRanks[i] = entry.getKey();// the threshold key
					break;// Arrêter si la somme cible est atteinte
				}
				currentSum += entry.getValue();
			}
		}
		C_LandscapeMarine.overallEnergyMean_Ukcal = C_LandscapeMarine.overallEnergy_Ukcal / (this.dimension_Ucell.getWidth()*this.dimension_Ucell.getHeight());
	}

	protected void rankEnergy0() {
		// Put each energy rank in keys of a sorted map and fill values with the corresponding energy sum
		double cellEnergy_Ukcal, overallEnergy_Ukcal = 0.;
		TreeMap<Double, Double> EnergyByRank = new TreeMap<>();
		for (int i = 0; i < this.dimension_Ucell.getWidth(); i++) {
			for (int j = 0; j < this.dimension_Ucell.getHeight(); j++) {
				cellEnergy_Ukcal = ((C_SoilCellMarine) grid[i][j]).getIntegralEnergy_Ukcal();
				if (EnergyByRank.get(cellEnergy_Ukcal) != null) {
					EnergyByRank.put(cellEnergy_Ukcal, EnergyByRank.get(cellEnergy_Ukcal) + cellEnergy_Ukcal);
				}
				// If not, create an entry and set values
				else EnergyByRank.put(cellEnergy_Ukcal, cellEnergy_Ukcal);
				overallEnergy_Ukcal += cellEnergy_Ukcal;
			}
		}
		// Temporary stores the threshold to proceed to the coming loop
		energyRanks[ENERGY_GREEN] = overallEnergy_Ukcal * GREEN_AREA_Upercent;
		energyRanks[ENERGY_ORANGE] = overallEnergy_Ukcal * ORANGE_AREA_Upercent;
		energyRanks[ENERGY_RED] = overallEnergy_Ukcal * RED_AREA_Upercent;
		double targetSum = 0., currentSum = 0.;
		for (int i = 0; i < 3; i++) {// for each threshold
			targetSum = energyRanks[i];

			currentSum = 0.0;
			for (Map.Entry<Double, Double> entry : EnergyByRank.descendingMap().entrySet()) {
				if (currentSum >= targetSum) {
					energyRanks[i] = entry.getKey();
					break;
				}
				currentSum += entry.getValue();
			}
		}
	}
}