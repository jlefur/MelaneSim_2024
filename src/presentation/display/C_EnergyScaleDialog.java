package presentation.display;
import javax.swing.*;

import data.C_Parameters;
import data.constants.I_ConstantPNMC;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class C_EnergyScaleDialog extends JDialog implements I_ConstantPNMC {

	private static final long serialVersionUID = 1L;

	// --- sliders
	private final JSlider chloroSlider;
	private final JSlider microSlider;
	private final JSlider whaleSlider;

	public C_EnergyScaleDialog(Frame owner, double initChloro, double initMicro, double initWhale) {
		super(owner, "Energy scale editor", true); // modal

		chloroSlider = mkSlider(initChloro);
		microSlider = mkSlider(initMicro);
		whaleSlider = mkSlider(initWhale);

		JPanel grid = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 12, 8, 12);
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		grid.add(new JLabel("Chlorophyll multiplier"), c);
		c.gridx = 1;
		grid.add(chloroSlider, c);
		c.gridx = 0;
		c.gridy = 1;
		grid.add(new JLabel("Micronekton multiplier"), c);
		c.gridx = 1;
		grid.add(microSlider, c);
		c.gridx = 0;
		c.gridy = 2;
		grid.add(new JLabel("Whale multiplier"), c);
		c.gridx = 1;
		grid.add(whaleSlider, c);

		JButton cancel = new JButton(new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		JButton done = new JButton(new AbstractAction("Done") {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyToGlobals();
				dispose();
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(cancel);
		buttons.add(done);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(grid, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(owner);
	}

	// slider helper
	private static JSlider mkSlider(double initFactor) {
		int init = (int) Math.round(initFactor * 100.0); // 1.0 => 100
		JSlider s = new JSlider(0, 200, Math.max(0, Math.min(200, init)));
		s.setPaintTicks(true);
		s.setMajorTickSpacing(50);
		s.setMinorTickSpacing(10);
		s.setPaintLabels(true);
		return s;
	}

	// === applique directement les valeurs ===
	private void applyToGlobals() {
		double chloro = chloroSlider.getValue() / 100.0;
		double micro = microSlider.getValue() / 100.0;
		double whale = whaleSlider.getValue() / 100.0;

		// ⚙️ affecte tes variables statiques
		C_Parameters.CHLOROPHYLL_MULTIPLIER = chloro *100.0;
		C_Parameters.MICRONEKTON_MULTIPLIER = micro *100.0;
		C_Parameters.ENERGY_MULTIPLIER_PLANKTON = whale *100.0;

		System.out.println("✅ Multipliers updated: " + "CHLORO=" + chloro + ", MICRO=" + micro + ", WHALE=" + whale);
	}
}