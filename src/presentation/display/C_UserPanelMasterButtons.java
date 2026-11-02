package presentation.display;

import javax.swing.*;
import java.awt.*;

public class C_UserPanelMasterButtons extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String CARD_MARINE = "MARINE";
	private static final String CARD_ENERGY = "ENERGY";
	private static final String CARD_MIAGE_TEST = "MIAGE_TEST";

	public C_UserPanelMasterButtons() {
		super(new BorderLayout(6, 6));

		// --- tes deux sous-panneaux existants ---
		C_UserPanelMarine marine = new C_UserPanelMarine();
		C_UserPanelEnergyLinearGrid energy = new C_UserPanelEnergyLinearGrid(); // ou EnergyLog si tu préfères
		C_UserPanelEnergyLinearGrid miageTest = new C_UserPanelEnergyLinearGrid(true); // sliders horizontaux

		// --- zone centrale à cartes ---
		JPanel center = new JPanel(new CardLayout());
		center.add(marine, CARD_MARINE);
		center.add(energy, CARD_ENERGY);
		center.add(miageTest, CARD_MIAGE_TEST);

		// --- barre de boutons en haut ---
		JToggleButton btnMarine = new JToggleButton("Tableau de bord");
		JToggleButton btnEnergy = new JToggleButton("Marine Energy Control");
		JToggleButton btnMiageTest = new JToggleButton("MIAGE - TEST");
		ButtonGroup group = new ButtonGroup();
		group.add(btnMarine);
		group.add(btnEnergy);
		group.add(btnMiageTest);
		btnMarine.setSelected(true);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		top.add(new JLabel("Vue :"));
		top.add(btnMarine);
		top.add(btnEnergy);
		top.add(btnMiageTest);

		// --- actions : bascule de carte ---
		CardLayout cards = (CardLayout) center.getLayout();
		btnMarine.addActionListener(e -> cards.show(center, CARD_MARINE));
		btnEnergy.addActionListener(e -> cards.show(center, CARD_ENERGY));
		btnMiageTest.addActionListener(e -> cards.show(center, CARD_MIAGE_TEST));

		// (option) raccourcis clavier
		btnMarine.setMnemonic('M');
		btnEnergy.setMnemonic('E');
		btnMiageTest.setMnemonic('T');

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
	}
}
