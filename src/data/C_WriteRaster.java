package data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.valueLayer.GridValueLayer;
import repast.simphony.space.Dimensions;

public class C_WriteRaster {
	//
	// FIELD
	//
	DecimalFormat df = new DecimalFormat("0.00",DecimalFormatSymbols.getInstance(Locale.US));
	//
	// CONSTRUCTOR
	//
	public C_WriteRaster() {}
	//
	// METHODS
	//
	/** Export valueLayer grid in the selected format */
	public void export(GridValueLayer layer, String name, String format) {
		Dimensions layerDim = layer.getDimensions();
		int width = (int)layerDim.getWidth();
		int height = (int)layerDim.getHeight();
		double[][] grid = new double[width][height];
		for(int i = 0;i<width;i++) for(int j = 0;j<height;j++) grid[i][j] = layer.get(i,j);
		export(grid,name,format);
	}
	/** Export double[][] grid in the selected format */
	public void export(double[][] grid, String name, String format) {
		if(format.equals("CSV")) exportCSV(grid,name);
		if(format.equals("OBJ")) exportOBJ(grid,name);
	}

	/** Export double[][] grid in CSV format */
	private void exportCSV(double[][] grid, String name) {
		long tick = (long)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		int nbColumns = grid.length;
		int nbLines = grid[0].length;
		Path path = Path.of("data_output/grids/"+name+"_"+tick+".csv");
		try(BufferedWriter out = Files.newBufferedWriter(path,StandardCharsets.UTF_8)){
			out.write("# ncols="+nbColumns);
			out.newLine();
			out.write("# nrows="+nbLines);
			out.newLine();
			StringBuilder sb = new StringBuilder(8192);
			for(int j = 0;j<nbLines;j++){
				sb.setLength(0);
				for(int i = 0;i<nbColumns;i++){
					sb.append(df.format(grid[i][j]));
					if(i<nbColumns-1) sb.append(';');
				}
				out.write(sb.toString());
				out.newLine();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/** Export grid in OBJ format */
	private void exportOBJ(double[][] grid, String name) {
		double exagerate = -5.;// exagération verticale
		long tick = (long)RunEnvironment.getInstance().getCurrentSchedule().getTickCount();
		int nbColumns = grid.length;
		int nbLines = grid[0].length;
		Path path = Path.of("data_output/grids/"+name+"_"+tick+".obj");
		try(BufferedWriter out = Files.newBufferedWriter(path,StandardCharsets.UTF_8)){
			out.write("o " + name);
			out.newLine();
			// Sommets
			for(int j = 0;j<nbLines;j++){
				for(int i = 0;i<nbColumns;i++){
//					if (grid[i][j]==99.0)grid[i][j]=-1.;
					double z = grid[i][j]*exagerate;
					out.write(String.format(Locale.US,"v %d %d %.4f%n",i,j,z));
				}
			}
			// Faces
			for(int j = 0;j<nbLines-1;j++){
				for(int i = 0;i<nbColumns-1;i++){
					int a = j*nbColumns+i+1;
					int b = a+1;
					int c = a+nbColumns;
					int d = c+1;
					out.write(String.format("f %d %d %d%n",a,b,d));
					out.write(String.format("f %d %d %d%n",a,d,c));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
