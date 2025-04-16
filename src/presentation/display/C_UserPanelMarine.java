package presentation.display;
import javax.swing.JLabel;

import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import presentation.epiphyte.C_InspectorPopulationMarine;

/** Plankton
 * @author JLF 2024 */
public class C_UserPanelMarine extends C_UserPanel implements I_ConstantPNMC {
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
		this.meterEnergy = new C_Meter("max energy (X1E3)", true, 1000);
		this.metersPopulation.add(this.meterEnergy.getPan());
	}
	@Override
	/** Met à jour les données des compteurs */
	protected void update_Meters() {
		super.update_Meters();
		this.meterPlankton.setData(C_InspectorPopulationMarine.planktonList.size());
		this.meterEnergy.setData(C_InspectorPopulationMarine.maxEnergy_Ukcal);
//		this.meterEnergy.setData(C_LandscapeMarine.overallEnergy_Ukcal);
		C_InspectorPopulationMarine.planktonExport = 0;
	}
	@Override
	/** Gestion de l'image centrale (JLF - 2011, 2018, 2025) */
	protected JLabel createTitleBlock(String FileName) {
		String fileName = "";
		switch (C_Parameters.PROTOCOL) {
			case PNMC_DRIFTERS :
				fileName = "icons/titleDrifters.gif";
				break;
			case PNMC_PLANKTON :
				fileName = "icons/titlePlancton.gif";
				break;
		}
		return super.createTitleBlock(fileName);
	}
	@Override
	public String toString() {
		return "MelaneSim user panel";
	}
}
