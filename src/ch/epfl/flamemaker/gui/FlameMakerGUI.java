/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 *	Classe modélisant la fenêtre de l'interface graphique.
 */
public class FlameMakerGUI {

	
	/**
	 * Le builder de la fractale
	 */
	private ObservableFlameBuilder m_builder;

	/**
	 * La couleur de fond à utiliser
	 */
	private Color m_backgroundColor = Color.BLACK;

	/**
	 * La palette à utiliser
	 */
	private Palette m_palette;

	/**
	 * Le cadre de dessin à utiliser
	 */
	private Rectangle m_frame;

	/**
	 * La densité à utiliser
	 */
	private int m_density = 50;

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
		m_frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		m_palette = new InterpolatedPalette(Arrays.asList(Color.RED,
				Color.GREEN, Color.BLUE));

		// Configure la fractale "shark fin"
		m_builder = buildSharkFin();
		
		m_affineTransformationComponent = new AffineTransformationsComponent(m_builder, m_frame);
		m_affineModificationComponent = new AffineModificationComponent(m_builder);
		m_transformationsEditPanel = new TransformationsEditPanel(m_builder);
		m_weightsModificationComponent = new WeightsModificationComponent(m_builder);
	}
	
	/**
	 * Utilisée par le constructeur pour initialiser la fractale SharkFin
	 * @return {@link ObservableFlameBuilder} Le builder de cette fractale
	 */
	private ObservableFlameBuilder buildSharkFin() {
		return 
			new ObservableFlameBuilder(new Flame(Arrays.asList(
				new FlameTransformation(
						new AffineTransformation(
								-0.4113504,	-0.7124804, -0.4, 0.7124795, -0.4113508, 0.8
						),
						new double[] { 1, 0.1, 0, 0, 0, 0 }),
				new FlameTransformation(
						new AffineTransformation(
								-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), 
								new double[] { 0, 0, 0, 0, 0.8, 1 }
						), 
				new FlameTransformation(
						new AffineTransformation(
								0.4810169, 0, 1, 0, 0.4810169, 0.9), 
								new double[] { 1, 0, 0, 0, 0, 0 }
						)
				)
		));
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

		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));


		m_affineTransformationComponent.highlightedTransformationIndex(0);

		transformationsPreviewPanel.add(m_affineTransformationComponent, BorderLayout.CENTER);

		return transformationsPreviewPanel;
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

		fractalPanel.add(new FlameBuilderPreviewComponent(m_builder,
				m_backgroundColor, m_palette, m_frame, m_density),
				BorderLayout.CENTER);

		return fractalPanel;
	}

	/**
	 * Crée et remplit le panneau inférieur
	 * 
	 * @return Le panneau inférieur
	 */
	private JPanel buildLowerPanel() {
		JPanel lowerPanel = new JPanel();
		final FlameMakerGUI self = this;
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));

		m_transformationsEditPanel.addListener(new TransformationsEditPanel.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}

		});

		JPanel selectedTransformationEditPanel = buildSelectedTransformationEditPanel();

		lowerPanel.add(m_transformationsEditPanel);
		lowerPanel.add(selectedTransformationEditPanel);

		return lowerPanel;
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
	 * @param id
	 *            l'id de la transformation
	 */
	public void setSelectedTransformationId(int id){

		m_selectedTransformationId = id;

		Iterator<Listener> it = m_listeners.iterator();
		while(it.hasNext()){
			it.next().onSelectedTransformationIdChange(id);
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
	 * L'interface que les classes des objets désirant écouter
	 * la transformation actuellement sélectionée doivent implémenter.
	 */
	public interface Listener {
		public void onSelectedTransformationIdChange(int id);
	}
}
