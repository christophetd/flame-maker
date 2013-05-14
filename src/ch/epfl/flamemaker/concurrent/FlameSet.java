package ch.epfl.flamemaker.concurrent;

import java.util.ArrayList;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Preset;
import ch.epfl.flamemaker.geometry2d.ObservableRectangle;

// TODO javadoc
public class FlameSet {
	
	public static final int DEFAULT_DENSITY = 50;
	
	private ObservableFlameBuilder m_flameBuilder;
	private Color m_backgroundColor;
	private Palette m_palette;
	private ObservableRectangle m_frame;
	private int m_density;
	
	public FlameSet(){
		m_frame = new ObservableRectangle(null);
		m_flameBuilder = new ObservableFlameBuilder(new Flame(new ArrayList<FlameTransformation>()));
		m_backgroundColor = Color.BLACK;
		m_density = DEFAULT_DENSITY;
	}
	
	public FlameSet(Preset preset){
		m_frame = new ObservableRectangle(preset.frame());
		m_flameBuilder = new ObservableFlameBuilder(new Flame(preset.transformations()));
		m_palette = preset.palette();
		m_backgroundColor = Color.BLACK;
		m_density = DEFAULT_DENSITY;
	}
	
	public ObservableFlameBuilder getBuilder(){
		return m_flameBuilder;
	}
	
	public Color getBackgroundColor(){
		return m_backgroundColor;
	}
	
	public Palette getPalette(){
		return m_palette;
	}
	
	public ObservableRectangle getFrame(){
		return m_frame;
	}
	
	public int getDensity(){
		return m_density;
	}
	
	public void loadPreset(Preset preset){
		m_palette = preset.palette();
		m_frame.set(preset.frame());
		m_flameBuilder.set(new Flame(preset.transformations()));
	}
}
