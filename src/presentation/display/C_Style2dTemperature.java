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
		colorMap.put(0, new Color(255, 255, 255)); // terre
		colorMap.put(1, new Color(5, 48, 97));     // 18-20°C bleu très foncé
		colorMap.put(2, new Color(33, 113, 181));  // 20-22°C bleu
		colorMap.put(3, new Color(107, 174, 214)); // 22-24°C bleu clair
		colorMap.put(4, new Color(161, 217, 155)); // 24-26°C vert clair
		colorMap.put(5, new Color(253, 219, 57));  // 26-28°C jaune
		colorMap.put(6, new Color(240, 134, 30));  // 28-29°C orange
		colorMap.put(7, new Color(215, 25, 28));   // >29°C rouge
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
