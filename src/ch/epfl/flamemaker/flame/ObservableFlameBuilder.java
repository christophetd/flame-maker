package ch.epfl.flamemaker.flame;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;

/**
 * Décorateur pour rendre observable la classe {@link Flame.Builder}.
 */
public class ObservableFlameBuilder {
	
	private Flame.Builder m_builder;

	private Set<Listener> m_listeners = new HashSet<Listener>();
	
	/**
	 * Construit un bâtisseur à partir d'une fractale existante
	 * 
	 * @param flame
	 *            La fractale flame
	 */
	public ObservableFlameBuilder(Flame flame) {
		m_builder = new Flame.Builder(flame);
	}
	
	/**
	 * Remplace la construction actuelle par une nouvelle basée sur la fractale flame passée en argument.<br>
	 * Cette méthode conserve la factory.
	 * 
	 * @param flame modèle pour le nouvel état du constructeur
	 */
	public void set(Flame flame){
		Flame.Builder b = new Flame.Builder(flame);
		b.setFactory(m_builder.getFactory());
		m_builder = b;
		notifyObservers();
	}
	
	/**
	 * Construit un bâtisseur à partir d'une fractale donnée et de la stratégie donnée
	 * 
	 * @param flame
	 * 			La fractale flame
	 * @param strategy
	 * 			La stratégie à utiliser
	 */
	public ObservableFlameBuilder(Flame flame, FlameFactory strategy){
		m_builder = new Flame.Builder(flame, strategy);
	}
	
	/**
	 * Renvoie la transformation flame à l'index donné
	 * @param index L'index de la transformation
	 * @return La transformation flame correspondante
	 */
	public FlameTransformation getTransformation(int index) {
		return m_builder.getTransformation(index);
	}
	
	/**
	 * Ajoute un observateur au bâtisseur. Les observateurs sont notifiés à chaque changement du 
	 * bâtisseur par l'appel de leur méthode {@link #Listener.onFlameBuilderChange()}
	 * 
	 * @param l observateur  à ajouter
	 */
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	/**
	 * Supprime un observateur si il a été ajouté précédemment. Celui-ci ne sera plus notifié des modifications sur le bâtisseur.
	 * 
	 * @param l observateur à supprimer
	 */
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	/**
	 * @return Le nombre actuel de transformations de la fractale
	 */
	public int transformationsCount() {
		return m_builder.getTransformationsCount();
	}
	
	/**
	 * Ajoute une transformation de type flame à la fractale
	 * 
	 * @param transformation
	 *            La transformation
	 */
	public void addTransformation(FlameTransformation transformation) {
		m_builder.addTransformation(transformation);
		
		notifyObservers();
	}
	
	/**
	 * Notifies observers that the builder has changed.
	 * 
	 */
	private void notifyObservers(){
		Iterator<Listener> it = m_listeners.iterator();
		while(it.hasNext()){
			it.next().onFlameBuilderChange(this);
		}
	}
	
	/**
	 * @param index
	 * @return La composante affine de la transformation d'index
	 *         <i>index</i> de la fractale
	 * @throws IllegalArgumentException
	 *             Si l'index n'est pas valide
	 */
	public AffineTransformation affineTransformation(int index) {
		return m_builder.getAffineTransformation(index);
	}
	
	/**
	 * Remplace la composante affine de la transformation d'index
	 * <i>index</i> par <i>newTransformation</i>
	 * 
	 * @param index
	 * @param newTransformation
	 * @throws IllegalArgumentException
	 *             Si l'index n'est pas valide
	 */
	public void setAffineTransformation(int index,
			AffineTransformation newTransformation) {
		m_builder.setAffineTransformation(index, newTransformation);
		
		notifyObservers();
	}
	
	/**
	 * Retourne le poids de la variation <i>variation</i> pour la
	 * transformation d'index <i>index</i>
	 * 
	 * @param index
	 *            L'index de la transformation
	 * @param variation
	 *            La variation dont on veut récupérer le poids
	 * @return Le poids demandé
	 * 
	 * @throws IllegalArgumentException
	 *             Si l'index n'est pas valide
	 */
	public double variationWeight(int index, Variation variation) {
		return m_builder.getVariationWeight(index, variation);
	}
	
	/**
	 * Modifie le poids de la variation <i>variation</i> pour la
	 * transformation d'index <i>index</i>
	 * 
	 * @param index
	 *            L'index de la transformation
	 * @param variation
	 *            La variation dont on veut modifier le poids
	 * @param newWeight
	 *            Le nouveau poids à affecter à la variation, pour cette
	 *            transformation
	 * @throws IllegalArgumentException
	 *             Si l'index n'est pas valide
	 */
	public void setVariationWeight(int index, Variation variation,
			double newWeight) {
		
		m_builder.setVariationWeight(index, variation, newWeight);
		
		notifyObservers();
	}
	
	/**
	 * Supprime le bâtisseur de transformation flame à l'index <i>index</i>
	 * 
	 * @param index
	 * @throws IllegalArgumentException
	 *             Si l'index n'est pas valide
	 */
	public void removeTransformation(int index) {
		m_builder.removeTransformation(index);
		
		notifyObservers();
	}
	
	/**
	 * Change la stratégie de calcul de la fractale
	 * @param strategy
	 * 		Nouvelle stratégie à utiliser.
	 */
	public void setComputeStrategy(FlameFactory s){
		m_builder.setFactory(s);
		
		notifyObservers();
	}
	
	public FlameFactory getComputeStrategy(){
		return m_builder.getFactory();
	}
	
	/**
	 * Construit une fractale Flame à partir des informations récoltées
	 * @return La fractale Flame construite
	 */
	public Flame build() {
		return m_builder.build();
	}
	
	public interface Listener {
		
		/**
		 * Appelée quand un ObservableFlameBuilder que cet objet observe est modifié.
		 * @param b bâtisseur modifié (ce paramètre permet à une même instance d'observer plusieurs bâtisseurs)
		 */
		void onFlameBuilderChange(ObservableFlameBuilder b);
	}
}
