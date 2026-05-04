package presentation.display;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantPNMC;
import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;
import thing.ground.landscape.C_Landscape;

/** Color style for the temperature value layer (8 categories, blue to red gradient)
 * @author MIAGE-2026 */
public class C_Style2dTemperature implements ValueLayerStyleOGL, I_ConstantNumeric, I_ConstantPNMC {
	//
	// FIELDS
	//
	protected ValueLayer layer;
	Map<Integer, Color> colorMap;
	//
	// CONSTRUCTOR
	//
	public C_Style2dTemperature() {
		this.colorMap = C_Landscape.getColormap();
		if (this.colorMap == null) {
			System.out.print("C_Style2dTemperature(); creating ");
			this.colorMap = new HashMap<Integer, Color>();
			this.colorMap = colorMapTemperature(this.colorMap);
			System.out.println("PNMC temperature colormap: " + this.colorMap.size() + " colors identified");
		}
	}
	//
	// METHODS
	//
	@Override
	public void init(ValueLayer layer) {
		this.layer = layer;
	}

	public Map<Integer, Color> colorMapTemperature(Map<Integer, Color> colorMap) {
		colorMap = new HashMap<Integer, Color>();
		colorMap.put(0, new Color(255, 255, 255)); // ground
		colorMap.put(1, new Color(21,124,176));//blue - cold
		colorMap.put(2, new Color(62,146,146));
		colorMap.put(3, new Color(104,171,113));
		colorMap.put(4, new Color(146,194,82));
		colorMap.put(5, new Color(190,218,49));
		colorMap.put(6, new Color(234,243,16));
		colorMap.put(7, new Color(255,235,0));
		colorMap.put(8, new Color(255,191,0));
		colorMap.put(9, new Color(255,150,0));
		colorMap.put(10, new Color(255,103,0));
		colorMap.put(11, new Color(255,66,0));
		colorMap.put(12, new Color(255,19,0));// red - hot
		// colorMap.put(BLACK_MAP_COLOR, new Color(0, 0, 0)); // TODO JLF 05.2026 has to be corrected (black map is 10 already used
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
