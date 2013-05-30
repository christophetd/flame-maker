/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ch.epfl.flamemaker.anim.FlameAnimation;
import ch.epfl.flamemaker.anim.TransformationAnimation;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Presets;
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
		m_flameBuilder = new FlameAnimation.Builder(new FlameAnimation(new ArrayList<TransformationAnimation>(), null, 240));
		m_backgroundColor = Color.BLACK;
		m_density = DEFAULT_DENSITY;
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
