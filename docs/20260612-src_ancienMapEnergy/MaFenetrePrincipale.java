import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MaFenetrePrincipale extends JFrame {
    

	private static final long serialVersionUID = 1L;

	public MaFenetrePrincipale() {
        setTitle("Fen�tre Principale");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Cr�er un bouton
        JButton bouton = new JButton("Ouvrir une nouvelle fen�tre");
        
        // Ajouter un �couteur d'�v�nements au bouton
        bouton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Appeler une m�thode pour ouvrir une nouvelle fen�tre
                ouvrirNouvelleFenetre();
            }
        });
        
        // Ajouter le bouton � la fen�tre principale
        getContentPane().add(bouton, BorderLayout.CENTER);
        
        // Ajuster la taille de la fen�tre
        pack();
        
        // Centrer la fen�tre sur l'�cran
        setLocationRelativeTo(null);
    }
    
    private void ouvrirNouvelleFenetre() {
        // Cr�er une nouvelle fen�tre
        JFrame nouvelleFenetre = new JFrame("Nouvelle Fen�tre");
        
        // Ajouter un libell� � la nouvelle fen�tre
        JLabel label = new JLabel("Bienvenue dans la nouvelle fen�tre!");
        nouvelleFenetre.getContentPane().add(label);
        
        // Ajuster la taille de la nouvelle fen�tre
        nouvelleFenetre.setSize(300, 200);
        
        // Centrer la nouvelle fen�tre par rapport � la fen�tre principale
        nouvelleFenetre.setLocationRelativeTo(this);
        
        // Rendre la nouvelle fen�tre visible
        nouvelleFenetre.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Cr�er et afficher la fen�tre principale
                new MaFenetrePrincipale().setVisible(true);
            }
        });
    }
}
