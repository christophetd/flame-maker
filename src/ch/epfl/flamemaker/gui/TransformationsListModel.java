package ch.epfl.flamemaker.gui;

import javax.swing.AbstractListModel;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;

@SuppressWarnings("serial")
public class TransformationsListModel extends AbstractListModel implements ObservableFlameBuilder.Listener {
	
	private ObservableFlameBuilder m_builder;
	
	public TransformationsListModel(ObservableFlameBuilder flameBuilder) {
		m_builder = flameBuilder;
		m_builder.addListener(this);
	}

	@Override
	public int getSize() {
		return m_builder.transformationsCount();
	}

	@Override
	public Object getElementAt(int index) {
		return "Transformation n°"+(index+1);
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

	@Override
	public void onFlameBuilderChange(ObservableFlameBuilder b) {
		this.fireContentsChanged(this, 0, b.transformationsCount());
	}
}