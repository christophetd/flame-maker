package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class Preset {
	
	public static final List<Preset> ALL_PRESETS = Arrays.asList(
			new Preset(
					"Shark fin",
					new Rectangle(new Point(-0.25, 0), 5, 4),
					//new InterpolatedPalette(Arrays.asList(new Color(1,1,1), new Color(1, 1, 0), new Color(1, 0, 1))),
					new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
					Arrays.asList(
							new FlameTransformation(new AffineTransformation(
								-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
								new double[] { 1, 0.1, 0, 0, 0, 0 }),
							new FlameTransformation(new AffineTransformation(
									-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), new double[] { 0, 0, 0, 0, 0.8, 1 }),
							new FlameTransformation(new AffineTransformation(
									0.4810169, 0, 1, 0, 0.4810169, 0.9), 
									new double[] { 1, 0, 0, 0, 0, 0 })
					)
			),
			new Preset(
					"Bapt",
					new Rectangle(new Point(0, 0), 5, 5),
					new InterpolatedPalette(Arrays.asList(new Color(1,1,1), new Color(1, 1, 0), new Color(1, 0, 1))),
					Arrays.asList(
							new FlameTransformation(new AffineTransformation(1, -0.3, 0, 1.2, -0.5, 0),
	            				new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(new AffineTransformation(1, -0.3, 0.3, -.3, 1, 0.3),
	            					new double[] { 1, 0, .4, .4, 0, 0 }),
	            			new FlameTransformation(new AffineTransformation(0.3, .3 , -.3, .5, -.5, 0),
	            					new double[] { 1, -0, 0, -.6, .0, 0 })
					)
			));
	
	private String m_name;
	private Rectangle m_frame;
	private Palette m_palette;
	private List<FlameTransformation> m_transforms;
	
	private Preset(String name, Rectangle frame, Palette palette, List<FlameTransformation> transformations){
		m_name = name;
		m_frame = frame;
		m_palette = palette;
		m_transforms = transformations;
	};
	
	public String name(){		return m_name; 		}
	public Rectangle frame(){ 	return m_frame; 	}
	public Palette palette(){ 	return m_palette; 	}
	public List<FlameTransformation> transformations(){ return m_transforms; }
}
