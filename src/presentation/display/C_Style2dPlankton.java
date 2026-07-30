package presentation.display;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import data.constants.I_ConstantNumeric;
import data.constants.I_ConstantPNMC;
import repast.simphony.valueLayer.ValueLayer;
import repast.simphony.visualizationOGL2D.ValueLayerStyleOGL;
import thing.ground.landscape.C_Landscape;

/** Color style for the plankton value layer (11 categories, green gradient)
 * @author JLF-2026 */
public class C_Style2dPlankton implements ValueLayerStyleOGL, I_ConstantNumeric, I_ConstantPNMC {
	//
	// FIELDS
	//
	protected ValueLayer layer; // the layer to represent
	Map<Integer, Color> colorMap;// Used to associate a color to each value of the layer
	//
	// CONSTRUCTOR
	//
	public C_Style2dPlankton() {
		this.colorMap = new HashMap<Integer, Color>();
		this.colorMap = C_Landscape.getColormap();
		if(this.colorMap==null){
			System.out.print("C_Style2dPlankton(); creating ");
			this.colorMap = new HashMap<Integer,Color>();
			this.colorMap = colorMapPlankton(this.colorMap);
			System.out.println("PNMC plankton colormap: "+this.colorMap.size()+" colors identified");
		}
	}
	//
	// METHODS
	//
	@Override
	public void init(ValueLayer layer) { this.layer = layer; }

	public Map<Integer,Color> colorMapPlankton(Map<Integer,Color> colorMap) {
		colorMap = new HashMap<Integer,Color>();
		colorMap.put(0,new Color(193,254,0));
		colorMap.put(1,new Color(168,255,2));
		colorMap.put(2,new Color(127,247,0));
		colorMap.put(3,new Color(87,238,2));
		colorMap.put(4,new Color(52,227,2));
		colorMap.put(5,new Color(3,217,11));
		colorMap.put(6,new Color(0,204,31));
		colorMap.put(7,new Color(0,184,49));
		colorMap.put(8,new Color(6,168,59));
		colorMap.put(9,new Color(1,160,60));
		colorMap.put(10,new Color(1,87,34));
		colorMap.put(TERRESTRIAL_MIN_AFFINITY,new Color(58,124,76));// terre
		colorMap.put(BLACK_MAP_COLOR,new Color(0,0,0));
		colorMap.put(11,Color.red);// spot most occupied cells, @see A_Protocol_PNMC#haltSimulation()
		return colorMap;
	}
	//
	// GETTERS
	//
	@Override
	public Color getColor(double...coordinates) { return this.colorMap.get((int)this.layer.get(coordinates)); }

	@Override
	public float getCellSize() { return cellSize.get(0); }
}
