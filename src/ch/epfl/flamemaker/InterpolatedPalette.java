package ch.epfl.flamemaker;

import java.util.List;

import ch.epfl.flamemaker.color.Color;

public class InterpolatedPalette implements Palette {

	List<Color> m_colors;
	
	public InterpolatedPalette(List<Color> colors) {
		if(colors.size() < 2) {
			throw new IllegalArgumentException("An interpolated palette must contain at least 2 colors");
		}
		m_colors = colors;
	}
	
	@Override
	public Color colorForIndex(double index) {
		if(index < 0 || index > 1) {
			throw new IllegalArgumentException("index must be between 0 and 1");
		}
		
		int nbColors = m_colors.size();
		
		int lowColor = (int) Math.floor(nbColors * index);
		double highColorWeight = Math.abs(lowColor / nbColors - index);
		int highColor = lowColor+1;
		
		return m_colors.get(highColor).mixWith(m_colors.get(lowColor), highColorWeight);
	}

}
