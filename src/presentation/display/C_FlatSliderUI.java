package presentation.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;

/** Slider "plat" (inspiré de melanesim.util.slider_material, sans dépendance TimingFramework) : piste grise fine,
 * partie remplie et curseur rond de la couleur du canal.
 * @author Dan Moulinie & Quoc-Kim Bui, MIAGE 11.2026 */
public class C_FlatSliderUI extends BasicSliderUI {
	private static final Color TRACK = new Color(200,200,200);
	private static final int TRACK_THICKNESS = 4, THUMB_SIZE = 16;
	private final Color color;

	public C_FlatSliderUI(JSlider slider, Color color) {
		super(slider);
		this.color = color;
		slider.setOpaque(false);
		slider.setFocusable(true);
	}

	@Override
	protected Dimension getThumbSize() { return new Dimension(THUMB_SIZE,THUMB_SIZE); }

	@Override
	public void paintFocus(Graphics g) {}

	@Override
	public void paintTrack(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		int thumbCenter = thumbRect.x+thumbRect.width/2;
		if(slider.getOrientation()==JSlider.HORIZONTAL){
			int y = trackRect.y+(trackRect.height-TRACK_THICKNESS)/2;
			g2.setColor(TRACK);
			g2.fillRoundRect(trackRect.x,y,trackRect.width,TRACK_THICKNESS,TRACK_THICKNESS,TRACK_THICKNESS);
			g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),130));
			g2.fillRoundRect(trackRect.x,y,thumbCenter-trackRect.x,TRACK_THICKNESS,TRACK_THICKNESS,TRACK_THICKNESS);
		}
		else{
			int x = trackRect.x+(trackRect.width-TRACK_THICKNESS)/2;
			g2.setColor(TRACK);
			g2.fillRoundRect(x,trackRect.y,TRACK_THICKNESS,trackRect.height,TRACK_THICKNESS,TRACK_THICKNESS);
		}
		g2.dispose();
	}

	@Override
	public void paintThumb(Graphics g) {
		Graphics2D g2 = (Graphics2D)g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(slider.isEnabled()?color:Color.LIGHT_GRAY);
		g2.fillOval(thumbRect.x,thumbRect.y,thumbRect.width-1,thumbRect.height-1);
		g2.dispose();
	}
}
