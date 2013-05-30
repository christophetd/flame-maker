package ch.epfl.flamemaker.anim;

import java.util.List;

import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class KeyFrame {
	
	private final Rectangle m_viewport;
	private final List<FlameTransformation> m_transformations;
	private final Palette m_colorPalette;
	
	private final int m_position;
	
	public KeyFrame(Rectangle viewport, List<FlameTransformation> transformations, Palette colorPalette, int position){
		m_viewport = viewport;
		m_transformations = transformations;
		m_colorPalette = colorPalette;
		m_position = position;
	}
}
