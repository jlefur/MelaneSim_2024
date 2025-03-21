package presentation.display;
import javax.swing.JLabel;
import data.constants.I_ConstantPNMC_particules;
import presentation.epiphyte.C_InspectorPopulationMarine;
import thing.ground.landscape.C_LandscapeMarine;

/** Plankton
 * @author JLF 2024 */
public class C_UserPanelMarine extends C_UserPanel implements I_ConstantPNMC_particules {
	//
	// FIELDS
	//
	protected static final long serialVersionUID = 1L;
	private C_Meter meterPlankton, meterEnergy;
	//
	// CONSTRUCTOR
	//
	public C_UserPanelMarine() {
		super();
	}
	//
	// OVERRIDEN METHODS
	//
	@Override
	/** Initialise les composants du tableau de bord */
	protected void initTop() {
		super.initTop();
		// C. BOXES METERS - 1.BOX POPULATION
		this.meterPlankton = new C_Meter("Plancton (X1E3)", true, 1000);
		this.metersPopulation.add(this.meterPlankton.getPan());
		this.meterEnergy = new C_Meter("Energy (X1E3)", true, 1000);
		this.metersPopulation.add(this.meterEnergy.getPan());
	}
	@Override
	/** Met à jour les données des compteurs */
	protected void update_Meters() {
		super.update_Meters();
		this.meterPlankton.setData(C_InspectorPopulationMarine.planktonList.size());
		this.meterEnergy.setData(C_LandscapeMarine.overallEnergy_Ukcal);
		//A_Protocol.event("C_UserPanelMarine.update_Meters()",C_InspectorPopulationMarine.planktonList.size()+" /// "+C_InspectorPopulationMarine.planktonExport,false);
		C_InspectorPopulationMarine.planktonExport = 0;
	}
	@Override
	/** Gestion de l'image centrale overriden selon rodent ou marine (JLF - 2011, 2018, 2024) */
	protected JLabel createTitleBlock(String fileName) {
		fileName = "icons/titlePlancton.gif";
		return super.createTitleBlock(fileName);
	}
	@Override
	public String toString() {
		return "MelaneSim user panel";
	}
}
