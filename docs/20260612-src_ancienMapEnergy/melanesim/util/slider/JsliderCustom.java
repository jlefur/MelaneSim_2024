package melanesim.util.slider;

import java.awt.Color;
import javax.swing.JSlider;

public class JsliderCustom extends JSlider {

	private static final long serialVersionUID = 1L;

	public JsliderCustom() {
        setOpaque(false);
        setBackground(new Color(180, 180, 180));
        setForeground(new Color(69, 124, 235));
        setUI(new JSliderUI(this));
    }
}
