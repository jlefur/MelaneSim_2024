package presentation.display;

import javax.swing.*;
import java.awt.*;

public class C_UserPanelMasterTabs extends JPanel {

	private final CardLayout cards = new CardLayout();

	public C_UserPanelMasterTabs() {
		super(new BorderLayout(6, 6));

		// Sélecteur en haut
		String MARINE = "Tableau de bord";
		String ENERGY = "Marine Energy Control";
		JComboBox<String> combo = new JComboBox<>(new String[] { MARINE, ENERGY });
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
		top.add(new JLabel("User Panel:"));
		top.add(combo);
		add(top, BorderLayout.NORTH);

		// Zone centrale à cartes
		JPanel center = new JPanel(cards);
		C_UserPanelMarine marine = new C_UserPanelMarine(); // ton panel existant
		C_UserPanelEnergyIO energy = new C_UserPanelEnergyIO(); // ton nouveau panel
		JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.addTab("Tableau de bord", marine);
		tabs.addTab("Marine Energy Control", energy);
		add(tabs, BorderLayout.CENTER);
		center.add(marine, MARINE);
		center.add(energy, ENERGY);
		add(center, BorderLayout.CENTER);

		combo.addActionListener(e -> cards.show(center, (String) combo.getSelectedItem()));

		// option: démarrer sur “Tableau de bord”
		cards.show(center, MARINE);
		System.out.println("MasterTabs chargé !");

	}
}
