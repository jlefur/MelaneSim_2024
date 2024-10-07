import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceGraphique {

    public static void main(String[] args) {
        // Créer la fenêtre principale
        JFrame frame = new JFrame("Interface Graphique");
        frame.setSize(300, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Créer un panneau pour contenir les composants
        JPanel panel = new JPanel();
        frame.add(panel);

        // Créer un bouton rouge
        JButton boutonRouge = new JButton("Rouge");
        boutonRouge.setBackground(Color.RED);
        boutonRouge.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setBackground(Color.RED);
            }
        });

        // Créer un bouton vert
        JButton boutonVert = new JButton("Vert");
        boutonVert.setBackground(Color.GREEN);
        boutonVert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.setBackground(Color.GREEN);
            }
        });

        // Ajouter les boutons au panneau
        panel.add(boutonRouge);
        panel.add(boutonVert);

        // Afficher la fenêtre
        frame.setVisible(true);
    }
}
