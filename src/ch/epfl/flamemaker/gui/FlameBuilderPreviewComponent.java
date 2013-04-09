package ch.epfl.flamemaker.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.geometry2d.Rectangle;

@SuppressWarnings("serial")
public class FlameBuilderPreviewComponent extends JComponent{
	
	FlameAccumulator m_accumulator;
	Palette m_palette;
	Color m_bgColor;

	public FlameBuilderPreviewComponent(
			Flame.Builder builder,
			Color backgroundColor,
			Palette palette,
			Rectangle frame,
			int density){
		
		m_bgColor = backgroundColor;
		m_palette = palette;
		
		m_accumulator = builder.build().compute(frame, 100, 100, density);
	}
	
	@Override
	protected void paintComponent(Graphics g){
		
		
		BufferedImage image = new BufferedImage(m_accumulator.width(), m_accumulator.height(), BufferedImage.TYPE_INT_RGB);
		
		for(int x = 0 ; x < m_accumulator.width() ; x++){
			for(int y = 0 ; y < m_accumulator.height() ; y++){
				
				image.setRGB(x, y, m_accumulator.color(m_palette, m_bgColor, x, y).asPackedRGB());
			}
		}
		
		g.drawImage(image, 0, 0, null);
	}
	
	@Override
	public Dimension getPreferredSize(){
		
		return new Dimension(m_accumulator.width(), m_accumulator.height());
	}
}
