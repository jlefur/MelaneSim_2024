package melanesim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import data.constants.I_ConstantPNMC;
import repast.simphony.runtime.RepastMain;

/** Display buttons to select one of SimMasto protocols, then split xml file to set the protocol<br>
 * Mboup, 2014, rev. Le Fur, 2018, 2024<br>
 * <br>
 * 1.-PNMC_drifters: particles moved by surface currents<br>
 * 2.-PNMC_plancton: particles moved by surface currents<br>
 */
public class C_ChooseProtocol_MelaneSim_full extends C_ChooseProtocol_Rodents implements ActionListener,
        I_ConstantPNMC {
	private static final Path REPAST_PLUGINS_DIRECTORY = Paths.get("C:/RepastSimphony-2.11.0/eclipse/plugins");
	private static final long serialVersionUID = 1L;
	private JPanel panel = new JPanel();
	private JPanel pnmc_planktonBox = new JPanel();
	private JPanel pnmc_driftersBox = new JPanel();
	private JPanel pnmc_nektonBox = new JPanel();
	private JPanel pnmc_temperatureBox = new JPanel();
	private JPanel pnmc_shipBox = new JPanel();

	public static void main(String[] args) { new C_ChooseProtocol_MelaneSim_full(); }
	public C_ChooseProtocol_MelaneSim_full() {
		this.setTitle("- Choose one simulation protocol (MelaneSim project - IRD/Entropie)");
		this.frameInit();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // imp�ratif cette ligne
		this.setLocation(560,10);
		this.setContentPane(panel);
		this.panel.setLayout(new BoxLayout(this.panel,BoxLayout.PAGE_AXIS));

		pnmc_driftersBox.setLayout(new BoxLayout(pnmc_driftersBox,BoxLayout.X_AXIS));
		pnmc_driftersBox.setBorder(BorderFactory.createTitledBorder("Inert particles drifted by currents"));
		this.pnmc_driftersBox.add(addImageButton("icons/titleDrifters.gif","PNMC1-drifters"));
		this.panel.add(pnmc_driftersBox);

		pnmc_planktonBox.setLayout(new BoxLayout(pnmc_planktonBox,BoxLayout.X_AXIS));
		pnmc_planktonBox.setBorder(BorderFactory.createTitledBorder(
		        "Chlorophyll loaded particles drifted by currents"));
		this.pnmc_planktonBox.add(addImageButton("icons/titlePlancton.gif","PNMC2-plankton"));
		this.panel.add(pnmc_planktonBox);

		pnmc_nektonBox.setLayout(new BoxLayout(pnmc_nektonBox,BoxLayout.X_AXIS));
		pnmc_nektonBox.setBorder(BorderFactory.createTitledBorder("Currents, chlorophyll and microNekton stages"));
		this.pnmc_nektonBox.add(addImageButton("icons/titleNecton.gif","PNMC3-nekton"));
		this.panel.add(pnmc_nektonBox);

		pnmc_shipBox.setLayout(new BoxLayout(pnmc_shipBox,BoxLayout.X_AXIS));
		pnmc_shipBox.setBorder(BorderFactory.createTitledBorder("ships activities within the domain"));
		this.pnmc_shipBox.add(addImageButton("icons/titleShips.gif","PNMC4-ships"));
		this.panel.add(pnmc_shipBox);

		pnmc_temperatureBox.setLayout(new BoxLayout(pnmc_temperatureBox,BoxLayout.X_AXIS));
		pnmc_temperatureBox.setBorder(BorderFactory.createTitledBorder("+Sea surface temperature"));
		this.pnmc_temperatureBox.add(addImageButton("icons/titleTemperature.gif","PNMC5-temperature"));
		this.panel.add(pnmc_temperatureBox);

		this.pack();
		this.setVisible(true);
	}
	// @Override
	public void actionPerformed0(ActionEvent e) {
		String xmlFileName = "parameters_scenario_"+e.getActionCommand()+".txt";
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(e.getActionCommand()+" Protocol choosed");
		System.exit(0);
	}
	// @Override
	public void actionPerformed1(ActionEvent e) {
		String protocol = e.getActionCommand();
		String xmlFileName = "parameters_scenario_"+protocol+".txt";
		// 1. Création / mise à jour des fichiers XML
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(protocol+" protocol chosen");
		this.dispose();// 2. Fermer uniquement la fenêtre de sélection
		// 3. Chemin vers le scénario Repast
		Path scenarioPath = Paths.get("MelaneSim.rs").toAbsolutePath().normalize();
		System.out.println("Starting MelaneSim scenario: "+scenarioPath);
		// 4. Lancement du simulateur
		Thread repastThread = new Thread(()->{
			RepastMain.main(new String[]{scenarioPath.toString()});
		},"Repast-MelaneSim");
		repastThread.start();
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String protocol = e.getActionCommand();
		String xmlFileName = "parameters_scenario_"+protocol+".txt";
		splitRepastXmlConfigFiles(xmlFileName);
		System.out.println(protocol+" protocol chosen");
		this.dispose();
		launchMelaneSim();
	}

	private void launchMelaneSim() {

		Path projectDirectory = Paths.get("").toAbsolutePath().normalize();
		Path scenarioPath = projectDirectory.resolve("MelaneSim.rs").normalize();
		Path javaExecutable = REPAST_PLUGINS_DIRECTORY.resolve(Paths.get(
		        "org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.10.v20240120-1143","jre","bin",
		        "javaw.exe"));
		Path groovyDirectory = REPAST_PLUGINS_DIRECTORY.resolve(Paths.get("org.codehaus.groovy_3.0.21.v202403311524-e2403",
		        "lib"));
		Path runtimeDirectory = REPAST_PLUGINS_DIRECTORY.resolve("repast.simphony.runtime_2.11.0");
		List<String> classPathEntries = List.of(groovyDirectory.resolve("groovy-3.0.21-indy.jar").toString(),
		        groovyDirectory.resolve("groovy-test-3.0.21-indy.jar").toString(),groovyDirectory.resolve(
		                "ivy-2.5.2.jar").toString(),
		        // Élément essentiel pour l'initialisation de Repast
		        runtimeDirectory.resolve("bin").toString(),runtimeDirectory.resolve("lib/saf.core.runtime.jar")
		                .toString(),runtimeDirectory.resolve("lib/commons-logging-1.1.2.jar").toString(),
		        runtimeDirectory.resolve("lib/javassist-3.17.1-GA.jar").toString(),runtimeDirectory.resolve(
		                "lib/jpf.jar").toString(),runtimeDirectory.resolve("lib/jpf-boot.jar").toString(),
		        runtimeDirectory.resolve("lib/log4j-1.2-api-2.17.2.jar").toString(),runtimeDirectory.resolve(
		                "lib/log4j-api-2.17.2.jar").toString(),runtimeDirectory.resolve("lib/log4j-core-2.17.2.jar")
		                        .toString(),runtimeDirectory.resolve("lib/xpp3_min-1.1.4c.jar").toString(),
		        runtimeDirectory.resolve("lib/xstream-1.4.19.jar").toString(),runtimeDirectory.resolve(
		                "lib/xmlpull-1.1.3.1.jar").toString(),runtimeDirectory.resolve("lib/commons-cli-1.3.1.jar")
		                        .toString());
		String classPath = String.join(File.pathSeparator,classPathEntries);
		List<String> command = new ArrayList<>();
		command.add(javaExecutable.toString());
		command.add("-XX:+IgnoreUnrecognizedVMOptions");
		command.add("--add-opens");
		command.add("java.base/java.lang.reflect=ALL-UNNAMED");
		command.add("--add-modules=ALL-SYSTEM");
		command.add("--add-exports=java.base/jdk.internal.ref=ALL-UNNAMED");
		command.add("--add-exports=java.base/java.lang=ALL-UNNAMED");
		command.add("--add-exports");
		command.add("java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED");
		command.add("--add-exports");
		command.add("java.xml/com.sun.org.apache.xpath.internal.objects=ALL-UNNAMED");
		command.add("--add-exports=java.desktop/sun.awt=ALL-UNNAMED");
		command.add("--add-exports=java.desktop/sun.java2d=ALL-UNNAMED");
		command.add("--add-opens");
		command.add("java.base/java.lang=ALL-UNNAMED");
		command.add("--add-opens");
		command.add("java.base/java.util=ALL-UNNAMED");
		command.add("-Dfile.encoding=UTF-8");
		command.add("-Dstdout.encoding=UTF-8");
		command.add("-Dstderr.encoding=UTF-8");
		command.add("-classpath");
		command.add(classPath);
		command.add("-XX:+ShowCodeDetailsInExceptionMessages");
		command.add("repast.simphony.runtime.RepastMain");
		command.add(scenarioPath.toString());

		System.out.println("Starting MelaneSim scenario: "+scenarioPath);
		try{
			verifyLaunchFiles(javaExecutable,scenarioPath,classPathEntries);
			ProcessBuilder builder = new ProcessBuilder(command);
			builder.directory(projectDirectory.toFile());

			// Affiche les sorties de Repast dans la console Eclipse
			builder.inheritIO();
			Process process = builder.start();
			System.out.println("MelaneSim launched, PID = "+process.pid());
			dispose();
			System.exit(0);
		}catch(IOException ex){
			System.err.println("Unable to launch MelaneSim.");
			ex.printStackTrace();
		}
	}

	private void verifyLaunchFiles(Path javaExecutable, Path scenarioPath, List<String> classPathEntries)
	        throws IOException {
		if(!Files.isRegularFile(javaExecutable)){ throw new IOException("Java executable not found: "+javaExecutable); }
		if(!Files.isDirectory(scenarioPath)){
			throw new IOException("Repast scenario directory not found: "+scenarioPath);
		}
		for(String entry:classPathEntries){
			Path path = Paths.get(entry);
			if(!Files.exists(path)){ throw new IOException("Repast classpath entry not found: "+path); }
		}
	}
}