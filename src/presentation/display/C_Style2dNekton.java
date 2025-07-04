package presentation.display;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantPNMC;
import data.constants.rodents.I_ConstantStringRodents;
import data.constants.rodents.I_ConstantTransportation;
import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;
import thing.ground.landscape.C_Landscape;
public class C_Style2dNekton implements ValueLayerStyleOGL, I_ConstantNumeric, I_ConstantStringRodents,
		I_ConstantTransportation, I_ConstantPNMC {
	//
	// FIELDS
	//
	protected ValueLayer layer; // the layer to represent
	Map<Integer, Color> colorMap;// Used to associate a color to each value of the layer
	//
	// CONSTRUCTOR
	//
	public C_Style2dNekton() {// TODO JLF 2014.11 rename into protocolColorMapFactory
		// we try to get back the colormodel which should be read in the same time as the raster
		this.colorMap = new HashMap<Integer, Color>();
		this.colorMap = C_Landscape.getColormap();
		// if there is no colormap, create one
		if (this.colorMap == null) {
			System.out.print("C_Style2dNekton(); creating ");
			this.colorMap = new HashMap<Integer, Color>();
			this.colorMap = colorMapNekton(this.colorMap);
			System.out.println("PNMC nekton colormap: " + this.colorMap.size() + " colors identified");
		}
	}
	//
	// METHODS
	//
	@Override
	public void init(ValueLayer layer) {
		this.layer = layer;
	}
	public Map<Integer, Color> colorMapNekton(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(58, 124, 76));// terre
		colorMap.put(1, new Color(6, 63, 91));// bleu
		colorMap.put(2, new Color(30, 217, 35));// vert1
		colorMap.put(3, new Color(161, 242, 7));// vert2
		colorMap.put(4, new Color(245, 232, 2));// jaune
		colorMap.put(5, new Color(252, 159, 2));// orange
		colorMap.put(6, new Color(253, 93, 1));// rouge1
		colorMap.put(7, new Color(252, 31, 1));// rouge2
		colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0));
		return colorMap;
	}
	//
	// GETTERS
	//
	@Override
	public Color getColor(double... coordinates) {
		return this.colorMap.get((int) this.layer.get(coordinates));
	}
	@Override
	public float getCellSize() {
		return cellSize.get(0);
	}
}
