/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.flamemaker.anim.AnimableTransformation;
import ch.epfl.flamemaker.anim.FlameAnimation;
import ch.epfl.flamemaker.anim.TransformationAnimation;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Presets;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.ObservableRectangle;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Structure de donnée partagée par tous les éléments de l'UI qui en ont besoin. 
 * Centralise toutes les données nécessaires au calcul et au dessin d'une fractale.
 * Cette structure facilite les interactions entre les différents composants de l'interface
 * graphique et permet de charger des préréglages avec {@link #loadPreset(Presets) }.<br />
 * <br />
 * Le set peut être observé pour savoir quand un préréglage est chargé. A noter toutefois que les
 * objets observables du set informeront aussi leurs observateurs d'un changement.
 */
public class FlameSet {
	
	/** Paramètre de densité par défaut */
	public static final int DEFAULT_DENSITY = 50;
	
	
	// Attributs correspondant à l'ensemble des éléments pour la génération et le dessin d'une fractale
	
	/** constructeur de fractale */
	private FlameAnimation.Builder m_flameBuilder;
	/** Couleur de fond */
	private Color m_backgroundColor;
	/** Palette de couleur pour le dessin */
	private Palette m_palette;
	/** Rectangle source pour le calcul */
	private ObservableRectangle m_frame;
	/** densité de calcul */
	private int m_density;
	
	// Ensemble des observateurs
	private Set<Listener> m_listeners = new HashSet<Listener>();
	
	/** Construit un set par défaut */
	public FlameSet(){
		m_frame = new ObservableRectangle(new Rectangle(new Point(0,0), 5,5));
		m_backgroundColor = Color.BLACK;
		m_density = DEFAULT_DENSITY;
		
		//Constructs sharkfin preset
		m_flameBuilder = new FlameAnimation.Builder(new FlameAnimation(new ArrayList<TransformationAnimation>(), null, 240));
		
		TransformationAnimation t0 = new TransformationAnimation(new AnimableTransformation(
				new AffineTransformation(0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3),
				new double[] { 1, 0, 0.1, 0, 0, 0 }));
		t0.set(new AnimableTransformation (
				new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
				new double[] { 1, 0.1, 0, 0, 0, 0 }), m_flameBuilder.getDuration());
		
		TransformationAnimation t1 = new TransformationAnimation(new AnimableTransformation(
				new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3),
				new double[] { 1, 0, 0, 0, 0, 0 }));
		t1.set(new AnimableTransformation (
				new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), 
				new double[] { 0, 0, 0, 0, 0.8, 1 }), m_flameBuilder.getDuration());

		TransformationAnimation t2 = new TransformationAnimation(new AnimableTransformation(
				new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7),
				new double[] { 0.5, 0, 0, 0.4, 0, 0 }));
		t2.set(new AnimableTransformation (
				new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), 
				new double[] { 1, 0, 0, 0, 0, 0 }), m_flameBuilder.getDuration());
	
		m_flameBuilder.addTransformation(t0);
		m_flameBuilder.addTransformation(t1);
		m_flameBuilder.addTransformation(t2);
		
		m_palette = new InterpolatedPalette(Color.RED, Color.GREEN, Color.BLUE);
	}
	
	/** Construit un set selon le preset fourni en argument */
	/*public FlameSet(Presets preset){
		m_frame = new ObservableRectangle(preset.frame());
		m_flameBuilder = new FlameAnimation.Builder(new FlameAnimation(preset.transformations()));
		m_palette = preset.palette();
		m_backgroundColor = Color.BLACK;
		m_density = DEFAULT_DENSITY;
	}*/
	
	/** @return constructeur de flame observable associé au set */
	public FlameAnimation.Builder getBuilder(){
		return m_flameBuilder;
	}
	
	/** @return la couleur de fond */
	public Color getBackgroundColor(){
		return m_backgroundColor;
	}
	
	/** @return la palette de couleur */
	public Palette getPalette(){
		return m_palette;
	}
	
	/** @return le cadre de capture */
	public ObservableRectangle getFrame(){
		return m_frame;
	}
	
	/** @return la densité de calcul */
	public int getDensity(){
		return m_density;
	}
	
	/**
	 * Charge un preset dans le set sélectionné et informe les observateur d'une modification du set.
	 * @param preset préréglage à charger.
	 */
	/*public void loadPreset(Presets preset){
		m_palette = preset.palette();
		m_frame.set(preset.frame());
		m_flameBuilder.set(new Flame(preset.transformations()));
		
		notifyListeners();
	}
	
	public void importDataFrom(SerializableFlameSet other) {
		m_palette = other.getPalette();
		m_frame.set(other.getFrame());
		m_density = other.getDensity();
		m_backgroundColor = other.getBackgroundColor();
		m_flameBuilder.set(new Flame(other.getTransformationsList()));
		
		notifyListeners();
	}*/
	
	/**
	 * Ajoute un observateur au set.
	 * @param l observateur à ajouter
	 */
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	/**
	 * Supprime un observateur du set.
	 * @param l observateur à supprimer.
	 */
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	// Informe les observateurs d'un changement
	private void notifyListeners(){
		for(Listener l : m_listeners){
			l.onSetChanged(this);
		}
	}
	
	/**
	 * Définit un observateur de set. La méthode {@link #onSetChanged(FlameSet)} est appelée quand le set
	 * charge un nouveau preset.
	 */
	public interface Listener{
		/**
		 * Callback appelé quand le set observé charge un nouveau preset.
		 * @param set instance du set modifié.
		 */
		public void onSetChanged(FlameSet set);
	}
}
