//
// JEAN LE FUR - ENTROPIE
//
/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data.converters;

import java.util.HashMap;

import data.C_ReadRaster;
import data.constants.I_ConstantPNMC;
import data.constants.rodents.I_ConstantGerbil;
import data.constants.rodents.I_ConstantStringRodents;
import presentation.dataOutput.C_FileWriter;

/** Read the bitmap, rescale colors and save the grid in ASCII
 * @author M.Sall 10.2015, rev. MS&JLF 04.2016, JLF 10.2023 */
public class C_ConvertBitmap implements I_ConstantStringRodents, I_ConstantGerbil, I_ConstantPNMC {
	//
	// METHODS
	//
	/** Read the rain raster bitmap file and build the corresponding matrix */
	public int[][] readRaster(String fileName) {
		int[][] rasterMatrix = C_ReadRaster.imgRasterLoader(RASTER_PATH_MELANESIA + fileName);
//		rasterMatrix = rescaleColorValues(rasterMatrix);
		System.out.println(fileName + " bitmap read ");
		return rasterMatrix;
	}
	/** Save the grid in the file following the name passed in args */
	public static void saveRasterFile(int[][] matriceLue, String fileName, String path) {
		HashMap<Integer, Integer> compteur = new HashMap<Integer, Integer>();
		int nbColumns;
		int nbLines;
		C_FileWriter rasterFile_Utxt;
		// Create or recreate empty file
		nbColumns = matriceLue.length;
		nbLines = matriceLue[0].length;
		rasterFile_Utxt = new C_FileWriter(RASTER_PATH_MELANESIA + path + "/" + fileName + ".grd", false);
		// Compute max and min values of the matrix
		int maxValue = 0;
		int minValue = Integer.MAX_VALUE;
		for (int i = 0; i < nbColumns; i++) {
			for (int j = 0; j < nbLines; j++) {
				if (maxValue < matriceLue[i][j]) maxValue = matriceLue[i][j];
				if (minValue > matriceLue[i][j]) minValue = matriceLue[i][j];
			}
		}
		// Raster file save header for surfer grid format
		System.out.println("C_ConvertBitmap.saveRasterFile(): Writing file");
		rasterFile_Utxt.writeln("DSAA");
		rasterFile_Utxt.writeln(nbColumns + " " + nbLines);
		rasterFile_Utxt.writeln(1 + " " + (nbColumns - 1));
		rasterFile_Utxt.writeln(1 + " " + (nbLines - 1));
		rasterFile_Utxt.write(minValue + " " + maxValue);
		// Raster file add values
		// for (int i = nbLines - 1; i >= 0; i--) { //For MelaneSim
		for (int i = 0; i <= nbLines - 1; i++) {// For Surfer
			rasterFile_Utxt.writeln("");
			rasterFile_Utxt.write(matriceLue[0][i] + "");
			// for (int j = nbColumns-1; j >= 0; j--) // For Surfer
			for (int j = 1; j < nbColumns; j++) { // For MelaneSim
				rasterFile_Utxt.write(" " + matriceLue[j][i]);
				if (compteur.get(matriceLue[j][i]) == null) compteur.put(matriceLue[j][i], 1);
				else compteur.put(matriceLue[j][i], compteur.get(matriceLue[j][i]) + 1);
			}
		}
		for (int i = 0; i < compteur.size(); i++) {
			System.out.println("couleur " + i + " : " + compteur.get(i));
		}
		rasterFile_Utxt.closeFile();
		System.out.println("C_ConvertBitmap.saveRasterFile(): " + RASTER_PATH_MELANESIA + path + fileName
				+ ".grd conversion complete");
	}
	/** Replace all occurrences of old value with new value in the grid */
	public int[][] replaceValueInMatrix(int oldValue, int newValue, int[][] grid) {
		if (checkOccurrenceInMatrix(newValue, grid)) {
			System.err.println(newValue + " already exist, impossible to change with " + oldValue);
			return null;
		}
		int oldValueSaved = 0, frontierValue = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == oldValue) {
					grid[i][j] = newValue;
					oldValueSaved = newValue;
				}
				// When frontierValue is encountered, replace with preceding cell value. Used only once
				else {
					if (grid[i][j] == frontierValue) grid[i][j] = oldValueSaved;
					else oldValueSaved = grid[i][j];
				}
			}
		}
		return grid;
	}
	/** @return true if the requested value exist in matrix */
	public boolean checkOccurrenceInMatrix(int value, int[][] matrix) {
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[0].length; j++)
				if (matrix[i][j] == value) return true;
		return false;
	}
	/** Rain bitmap values are arbitrary color values, order them to the corresponding levels of rain intensity */
	public int[][] rescaleColorValues(int[][] matriceLue) {
		int[][] newMatrix = matriceLue;
		newMatrix = replaceValueInMatrix(30, 666, newMatrix);
		newMatrix = replaceValueInMatrix(23, 333, newMatrix);
		newMatrix = replaceValueInMatrix(28, -50, newMatrix);
		newMatrix = replaceValueInMatrix(16, -56, newMatrix);
		newMatrix = replaceValueInMatrix(20, -70, newMatrix);
		newMatrix = replaceValueInMatrix(4, -150, newMatrix);
		newMatrix = replaceValueInMatrix(22, -300, newMatrix);
		newMatrix = replaceValueInMatrix(14, -450, newMatrix);
		newMatrix = replaceValueInMatrix(24, -550, newMatrix);
		newMatrix = replaceValueInMatrix(13, -600, newMatrix);
		newMatrix = replaceValueInMatrix(21, -800, newMatrix);
		newMatrix = replaceValueInMatrix(18, -1200, newMatrix);
		newMatrix = replaceValueInMatrix(3, -1500, newMatrix);
		newMatrix = replaceValueInMatrix(27, -1800, newMatrix);
		newMatrix = replaceValueInMatrix(11, -2100, newMatrix);
		newMatrix = replaceValueInMatrix(10, -2300, newMatrix);
		newMatrix = replaceValueInMatrix(1, -2500, newMatrix);
		newMatrix = replaceValueInMatrix(19, -2600, newMatrix);
		newMatrix = replaceValueInMatrix(25, -2766, newMatrix);
		newMatrix = replaceValueInMatrix(26, -2800, newMatrix);
		newMatrix = replaceValueInMatrix(2, -3000, newMatrix);
		newMatrix = replaceValueInMatrix(9, -3100, newMatrix);
		newMatrix = replaceValueInMatrix(17, -3300, newMatrix);
		newMatrix = replaceValueInMatrix(7, -4250, newMatrix);
		newMatrix = replaceValueInMatrix(6, -4700, newMatrix);
		newMatrix = replaceValueInMatrix(5, -5600, newMatrix);
		newMatrix = replaceValueInMatrix(12, -6200, newMatrix);
		newMatrix = replaceValueInMatrix(29, -6550, newMatrix);
		newMatrix = replaceValueInMatrix(8, -6750, newMatrix);
		newMatrix = replaceValueInMatrix(15, -2200, newMatrix);
		newMatrix = replaceValueInMatrix(0, -2250, newMatrix);
		return newMatrix;
	}
	//
	// MAIN
	//
	public static void main(String[] args) {
		// new C_OutputConsole();
		int[][] matriceLue;
		C_ConvertBitmap converter = new C_ConvertBitmap();
		// build matrix
		// matriceLue = converter.readRaster("20231018-PNMC.4a.300x201_test.bmp");
		matriceLue = converter.readRaster("test2.bmp");
//		matriceLue = converter.readRaster("20231025-Temperatures/20231124-temperatures_sansNoir.3c_40couleurs.bmp");
		// C_ConvertBitmap.saveRasterFile(matriceLue, "20231009-PNMC.3a_30couleurs", "");
		// C_ConvertBitmap.saveRasterFile(matriceLue, "Legendre_Sans nom-2", "");
		/*
		 * for (int i = 0; i < matriceLue.length; i++) { for (int j = 0; j < matriceLue[0].length; j++) { if (matriceLue[i][j] ==
		 * 66) { if (i != 0) { matriceLue[i][j] = matriceLue[i - 1][j]; } else matriceLue[i][j] = matriceLue[i][j - 1]; } } }
		 */
		C_ConvertBitmap.saveRasterFile(matriceLue, "test2", "");
		System.out.println("conversion complete!!!");
	}
}
