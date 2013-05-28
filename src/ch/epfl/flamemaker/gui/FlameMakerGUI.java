/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.HashSet;
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

	/**
	 * Contient toutes les informations sur la fractale courante et ses informations d'affichage
	 */
	private FlameSet m_set = new FlameSet(Presets.SHARKFIN_FRACTALE);
	
	
	/**
	 * L'attribut observable représentant l'id de la transformation actuellement
	 * sélectionnée
	 */
	private int m_selectedTransformationId;

	/**
	 * Les observateurs de l'id de la transformation actuellement sélectionée.
	 * 
	 * @see FlameMakerGUI.Listener
	 */
	private Set<Listener> m_listeners = new HashSet<Listener>();

	/**
	 * Le composant d'affichage des composantes affines des transformations;
	 * Celui-ci est final et initialisé par le constructeur afin de pouvoir être
	 * utilisé dans une instance d'une classe anonyme et utilisé à plusieurs
	 * endroits du code.
	 * 
	 * @see AffineTransformationsComponent
	 */
	private final AffineTransformationsComponent m_affineTransformationComponent;

	/**
	 * Le composant de modification des transformations affines. Final et
	 * initialisé dans le constructeur pour les mêmes raisons que le composant
	 * d'affichage des composantes affines.
	 */
	private final AffineModificationComponent m_affineModificationComponent;

	/**
	 * Le panneau d'édition des transformations. Contient les composants
	 * d'édition des composantes affines et de modification des poids des
	 * variations. Final et initialisé dans le constructeur pour les mêmes
	 * raisons que plus haut.
	 */
	private final TransformationsEditPanel m_transformationsEditPanel;

	/**
	 * Le composant de modification des poids des variations. Final et
	 * initialisé dans le constructeur pour les mêmes raisons que plus haut.
	 */
	private final WeightsModificationComponent m_weightsModificationComponent;
	
	/**
	 * Le constructeur de la classe modélisant le GUI. Appelé pour lancer ce
	 * dernier.
	 */
	public FlameMakerGUI() {
		m_set = new FlameSet(Presets.SHARKFIN_FRACTALE);
		m_set.addListener(this);
		
		m_affineTransformationComponent = new AffineTransformationsComponent(m_set);
		m_affineModificationComponent = new AffineModificationComponent(m_set.getBuilder());
		m_transformationsEditPanel = new TransformationsEditPanel(m_set.getBuilder());
		m_weightsModificationComponent = new WeightsModificationComponent(m_set.getBuilder());;
	}
	
	
	/**
	 * Initialise et affiche la fenêtre principale
	 * du GUI à l'écran.
	 */
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
	
	/**
	 * Crée et remplit le panneau supérieur.
	 * @return Le panneau supérieur
	 */
	private JPanel buildUpperPanel() {
		JPanel upperPanel = new JPanel();
		
		JPanel transformationsPreviewPanel = buildTransformationsPreviewPanel(); 
		JPanel fractalPanel = buildFractalePanel();
		
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		return upperPanel;
	}

	/**
	 * Crée et remplit le panneau de visualisation des composantes affines des
	 * transformations
	 * 
	 * @return Le panneau de visualisation des composantes affines des
	 *         transformations
	 */
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

	/**
	 * Crée et remplit le panneau inférieur
	 * 
	 * @return Le panneau inférieur
	 */
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

	/**
	 * Crée et remplit le panneau de prévisualisation de la fractale.
	 * 
	 * @return Le panneau de prévisualisation de la fractale.
	 */
	private JPanel buildFractalePanel() {
		JPanel fractalPanel = new JPanel();

		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
		
		fractalPanel.add(new FlameBuilderPreviewComponent(m_set), BorderLayout.CENTER);
		
		return fractalPanel;
	}

	/**
	 * Crée et construit le panneau de modification des transformations affines
	 * 
	 * @return Le panneau de modification des transformations affines
	 */
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
	 * @return L'id de la transformations actuellement sélectionnée
	 */
	public int getSelectedTransformationId(){
		return m_selectedTransformationId;
	}

	/**
	 * Modifie l'id de la transformation actuellement sélectionée
	 * 
	 * @param l
	 *            'id de la transformation
	 */
	public void setSelectedTransformationId(int id){
		m_selectedTransformationId = id;

		for(Listener l: m_listeners) {
			l.onSelectedTransformationIdChange(id);
		}
	}

	/**
	 * Ajoute un observateur qui sera notifié lorsqu'une nouvelle transformation
	 * est sélectionnée
	 * 
	 * @param l
	 *            L'observateur à ajouter
	 * @see #removeListener
	 */
	public void addListener(Listener l) {
		m_listeners.add(l);
	}

	/**
	 * Supprime un observateur
	 * 
	 * @param l
	 *            L'observateur à supprimer
	 * @see #addListener
	 */
	public void removeListener(Listener l) {
		m_listeners.remove(l);
	}

	/**
	 * L'interface que les classes des objets désirant écouter la transformation
	 * actuellement sélectionée doivent implémenter.
	 */
	public interface Listener {
		public void onSelectedTransformationIdChange(int id);
	}

	/* 
	 * @see ch.epfl.flamemaker.flame.FlameSet.Listener#onSetChanged(ch.epfl.flamemaker.flame.FlameSet)
	 */
	@Override
	public void onSetChanged(FlameSet set) {
		// Lorsqu'un nouveau preset est chargé, 
		// on sélectionne la première transformation de la liste
		setSelectedTransformationId(0);
	}
}