package presentation.display;

import thing.I_SituatedThing;
import thing.ground.C_LandPlot;
import thing.ground.I_Container;
import thing.ground.landscape.C_Landscape;

import org.locationtech.jts.geom.Coordinate;
import repast.simphony.context.Context;

/** Provides a display-able object (background facility map) to the GUI<br>
 * The bitmap is declared in presentation.display.C_Background.style_0.xml or equivalent
 * @author J.Le Fur, 08.2014, rev. 04.2018, 03.2025 */
public class C_Background implements I_SituatedThing {
    //
    // FIELDS
    //
    public boolean hasToSwitchFace;
    protected double scale;
    protected Coordinate coordinate_Umeter;
    public double whereX, whereY;
    //
    // CONSTRUCTOR
    //
    public C_Background(double scale, double x_position_Ucs, double y_position_Ucs) {
        this.hasToSwitchFace = true;
        this.whereX = x_position_Ucs;
        this.whereY = y_position_Ucs;
        this.scale = scale;
    }
    //
    // METHODS
    //
    public void contextualize(Context<Object> context, C_Landscape landscape) {
        if (!context.contains(this)) {
            context.add(this);
            landscape.getContinuousSpace().moveTo(this, this.whereX, this.whereY);
        }
    }
    //
    // GETTER
    //
    public double getScale() {// needed by GUI
    	return this.scale;
    }
    //
    // OVERRIDEN METHODS (Useless, for compatibility with I_SituatedThing and I_LivingThing
    //
    @Override
    /** Useless, for compatibility with I_SituatedThing */
    public void actionInfect(I_SituatedThing thing) {}
    
    @Override
    public void discardThis() {}
    @Override
    /** Needed & unused to conform to @see I_SituatedThing / JLF 02.2020 */
    public void assertColNo(int colNo) {}
    @Override
    /** Needed & unused to conform to @see I_SituatedThing / JLF 02.2020 */
    public void assertLineNo(int lineNo) {}
    @Override
    public void setCurrentSoilCell(I_Container cell) {}

    @Override
    public int retrieveColNo() {
        return (int) this.whereY;
    }
    @Override
    public int retrieveLineNo() {
        return (int) this.whereX;
    }
    @Override
    public boolean isInfected() {
        return false;
    }
    @Override
    public Coordinate getCoordinate_Umeter() {
        return null;
    }
    @Override
    public Coordinate getCoordinate_Ucs() {
        return null;
    }
    @Override
    public I_Container getCurrentSoilCell() {
        return null;
    }
    @Override
    /** Used for compatibility with I_SituatedThing @rev JLF 10.2019 */
    public double getDistance_Umeter(Coordinate oneCoordinate) {
        return 0;
    }
    @Override
    /** Used for compatibility with I_SituatedThing @rev JLF 10.2019 */
    public double getDistance_Umeter(I_SituatedThing agent) {
    	return 0;
    }
	@Override
	public boolean canInteractWith(I_SituatedThing oneOccupant) {
		return false;
	}
	@Override
	/** Used for compatibility with I_SituatedThing @rev JLF 10.2019 */
	public C_LandPlot getMyLandPlot() {
		return null;
	}
	@Override
    /** Used for compatibility with I_LivingThing @rev JLF 03.2025 */
	public String retrieveMyName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	/** Used for compatibility with I_LivingThing @rev JLF 03.2025 */
	public double getBirthDate_Utick() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	/** Used for compatibility with I_LivingThing @rev JLF 03.2025 */
	public double getAge_Uday() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	/** Used for compatibility with I_LivingThing @rev JLF 03.2025 */
	public void actionGrowOlder_Utick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	/** Used for compatibility with I_LivingThing @rev JLF 03.2025 */
	public double getEnergy_Ukcal() {
		// TODO Auto-generated method stub
		return 0;
	}
}
