package ch.epfl.flamemaker.gui;

import javax.swing.AbstractListModel;

import ch.epfl.flamemaker.flame.Flame.Builder;
import ch.epfl.flamemaker.flame.FlameTransformation;

public class TransformationsListModel extends AbstractListModel {
	
	private Builder m_builder;
	
	public TransformationsListModel(Builder flameBuilder) {
		m_builder = flameBuilder;
	}

	@Override
	public int getSize() {
		return m_builder.transformationsCount();
	}

	@Override
	public Object getElementAt(int index) {
		return "Transformation n°"+index;
	}
	
	public void addTransformation(FlameTransformation transfo) {
		m_builder.addTransformation(transfo);
		int currentIndex = getSize()-1;
		this.fireIntervalAdded(this, currentIndex, currentIndex);
	}
	
	public void removeTransformation(int index) {
		m_builder.removeTransformation(index);
		this.fireIntervalRemoved(this, index, index);
	}
}