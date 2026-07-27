
/* This source code is licensed under a BSD licence as detailed in file SIMmasto_0.license.txt */
package melanesim.protocol.rodents;

import java.util.Calendar;

import data.C_Parameters;
import data.constants.rodents.I_ConstantStringRodents;
import data.rodents.C_CircadianAffinitiesMus;
import melanesim.C_ContextCreator;
import presentation.display.C_Background;
import presentation.display.C_CustomPanelSet_Rodent;
import presentation.epiphyte.C_InspectorGenetic;
import repast.simphony.context.Context;
import thing.dna.C_GenomeAmniota;
import thing.dna.species.rodents.C_GenomeMusMusculus;
import thing.ground.C_SoilCell;
import thing.ground.I_Container;
import thing.ground.landscape.C_LandscapeRodent;
import thing.rodents.C_Rodent;
import thing.rodents.C_RodentCircadian;
import thing.rodents.C_TaxiManDodel;

/** author J.Le Fur & M.Diakhate 07.2014, rev. JLF 10.2014, 03.2018 */

public class C_ProtocolDodel extends A_ProtocolRodent implements I_ConstantStringRodents {
	//
	// FIELDS
	//
	private C_CircadianAffinitiesMus circadianAffinities = null;
	protected C_InspectorGenetic geneticInspector;
	private int nbWeekCount;
	private int marketDay;
	private int currentDayOfWeek;
	//
	// CONSTRUCTOR
	//
	/** Declare the inspectors, add them to the inspector list, declares them to the panelInitializer for indicators graphs.<br>
	 * Author J.Le Fur 02.2013 */
	public C_ProtocolDodel(Context<Object> ctxt) {
		super(ctxt);
		this.facilityMap = new C_Background(0.05, 13, 19);// TODO number in source 2018.03 jlf

		this.geneticInspector = new C_InspectorGenetic();
		this.inspectorList.add(geneticInspector);
		C_CustomPanelSet_Rodent.addGeneticInspector(geneticInspector);
		this.circadianAffinities = new C_CircadianAffinitiesMus((C_LandscapeRodent) this.landscape);
	}
	//
	// METHODS
	//
	/** The contact structure (term coined from S.E.Page: Diversity and Complexity) */
	protected void initLandscape(Context<Object> context) {
		this.setLandscape(new C_LandscapeRodent(context, C_Parameters.RASTER_URL, VALUE_LAYER_NAME, CONTINUOUS_SPACE_NAME));
	}
	@Override
	public void readUserParameters() {
		boolean oldValueBlackMap = C_Parameters.BLACK_MAP;
		super.readUserParameters();
		C_Parameters.UNLOAD_FREQUENCY_Uweek = ((Integer) C_Parameters.parameters.getValue("UNLOAD_FREQUENCY_Uweek")).intValue();
		C_Parameters.BLACK_MAP = ((Boolean) C_Parameters.parameters.getValue("BLACK_MAP")).booleanValue();
		if (oldValueBlackMap != C_Parameters.BLACK_MAP) {
			if (C_Parameters.BLACK_MAP) this.blackMap();
			else if (this.landscape != null) this.landscape.resetCellsColor();
		}
	}
	/** Fills the context with simple _wandering_ C_Rodent agents (as opposed to C_RodentFossorial's that dig burrows) <br>
	 * The sex ratio is randomly generated , rev. JLF 07.2014 currently unused */
	public void randomlyAddRodents(int nbAgent) {
		for (int i = 0; i < nbAgent; i++) {
			C_Rodent agent = createRodent();
			agent.setRandomAge();
			contextualizeNewThingInSpace(agent, 7., 22.);// TODO number in source 2018.03 jlf
			agent.setNewRandomMove();
		}
	}
	@Override
	public void initProtocol() {
		if (C_Parameters.DISPLAY_MAP) {
			this.facilityMap.contextualize(this.context, this.landscape);
		}
		this.circadianAffinities.setInitialAffinities();
		this.nbWeekCount = 0;
		this.marketDay = 1;
		this.currentDayOfWeek = protocolCalendar.get(Calendar.DAY_OF_WEEK) - 1;
		// randomlyAddRodents(C_Parameters.INIT_POP_SIZE);// add rodents within already created burrows
		// System.out.println("C_ProtocolRodents(): Population of " + C_Parameters.INIT_POP_SIZE
		// + " rodents created and positioned randomly");
		super.initProtocol();// manage inspectors and files after everything
	}
	@Override
	public C_Rodent createRodent() {
		return new C_RodentCircadian(new C_GenomeMusMusculus());
	}
	@Override
	public void step_Utick() {
		// market day and first tick within the market day
		if (protocolCalendar.get(Calendar.DAY_OF_WEEK) != this.currentDayOfWeek//
				&& protocolCalendar.get(Calendar.DAY_OF_WEEK) == this.marketDay) {
			if (this.nbWeekCount + 1 == C_Parameters.UNLOAD_FREQUENCY_Uweek) {
				C_TaxiManDodel taxi = this.createCarrier();
				C_Rodent agent = createRodent();
				agent.setRandomAge();
				contextualizeNewThingInContainer(agent, taxi.getVehicle());
				agent.setTrappedOnBoard(true);
				this.nbWeekCount = 0;
			}
			else this.nbWeekCount++;
		}
		this.currentDayOfWeek = protocolCalendar.get(Calendar.DAY_OF_WEEK);
		super.step_Utick();// has to come after the other inspectors' step since it records indicators in file
	}
	public C_TaxiManDodel createCarrier() {
		C_TaxiManDodel oneCarrier = new C_TaxiManDodel(new C_GenomeAmniota());
		// Declares a new object in the context and positions it within the raster ground
		I_Container cell;
		if (C_ContextCreator.randomGeneratorForInitialisation.nextDouble() > .5) cell = landscape.getGrid()[0][31];// TODO number in source 2018.03 jlf
		else cell = landscape.getGrid()[0][31];
		contextualizeNewThingInContainer(oneCarrier, cell);
		oneCarrier.ownVehicle(TAXI_EVENT);
		oneCarrier.setTarget(landscape.getGrid()[7][21]);// TODO number in source 2018.03 jlf
		return oneCarrier;
	}
	@Override
	public void manageTimeLandmarks() {
		super.manageTimeLandmarks();
		if (!C_Parameters.BLACK_MAP) {
			circadianAffinities.setAffinity(protocolCalendar.get(Calendar.HOUR_OF_DAY) / 2);
			this.landscape.resetCellsColor();
		}
		// NB: int hourOfDay = protocolCalendar.get(Calendar.HOUR_OF_DAY);
	}
	/** Color the map in black to see the overall distribution of burrows<br>
	 * Author J.Le Fur 10.2014, rev.03.2018 TODO JLF 2014.10 should be in presentation package ? */
	@Override
	protected void blackMap() {
		for (int i = 0; i < this.landscape.getDimension_Ucell().getWidth(); i++)
			for (int j = 0; j < this.landscape.getDimension_Ucell().getHeight(); j++) {
				if (this.landscape.getGrid()[i][j] instanceof C_SoilCell) {// safety measure
					C_SoilCell x = (C_SoilCell) this.landscape.getGrid()[i][j];
					String plotType = x.getMyLandPlot().getPlotType();
					if (plotType.equals("ROAD")) this.landscape.getValueLayer().set(9, i, j);
					else this.landscape.getValueLayer().set(BLACK_MAP_COLOR, i, j);
				}
			}
	}
	
}