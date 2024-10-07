import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MaFenetrePrincipale extends JFrame {
    
    public MaFenetrePrincipale() {
        setTitle("Fenêtre Principale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Créer un bouton
        JButton bouton = new JButton("Ouvrir une nouvelle fenêtre");
        
        // Ajouter un écouteur d'événements au bouton
        bouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appeler une méthode pour ouvrir une nouvelle fenêtre
                ouvrirNouvelleFenetre();
            }
        });
        
        // Ajouter le bouton à la fenêtre principale
        getContentPane().add(bouton, BorderLayout.CENTER);
        
        // Ajuster la taille de la fenêtre
        pack();
        
        // Centrer la fenêtre sur l'écran
        setLocationRelativeTo(null);
    }
    
    private void ouvrirNouvelleFenetre() {
        // Créer une nouvelle fenêtre
        JFrame nouvelleFenetre = new JFrame("Nouvelle Fenêtre");
        
        // Ajouter un libellé à la nouvelle fenêtre
        JLabel label = new JLabel("Bienvenue dans la nouvelle fenêtre!");
        nouvelleFenetre.getContentPane().add(label);
        
        // Ajuster la taille de la nouvelle fenêtre
        nouvelleFenetre.setSize(300, 200);
        
        // Centrer la nouvelle fenêtre par rapport à la fenêtre principale
        nouvelleFenetre.setLocationRelativeTo(this);
        
        // Rendre la nouvelle fenêtre visible
        nouvelleFenetre.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Créer et afficher la fenêtre principale
                new MaFenetrePrincipale().setVisible(true);
            }
        });
    }
}
