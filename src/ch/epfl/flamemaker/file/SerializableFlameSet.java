package ch.epfl.flamemaker.file;

import java.io.Serializable;
import java.util.ArrayList;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.FlameSet;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class SerializableFlameSet implements Serializable {
	
	private static final long serialVersionUID = 6762950268081380270L;

	private ArrayList<FlameTransformation> m_transformationsList = new ArrayList<FlameTransformation>();

	
	private Color m_backgroundColor;
	
	private Palette m_palette;
	
	private Rectangle m_frame;
	
	private int m_density;
	
	public SerializableFlameSet(FlameSet set) {
		m_backgroundColor = set.getBackgroundColor();
		m_palette = set.getPalette();
		m_frame = set.getFrame().toRectangle();
		m_density = set.getDensity();
		buildTransformationsList(set.getBuilder());
	}
	
	private void buildTransformationsList(ObservableFlameBuilder builder) {
		for(int i = 0; i < builder.transformationsCount(); i++) {
			m_transformationsList.add(builder.getTransformation(i));
		}
	}
	
	public ArrayList<FlameTransformation> getTransformationsList() {
		return m_transformationsList;
	}

	public Color getBackgroundColor() {
		return m_backgroundColor;
	}

	public Palette getPalette() {
		return m_palette;
	}

	public Rectangle getFrame() {
		return m_frame;
	}

	public int getDensity() {
		return m_density;
	}
}