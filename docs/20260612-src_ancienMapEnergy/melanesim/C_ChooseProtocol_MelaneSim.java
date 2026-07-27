package melanesim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.constants.I_ConstantPNMC;

/** Display buttons to select one of SimMasto protocols, then split xml file to set the protocol<br>
 * Mboup, 2014, rev. Le Fur, 2018, 2024<br>
 * <br>
 * 1.-PNMC_drifters: particles moved by surface currents<br>
 * 2.-PNMC_plancton: particles moved by surface currents<br>
 */
public class C_ChooseProtocol_MelaneSim extends C_ChooseProtocol_Rodents implements ActionListener, I_ConstantPNMC {
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private JPanel pnmc_planktonBox = new JPanel();
	private JPanel pnmc_driftersBox = new JPanel();
	private JPanel pnmc_nektonBox = new JPanel();
	private JPanel pnmc_shipBox = new JPanel();

	public static void main(String[] args) {
		new C_ChooseProtocol_MelaneSim();
	}
	public C_ChooseProtocol_MelaneSim() {
		this.setTitle("- Choose one simulation protocol (MelaneSim project - IRD/Entropie)");
		this.frameInit();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // imp�ratif cette ligne
		this.setLocation(560, 10);
		this.setContentPane(panel);
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));

		pnmc_driftersBox.setLayout(new BoxLayout(pnmc_driftersBox, BoxLayout.X_AXIS));
		pnmc_driftersBox.setBorder(BorderFactory.createTitledBorder("Inert particles drifted by currents"));
		this.pnmc_driftersBox.add(addImageButton("icons/titleDrifters.gif", "PNMC1-Drifters"));
		this.panel.add(pnmc_driftersBox);

		pnmc_planktonBox.setLayout(new BoxLayout(pnmc_planktonBox, BoxLayout.X_AXIS));
		pnmc_planktonBox.setBorder(BorderFactory.createTitledBorder(
				"Chlorophyll loaded particles drifted by currents"));
		this.pnmc_planktonBox.add(addImageButton("icons/titlePlancton.gif", "PNMC2-Plankton"));
		this.panel.add(pnmc_planktonBox);

		pnmc_nektonBox.setLayout(new BoxLayout(pnmc_nektonBox, BoxLayout.X_AXIS));
		pnmc_nektonBox.setBorder(BorderFactory.createTitledBorder("Currents, chlorophyll and microNekton stages"));
		this.pnmc_nektonBox.add(addImageButton("icons/titleNecton.gif", "PNMC3-Nekton"));
		this.panel.add(pnmc_nektonBox);
		
		pnmc_shipBox.setLayout(new BoxLayout(pnmc_shipBox, BoxLayout.X_AXIS));
		pnmc_shipBox.setBorder(BorderFactory.createTitledBorder("ships activities within the domain"));
		this.pnmc_shipBox.add(addImageButton("icons/titleShips.gif", "PNMC4-ships"));
		this.panel.add(pnmc_shipBox);

		this.pack();
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String xmlFileName = "parameters_scenario_" + e.getActionCommand() + ".txt";
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(e.getActionCommand() + " Protocol choosed");
		System.exit(0);
	}
}