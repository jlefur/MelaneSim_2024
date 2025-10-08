package presentation.display;

	import javax.swing.*;
	import java.nio.file.Path;
	import java.nio.file.Paths;

	public class C_TestEnergyScaleDialog {

	  public static void main(String[] args) {
	    // Lancer sur le thread Swing pour éviter tout souci d'UI
	    SwingUtilities.invokeLater(() -> {
	      Path csv = Paths.get("test_energy_scales.csv");

	      // Valeurs initiales de test (tu peux changer)
	      double initPlankton = 1.0;
	      double initMicronekton = 1.0;
	      double initWhale = 1.0;

	      // Création et affichage du dialog
	      C_EnergyScaleDialog dlg = new C_EnergyScaleDialog(
	          null, initPlankton, initMicronekton, initWhale);
	      dlg.setVisible(true);

	      System.out.println("Fenêtre fermée.");
	      System.out.println("Regarde ton fichier : " + csv.toAbsolutePath());
	    });
	  }
	}
