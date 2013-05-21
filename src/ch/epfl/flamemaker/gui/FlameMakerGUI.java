package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ch.epfl.flamemaker.flame.FlameSet;
import ch.epfl.flamemaker.flame.Preset;

public class FlameMakerGUI implements FlameSet.Listener {

	/*
	 * Contient toutes les informations sur la flame courante et ses informations d'affichage
	 */
	private FlameSet m_set;
	
	
	private int m_selectedTransformationId;
	private Set<Listener> m_listeners = new HashSet<Listener>();
	
	public FlameMakerGUI() {
		m_set = new FlameSet(Preset.ALL_PRESETS.get(0));
		
		m_set.addListener(this);
	}
	
	public void start() {
		final FlameMakerGUI self = this;
		
		final JFrame window = new JFrame("FlameMaker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.getContentPane().setLayout(new BorderLayout());
		
		JPanel	upperPanel = new JPanel(), 
				lowerPanel = new JPanel(),
				transformationsPreviewPanel = new JPanel(),
				selectedTransformationEditPanel = new JPanel(),
				fractalPanel = new JPanel();
		
		final AffineTransformationsComponent affineTransformationComponent = new AffineTransformationsComponent(m_set);
		
		/* Upper panel */
		window.getContentPane().add(upperPanel, BorderLayout.CENTER);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
	
		
		transformationsPreviewPanel.add(affineTransformationComponent, BorderLayout.CENTER);
		fractalPanel.add(new FlameBuilderPreviewComponent(m_set), BorderLayout.CENTER);
		
		/* Lower panel */
		window.getContentPane().add(lowerPanel, BorderLayout.PAGE_END);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		
		final TransformationsEditPanel transformationsEditPanel = new TransformationsEditPanel(m_set.getBuilder());
		// Panneau d'édition des transformations
		lowerPanel.add(transformationsEditPanel);
		
		/* TODO : rassembler du code dupliqué (ici et un peu plus bas) */
		transformationsEditPanel.addListener(new TransformationsEditPanel.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}
			
		});
		
		/* ---- Paneau d'édition de la transformation sélectionnée ---- */
		
		lowerPanel.add(selectedTransformationEditPanel);
		selectedTransformationEditPanel.setLayout(new BoxLayout(selectedTransformationEditPanel, BoxLayout.PAGE_AXIS));
		selectedTransformationEditPanel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));
		
		final AffineModificationComponent affineModificationComponent = new AffineModificationComponent(m_set.getBuilder());
		affineModificationComponent.setSelectedTransformationIndex(0);
		affineTransformationComponent.highlightedTransformationIndex(0);
		
		/* TODO : rassembler du code dupliqué (ici et un peu plus haut) */
		affineTransformationComponent.addListener(new AffineTransformationsComponent.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}
			
		});
		
		selectedTransformationEditPanel.add(affineModificationComponent);
		
		selectedTransformationEditPanel.add(new JSeparator());
		
		final WeightsModificationComponent weightsModificationComponent = new WeightsModificationComponent(m_set.getBuilder());
		weightsModificationComponent.setSelectedTransformationIndex(0);
		selectedTransformationEditPanel.add(weightsModificationComponent);
		
		this.addListener(new Listener(){

			@Override
			public void onSelectedTransformationIdChange(int id) {
				affineTransformationComponent.highlightedTransformationIndex(id);
				affineModificationComponent.setSelectedTransformationIndex(id);
				transformationsEditPanel.setSelectedTransformationIndex(id);
				weightsModificationComponent.setSelectedTransformationIndex(id);
			}
			
		});
		
		/* -------- */
		MenuBar.build(window, m_set, transformationsEditPanel.getListModel());

		ImageIcon icon = new ImageIcon("C:\\Users\\Christophe\\workspace\\flame-maker\\icon.png");
		window.setIconImage(icon.getImage());
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	/**
	 * Gets the currently selected transformation id
	 */
	public int getSelectedTransformationId(){
		return m_selectedTransformationId;
	}
	
	/**
	 * Sets the currently selected transformation id
	 * @param id of the transformation
	 */
	public void setSelectedTransformationId(int id){
		
		m_selectedTransformationId = id;

		Iterator<Listener> it = m_listeners.iterator();
		while(it.hasNext()){
			it.next().onSelectedTransformationIdChange(id);
		}
	}
	
	/**
	 * Adds a listener notified when a global GUI value change (ie. selectedTransformationId)
	 * @param l listener to add
	 * @see #removeListener
	 */
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	/**
	 * Removes a listener.
	 * @param l listener to remove
	 * @see #addListener
	 */
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	/**
	 *	Interface for GUI listeners.
	 */
	public interface Listener{
		public void onSelectedTransformationIdChange(int id);
	}

	@Override
	public void onSetChanged(FlameSet set) {
		setSelectedTransformationId(0);
	}
	
	
}
