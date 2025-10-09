
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimpleGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton redButton;
    private JButton greenButton;

    public SimpleGUI() {
        // Configuration de la fen�tre
        setTitle("Simple GUI");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialisation des boutons
        redButton = new JButton("Rouge");
        greenButton = new JButton("Vert");

        // Ajout des �couteurs d'�v�nements aux boutons
        redButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action � effectuer lors du clic sur le bouton rouge
                JOptionPane.showMessageDialog(SimpleGUI.this, "Bouton Rouge cliqu�", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        greenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action � effectuer lors du clic sur le bouton vert
                JOptionPane.showMessageDialog(SimpleGUI.this, "Bouton Vert cliqu�", "Message", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Configuration du layout de la fen�tre
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