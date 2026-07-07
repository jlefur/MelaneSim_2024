package presentation.display;

import javax.swing.*;
import java.awt.*;

public class TestEnergyColumn {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      JFrame frame = new JFrame("Test EnergyColumn");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      // panneau avec 3 curseurs verticaux
      JPanel grid = new JPanel(new GridLayout(1, 3, 12, 0));
      grid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      grid.add(new EnergyColumn("Chlorophyll", 1.0, 0.25, 2.0,
              val -> System.out.println("Chlorophyll → " + val)));

      grid.add(new EnergyColumn("Micronekton", 1.0, 0.25, 2.0,
              val -> System.out.println("Micronekton → " + val)));

      grid.add(new EnergyColumn("Whale", 1.0, 0.25, 2.0,
              val -> System.out.println("Whale → " + val)));

      frame.add(grid, BorderLayout.CENTER);
      frame.setSize(600, 400);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
    });
  }
}
