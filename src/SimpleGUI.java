
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleGUI extends JFrame {
    private JButton redButton;
    private JButton greenButton;

    public SimpleGUI() {
        // Configuration de la fenêtre
        setTitle("Simple GUI");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation des boutons
        redButton = new JButton("Rouge");
        greenButton = new JButton("Vert");

        // Ajout des écouteurs d'événements aux boutons
        redButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action à effectuer lors du clic sur le bouton rouge
                JOptionPane.showMessageDialog(SimpleGUI.this, "Bouton Rouge cliqué", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        greenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action à effectuer lors du clic sur le bouton vert
                JOptionPane.showMessageDialog(SimpleGUI.this, "Bouton Vert cliqué", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Configuration du layout de la fenêtre
        setLayout(new FlowLayout());
        add(redButton);
        add(greenButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SimpleGUI simpleGUI = new SimpleGUI();
                simpleGUI.setVisible(true);
            }
        });
    }
}