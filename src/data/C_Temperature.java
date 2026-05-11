/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package data;

import java.util.Calendar;
import java.util.Date;

import data.constants.I_ConstantPNMC;
import melanesim.protocol.A_Protocol;

/** Loads the 12 monthly sea surface temperature grids from PNMC_temperatures_2021/*.grd
 * and provides temperature at any grid cell for a given date */
public class C_Temperature implements I_ConstantPNMC {
	//
	// FIELDS
	//
	/** 12 monthly grids indexed 0=Jan ... 11=Dec, double[width][height] in Celsius */
	private double[][][] monthlyGrids = new double[12][][];
	//
	// CONSTRUCTOR
	//
	public C_Temperature() {
		for (int month = 0; month < 12; month++) {
			String url = buildUrl(month);
			monthlyGrids[month] = C_ReadRasterDouble.doubleRasterLoader(url);
			if (monthlyGrids[month] == null)
				A_Protocol.event("C_Temperature()", "Failed to load: " + url, isError);
		}
	}
	//
	// METHODS
	//
	private String buildUrl(int monthIndex) {
		if (monthIndex < 9)
			return RASTER_PATH_MELANESIA + "PNMC_temperatures_2021/2021" + "0" + (monthIndex + 1) + ".grd";
		else
			return RASTER_PATH_MELANESIA + "PNMC_temperatures_2021/2021" + (monthIndex + 1) + ".grd";
	}
	/** @return sea surface temperature (°C) at grid cell (i,j) for the given date, or NaN if unavailable */
	public double getTemperature_UCelsius(Date date, int i, int j) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		double[][] grid = monthlyGrids[cal.get(Calendar.MONTH)];
		if (grid == null) return Double.NaN;
		return grid[i][j];
	}
	//
	// SETTERS & GETTERS
	//
	public double[][] getMonthlyGrid(int month) {
		return monthlyGrids[month];
	}
}
