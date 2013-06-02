package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

import javax.swing.JColorChooser;
import javax.swing.JComponent;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.ColorUtils;
import ch.epfl.flamemaker.color.CustomPalette;
import ch.epfl.flamemaker.color.CustomPalette.Builder;

public class PaletteEditComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	private CustomPalette.Builder m_palette;
	
	private static final double PALETTE_WIDTH_RATIO		= 0.66666;
	private static final double PALETTE_HEIGHT_RATIO	= 0.2;
	private static final int COLOR_SAMPLE_RADIUS		= 5;
	private static final int COLOR_SAMPLE_MARGIN_TOP	= 6;
	private static final int ARROW_WIDTH				= 5;
	private static final int ARROW_HEIGHT				= 5;
	
	private double m_dragPos = -1;
	private int m_dragDelta;
	
	public PaletteEditComponent(CustomPalette.Builder palette){
		m_palette = palette;
		
		setPreferredSize(new Dimension(300, 200));
		
		m_palette.addListener(new CustomPalette.Builder.Listener() {

			@Override
			public void onPaletteChange(Builder p) {
				repaint();
			}
			
		});
		
		this.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				double pos = selectColor(e.getX(), e.getY());
				if(pos != -1){
					if(e.getButton() == 1){
						java.awt.Color col = JColorChooser.showDialog(null, "JColorChooser Sample", ColorUtils.colorToAwt(m_palette.getColor(pos)));
						if(col != null)
							m_palette.setColor(ColorUtils.awtToColor(col), pos);
						
					} else if (e.getButton() == 3 && m_palette.getColorsCount() > 1){
						m_palette.removeColorAtPos(pos);
					}
				} else if(e.getClickCount() == 2){
					pos = posToPalette(e.getX());
					if(pos >= 0 && pos <= 1){
						m_palette.setColor(m_palette.build().colorForIndex(pos), pos);
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				double pos = selectColor(e.getX(), e.getY());
				if(pos != -1){
					m_dragPos = pos;
					m_dragDelta = e.getX() - paletteToPos(pos);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				m_dragPos = -1;
			}
			
		});
		
		this.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent evt) {
				if(m_dragPos != -1){
					double pos = posToPalette(evt.getX() - m_dragDelta);
					
					if(pos < 0) pos = 0;
					if(pos > 1) pos = 1;
					// Prevents from overwriting another color
					if(m_palette.getColor(pos) == null){
						Color col = m_palette.getColor(m_dragPos);
						m_palette.removeColorAtPos(m_dragPos);
						
						m_palette.setColor(col, pos);
						
						m_dragPos = pos;
					}
				}
			}

			@Override
			public void mouseMoved(MouseEvent evt) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	

	@Override
	protected void paintComponent(final Graphics g){
		
		BufferedImage image = ColorUtils.renderPalette(m_palette.build(), 100);
		
		int paletteWidth = (int) (getWidth()*PALETTE_WIDTH_RATIO);
		int paletteHeight = (int) (getHeight()*PALETTE_HEIGHT_RATIO);
		int originX = (getWidth() - paletteWidth)/2;
		int originY = (getHeight() - paletteHeight)/2;
		
		g.drawString("hint : double click on strip to add a key, left click to edit key,", 3, 20);
		g.drawString("right click to remove key, drag to move", 3, 35);
		g.drawImage(image, originX, originY, paletteWidth, paletteHeight, null);
		
		Map<Double, Color> colors = m_palette.getColors();
		Set<Double> positions = colors.keySet();
		for(double d : positions){
			int x = (int) (originX + paletteWidth*d);
			
			g.setColor(ColorUtils.colorToAwt(colors.get(d)));
			g.fillRect(x - COLOR_SAMPLE_RADIUS, originY + paletteHeight + COLOR_SAMPLE_MARGIN_TOP, COLOR_SAMPLE_RADIUS*2, COLOR_SAMPLE_RADIUS*2);
			g.setColor(java.awt.Color.BLACK);
			g.drawRect(x - COLOR_SAMPLE_RADIUS, originY + paletteHeight + COLOR_SAMPLE_MARGIN_TOP, COLOR_SAMPLE_RADIUS*2, COLOR_SAMPLE_RADIUS*2);
			
			// Draw the arrow
			int arrowBase = originY+paletteHeight+ARROW_HEIGHT;
			int[] xPoints = new int[]{x, x+ARROW_WIDTH, x-ARROW_WIDTH};
			int[] yPoints = new int[]{originY+paletteHeight, arrowBase, arrowBase}; 
			g.fillPolygon(xPoints, yPoints, 3);
		}
	}
	
	/**
	 * Checks if a color sample is under this coordinates
	 * @param x
	 * @param y
	 * @return the color sample id or -1
	 */
	private double selectColor(int x, int y){
		double ret = -1;
		
		int paletteWidth = (int) (getWidth()*PALETTE_WIDTH_RATIO);
		int paletteHeight = (int) (getHeight()*PALETTE_HEIGHT_RATIO);
		
		int originX = (getWidth() - paletteWidth)/2;
		int py =  (getHeight() - paletteHeight)/2 + paletteHeight + COLOR_SAMPLE_MARGIN_TOP + COLOR_SAMPLE_RADIUS;
		
		// Checks already y-axis
		if(Math.abs(y - py) > COLOR_SAMPLE_RADIUS) return -1;
		
		Set<Double> positions = m_palette.getColors().keySet();
		
		for(double d : positions){
			int px = (int) (originX + paletteWidth*d);
			
			// Checks x-axis
			if( Math.abs(x - px) < COLOR_SAMPLE_RADIUS)
				ret = d;
		}
		
		return ret;
	}
	
	private double posToPalette(int pos){
		double paletteWidth = getWidth()*PALETTE_WIDTH_RATIO;
		return (pos - (getWidth() - paletteWidth)/2)/paletteWidth;
	}
	
	private int paletteToPos(double pos){
		double paletteWidth = getWidth()*PALETTE_WIDTH_RATIO;
		return (int) ((getWidth() - paletteWidth)/2 + pos*paletteWidth);
	}
}
