/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ch.epfl.flamemaker.flame.FlameSet;
import ch.epfl.flamemaker.flame.Presets;

public class FlameMakerGUI implements FlameSet.Listener {

	/*
	 * Contient toutes les informations sur la flame courante et ses informations d'affichage
	 */
	private FlameSet m_set = new FlameSet(Presets.SHARKFIN_FRACTALE);
	
	
	private int m_selectedTransformationId;
	private Set<Listener> m_listeners = new HashSet<Listener>();
	
	private final AffineTransformationsComponent m_affineTransformationComponent;
	private final AffineModificationComponent m_affineModificationComponent;
	private final TransformationsEditPanel m_transformationsEditPanel;
	private final WeightsModificationComponent m_weightsModificationComponent;
	
	public FlameMakerGUI() {
		m_set = new FlameSet(Presets.SHARKFIN_FRACTALE);
		m_set.addListener(this);
		
		m_affineTransformationComponent = new AffineTransformationsComponent(m_set);
		m_affineModificationComponent = new AffineModificationComponent(m_set.getBuilder());
		m_transformationsEditPanel = new TransformationsEditPanel(m_set.getBuilder());
		m_weightsModificationComponent = new WeightsModificationComponent(m_set.getBuilder());;
	}
	
	public void start() {
		final JFrame window = new JFrame("FlameMaker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = window.getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		JPanel upperPanel = buildUpperPanel();
		JPanel lowerPanel = buildLowerPanel();
		
		contentPane.add(upperPanel, BorderLayout.CENTER);
		contentPane.add(lowerPanel, BorderLayout.PAGE_END);
		
		JMenuBar menu = MenuBar.build(window, m_set, m_transformationsEditPanel.getListModel());
		window.setJMenuBar(menu);
		
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		this.addListener(new Listener(){

			@Override
			public void onSelectedTransformationIdChange(int id) {
				m_affineTransformationComponent.highlightedTransformationIndex(id);
				m_affineModificationComponent.setSelectedTransformationIndex(id);
				m_transformationsEditPanel.setSelectedTransformationIndex(id);
				m_weightsModificationComponent.setSelectedTransformationIndex(id);
			}
			
		});
	}
	
	
	private JPanel buildTransformationsPreviewPanel() {
		JPanel transformationsPreviewPanel = new JPanel();
		final FlameMakerGUI self = this;
		
		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		
		
		m_affineTransformationComponent.highlightedTransformationIndex(0);
		
		m_affineTransformationComponent.addListener(new AffineTransformationsComponent.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}
			
		});
		
		transformationsPreviewPanel.add(m_affineTransformationComponent, BorderLayout.CENTER);
		
		return transformationsPreviewPanel;
	}

	private JPanel buildUpperPanel() {
		JPanel upperPanel = new JPanel();
		
		JPanel transformationsPreviewPanel = buildTransformationsPreviewPanel(); 
		JPanel fractalPanel = buildFractalePanel();
		
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		return upperPanel;
	}

	private JPanel buildFractalePanel() {
		JPanel fractalPanel = new JPanel();

		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
		
		fractalPanel.add(new FlameBuilderPreviewComponent(m_set), BorderLayout.CENTER);
		
		return fractalPanel;
	}

	private JPanel buildLowerPanel() {
		JPanel lowerPanel = new JPanel();
		final FlameMakerGUI that = this;
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
	
		m_transformationsEditPanel.addListener(new TransformationsEditPanel.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				that.setSelectedTransformationId(transfoId);
			}
			
		});
		
		JPanel selectedTransformationEditPanel = buildSelectedTransformationEditPanel();
		
		lowerPanel.add(m_transformationsEditPanel);
		lowerPanel.add(selectedTransformationEditPanel);
		
		return lowerPanel;
	}

	private JPanel buildSelectedTransformationEditPanel() {
		JPanel selectedTransformationEditPanel = new JPanel();
		
		selectedTransformationEditPanel.setLayout(new BoxLayout(selectedTransformationEditPanel, BoxLayout.PAGE_AXIS));
		selectedTransformationEditPanel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));
		
		m_affineModificationComponent.setSelectedTransformationIndex(0);
		
		selectedTransformationEditPanel.add(m_affineModificationComponent);
		
		selectedTransformationEditPanel.add(new JSeparator());
		
		m_weightsModificationComponent.setSelectedTransformationIndex(0);
		
		selectedTransformationEditPanel.add(m_weightsModificationComponent);
		
		return selectedTransformationEditPanel;
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
