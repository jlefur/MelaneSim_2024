package presentation.display;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.function.DoubleConsumer;

/**
 * Curseur vertical + champ numérique + presets. Pour tester : instancie-la
 * depuis une JFrame.
 */
public class EnergyColumn extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JLabel title;
	private final JSlider slider;
	private final JSpinner spinner;
	private final DecimalFormat df = new DecimalFormat("0.00");
	private boolean updating = false;

	public EnergyColumn(String name, double initial, double min, double max, DoubleConsumer onChange) {
		super(new GridBagLayout());
		setBorder(BorderFactory.createTitledBorder(name));

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.weightx = 1.0;
		c.insets = new Insets(4, 6, 4, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		// titre au-dessus
		title = new JLabel(df.format(initial) + "×", SwingConstants.CENTER);
		c.gridy = 0;
		c.weighty = 0;
		add(title, c);

		// slider vertical
		slider = new JSlider(JSlider.VERTICAL, (int) Math.round(min * 100), (int) Math.round(max * 100),
				(int) Math.round(initial * 100));
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(25);
		slider.setMinorTickSpacing(5);
		slider.setPaintLabels(true);
		c.gridy = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(slider, c);

		// champ numérique
		spinner = new JSpinner(new SpinnerNumberModel(initial, min, max, 0.01));
		((JSpinner.NumberEditor) spinner.getEditor()).getFormat().applyPattern("0.00");
		c.gridy = 2;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(spinner, c);

		// boutons de presets
		JPanel presets = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
		for (double v : new double[] { 0.5, 1.0, 2.0 }) {
			JButton b = new JButton(df.format(v) + "×");
			b.setMargin(new Insets(2, 6, 2, 6));
			b.addActionListener(e -> setValue(v, onChange));
			presets.add(b);
		}
		c.gridy = 3;
		add(presets, c);

		// synchronisation slider -> spinner
		slider.addChangeListener((ChangeEvent e) -> {
			if (!updating) {
				updating = true;
				double val = slider.getValue() / 100.0;
				spinner.setValue(val);
				title.setText(df.format(val) + "×");
				onChange.accept(val);
				updating = false;
			}
		});

		// synchronisation spinner -> slider
		spinner.addChangeListener(e -> {
			if (!updating) {
				updating = true;
				double val = ((Number) spinner.getValue()).doubleValue();
				slider.setValue((int) Math.round(val * 100));
				title.setText(df.format(val) + "×");
				onChange.accept(val);
				updating = false;
			}
		});
	}

	private void setValue(double v, DoubleConsumer onChange) {
		updating = true;
		spinner.setValue(v);
		slider.setValue((int) Math.round(v * 100));
		title.setText(df.format(v) + "×");
		onChange.accept(v);
		updating = false;
	}
}
