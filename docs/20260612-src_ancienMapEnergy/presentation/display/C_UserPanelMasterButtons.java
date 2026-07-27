package presentation.display;

import javax.swing.*;
import java.awt.*;

public class C_UserPanelMasterButtons extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String CARD_MARINE = "MARINE";
	private static final String CARD_ENERGY = "ENERGY";

	public C_UserPanelMasterButtons() {
		super(new BorderLayout(6, 6));

		// --- tes deux sous-panneaux existants ---
		C_UserPanelMarine marine = new C_UserPanelMarine();
		C_UserPanelEnergyLinearGrid energy = new C_UserPanelEnergyLinearGrid(); // ou EnergyLog si tu préfères

		// --- zone centrale à cartes ---
		JPanel center = new JPanel(new CardLayout());
		center.add(marine, CARD_MARINE);
		center.add(energy, CARD_ENERGY);

		// --- barre de boutons en haut ---
		JToggleButton btnMarine = new JToggleButton("Tableau de bord");
		JToggleButton btnEnergy = new JToggleButton("Marine Energy Control");
		ButtonGroup group = new ButtonGroup();
		group.add(btnMarine);
		group.add(btnEnergy);
		btnMarine.setSelected(true);

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
		top.add(new JLabel("Vue :"));
		top.add(btnMarine);
		top.add(btnEnergy);

		// --- actions : bascule de carte ---
		CardLayout cards = (CardLayout) center.getLayout();
		btnMarine.addActionListener(e -> cards.show(center, CARD_MARINE));
		btnEnergy.addActionListener(e -> cards.show(center, CARD_ENERGY));

		// (option) raccourcis clavier
		btnMarine.setMnemonic('M');
		btnEnergy.setMnemonic('E');

		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
	}
}
