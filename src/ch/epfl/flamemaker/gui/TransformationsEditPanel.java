package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

@SuppressWarnings("serial")
public class TransformationsEditPanel extends JPanel{

	private List<Listener> m_listeners = new LinkedList<Listener>();
	
	private TransformationsListModel m_listModel;
	
	JList m_transformationsList;
	
	public TransformationsEditPanel(final ObservableFlameBuilder flameBuilder){
		JPanel transformationsEditButtons = new JPanel();
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder("Transformations"));
		
		m_listModel = new TransformationsListModel(flameBuilder);
		
		m_transformationsList = new JList(m_listModel);
		
		m_transformationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_transformationsList.setVisibleRowCount(5);
		m_transformationsList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				System.out.println("Value changed : "+m_transformationsList.getSelectedIndex());
				notifyTransformationSelected(m_transformationsList.getSelectedIndex());
			}
		});
		
		JScrollPane transformationsPane = new JScrollPane(m_transformationsList);
		
		this.add(transformationsPane, BorderLayout.CENTER);
		this.add(transformationsEditButtons, BorderLayout.PAGE_END);
		
		transformationsEditButtons.setLayout(new GridLayout(1, 2));

		// Bouton 'supprimer'
		final JButton deleteTransformationButton = new JButton("Supprimer");
		deleteTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = m_transformationsList.getSelectedIndex();

				if(selectedIndex != -1) {
					m_listModel.removeTransformation(selectedIndex);
					m_transformationsList.setSelectedIndex(Math.max(0, --selectedIndex));
				}
				if(flameBuilder.transformationsCount() == 1) {
					deleteTransformationButton.setEnabled(false);
				}
			}
		});
		
		JButton addTransformationButton = new JButton("Ajouter");
		transformationsEditButtons.add(addTransformationButton);
		transformationsEditButtons.add(deleteTransformationButton);
		
		addTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				m_listModel.addTransformation(new FlameTransformation(
						AffineTransformation.IDENTITY, 
						new double[]{1, 0, 0, 0, 0, 0}						
				));
				
				int newHighlightedIndex = flameBuilder.transformationsCount()-1;
				
				notifyTransformationSelected(newHighlightedIndex);
				
				if(!deleteTransformationButton.isEnabled() && flameBuilder.transformationsCount() > 1) {
					deleteTransformationButton.setEnabled(true);
				}
			}
		});
		
		m_transformationsList.setSelectedIndex(0);
	}
	
	public TransformationsListModel getListModel(){
		return m_listModel;
	}
	
	public void setSelectedTransformationIndex(int id) {
		m_transformationsList.setSelectedIndex(id);
	}
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	private void notifyTransformationSelected(int id){
		for(Listener l : m_listeners){
			l.onTransformationSelected(id);
		}
	}
	
	public interface Listener {
		public void onTransformationSelected(int transfoId);
	}
}
