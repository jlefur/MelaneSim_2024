package melanesim;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.C_ReadWriteFile;
import data.constants.I_ConstantPNMC;
import presentation.dataOutput.C_FileWriter;

/** Display buttons to select one of SimMasto protocols, then split xml file to set the protocol<br>
 * Mboup, 2014, rev. Le Fur, 2018, 2024<br>
 * <br>
 * 1.-PNMC_plancton: particles moved by surface currents<br>
 */
public class C_ChooseProtocol_MelaneSim extends JFrame implements ActionListener, I_ConstantPNMC {
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private BufferedImage buttonIcon = null;
	private JPanel pnmc_plancton = new JPanel();

	public static void main(String[] args) {
		new C_ChooseProtocol_MelaneSim();
	}
	public C_ChooseProtocol_MelaneSim() {
		super("- Choose one simulation protocol (MelaneSim project - IRD/Entropie)");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // imp�ratif cette ligne
		this.setLocation(560, 10);
		this.setContentPane(panel);
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));

		pnmc_plancton.setLayout(new BoxLayout(pnmc_plancton, BoxLayout.X_AXIS));
		pnmc_plancton.setBorder(BorderFactory.createTitledBorder("plankton moved by currents"));
		this.pnmc_plancton.add(addImageButton("icons/titlePlancton.gif", "PNMC1"));
		this.pnmc_plancton.add(addImageButton("icons/titleEmpty.gif", ""));
		this.panel.add(pnmc_plancton);

		this.pack();
		this.setVisible(true);
	}
	private JButton addImageButton(String iconFilename, String protocolName) {
		try {
			buttonIcon = ImageIO.read(new File(iconFilename));
		} catch (IOException e) {}
		JButton protocolButton = new JButton();
		protocolButton.addActionListener(this);
		protocolButton.setIcon(new ImageIcon(buttonIcon));
		protocolButton.setToolTipText(protocolName);
		protocolButton.setActionCommand(protocolName);
		protocolButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		return protocolButton;

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String xmlFileName = "parameters_scenario_" + e.getActionCommand() + ".txt";
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(e.getActionCommand() + " Protocol choosed");
		// String[] args = new String[]{"C:\\Users\\sallmous\\Documents\\Workspace_Moussa\\SIMmasto_0\\SIMmasto_0.rs"};
		// repast.simphony.runtime.RepastMain.main(args);
		System.exit(0);
	}
	/** Build parameters.xml and scenario.xml files from the merged file : parameter_scenario_protocolName.xml file The merged
	 * file must be in SIMmasto_0.rs/, and files built will be in the same folder.
	 * @param xmlFileName */
	public void splitRepastXmlConfigFiles(String xmlFileName) {
		BufferedReader buffer = C_ReadWriteFile.openBufferReader(REPAST_PATH, xmlFileName);
		C_FileWriter writingXmlFile = null;
		String readLine;
		try {
			// Write the next lines in a matrix before going on with the raterFile
			readLine = buffer.readLine();
			while (readLine != null) {
				if (readLine.contains("<?xml") && !readLine.trim().startsWith("<!--")) {
					String fistReadLine = readLine; // on sauvegarde la premi�re ligne
					readLine = buffer.readLine(); // on passe � la deuxi�me pr r�cup�rer le nom
					// R�cup�ration du nom. example: parameters.xml ou scenario.xml d'une ligne comme
					// <!--fileName:parameters.xml-->
					// ou
					// <!--fileName:scenario.xml-->
					String xmlConfigFileName = readLine.replace(" ", "").split(":")[1].split(".xml")[0] + ".xml";
					// Cr�ation du fichier .xml en construction
					writingXmlFile = new C_FileWriter(REPAST_PATH + xmlConfigFileName, false);
					// Ecriture de la premi�re ligne (<?xml version="1.0" encoding="UTF-8" ?>)
					writingXmlFile.writeln(fistReadLine);
				} // Ecriture du reste du fichier
				writingXmlFile.writeln(readLine);
				readLine = buffer.readLine();
			}
		} catch (Exception e) {
			System.err.println(
					"C_ChooseProtocol_MelaneSim.splitRepastXmlConfigFiles : Error  reading or writing xml file " + e
							.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				buffer.close();
				writingXmlFile.closeFile();
			} catch (Exception e) {
				System.err.println(
						"C_ChooseProtocol_MelaneSim.splitRepastXmlConfigFiles : buffer or writingXmlFile closing error"
								+ e.getMessage());
			}
		}
	}
}