package ch.epfl.flamemaker.geometry2d;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Classe mutable et observable représentant un rectangle dans l'espace à deux dimentions.<br />
 * Le rectangle est caractérisé par son centre ({@link Point Point}), sa largeur
 * et sa hauteur (double).
 */
public class ObservableRectangle {
	/**
	 * Le centre du rectangle
	 */
	private Rectangle m_rect;
	
	/**
	 * Observateurs du rectangle
	 */
	private Set<Listener> m_listeners = new HashSet<Listener>();

	/**
	 * Construit un rectangle de centre, largeur et hauteur passés en paramètres
	 * 
	 * @param center
	 *            Le centre du rectangle
	 * @param width
	 *            La largeur du rectangle
	 * @param height
	 *            La hauteur du rectangle
	 * @throws IllegalArgumentException
	 *             Si la hauteur ou la largeur est nulle ou négative
	 */
	public ObservableRectangle(Point center, double width, double height) {
		m_rect = new Rectangle(center, width, height);
	}
	
	//TODO javadoc
	public ObservableRectangle(Rectangle rect){
		m_rect = rect;
	}

	/**
	 * @return La plus petite coordonnée horizontale du rectangle.
	 */
	public double left() {
		return m_rect.left();
	}

	/**
	 * @return La plus grande coordonnée horizontale du rectangle.
	 */
	public double right() {
		return m_rect.right();
	}

	/**
	 * @return La plus petite coordonnée verticale du rectangle
	 */
	public double bottom() {
		return m_rect.bottom();
	}

	/**
	 * Retourne la plus grande coordonnée verticale du rectangle
	 */
	public double top() {
		return m_rect.top();
	}

	/**
	 * @return La largeur du rectangle
	 */
	public double width() {
		return m_rect.width();
	}

	/**
	 * @return La hauteur du rectangle
	 */
	public double height() {
		return m_rect.height();
	}

	/**
	 * @return Le centre du rectangle
	 */
	public Point center() {
		return m_rect.center();
	}

	/**
	 * Change la largeur du rectangle et informe les observateurs
	 */
	public void setWidth(double width) {
		m_rect = new Rectangle(m_rect.center(), width, m_rect.height());
		notifyObservers();
	}

	/**
	 * Change la hauteur du rectangle et informe les observateurs
	 */
	public void setHeight(double height) {
		m_rect = new Rectangle(m_rect.center(), m_rect.width(), height);
		notifyObservers();
	}

	/**
	 * Change le center du rectangle et informe les observateurs
	 */
	public void setCenter(Point p) {
		m_rect = new Rectangle(p, m_rect.width(), m_rect.height());
		notifyObservers();
	}
	
	/**
	 * Modifie la taille du rectangle et informe les observateurs
	 * @param width nouvelle largeur
	 * @param height nouvelle hauteur
	 */
	public void setSize(double width, double height){
		m_rect = new Rectangle(m_rect.center(), width, height);
		notifyObservers();
	}
	
	/**
	 * Change toutes les propriétés pour celles passées en paramètre
	 * @param rect
	 * 		Rectangle à recopier
	 */
	public void set(Rectangle rect){
		m_rect = rect;
		notifyObservers();
	}

	/**
	 * Teste si un point p appartient au rectangle. Un point est défini comme
	 * appartenant au rectangle :
	 * <ul>
	 * <li>si sa coordonnée horizontale est supérieure ou égale à la plus petite
	 * coordonnée horizontale du rectangle ;</li>
	 * <li>et si sa coordonnée horizontale est strictement inférieure à la plus
	 * grande coordonnée horizontale du rectangle ;</li>
	 * <li>et si sa coordonnée verticale est supérieure ou égale à la plus
	 * petite coordonnée verticale du rectangle ;</li>
	 * <li>et si sa coordonnée verticale est strictement inférieure à la plus
	 * grande coordonnée verticale du rectangle.</li>
	 * </ul>
	 * 
	 * @param p
	 *            Le point à tester
	 * @return true si p appartient au rectangle.
	 */
	public boolean contains(Point p) {
		return (p.x() >= this.left() && p.x() < this.right())
				&& (p.y() >= this.bottom() && p.y() < this.top());
	}

	/**
	 * @return Le rapport largeur/hauteur du rectangle
	 */
	public double aspectRatio() {
		return m_rect.aspectRatio();
	}

	/**
	 * Construit le plus petit rectangle ayant le même centre que le récepteur,
	 * le rapport largeur/hauteur <i>aspectRatio</i> et contenant totalement le
	 * récepteur (Tout point contenu dans le récepteur est également contenu
	 * dans le rectangle retourné)
	 * 
	 * @param aspectRatio
	 *            Le nouveau rapport largeur / hauteur
	 * @return Le rectangle résultant de la fonction
	 * @throws IllegalArgumentException
	 *             Si <i>aspectRation</i> est nul ou négatif
	 */
	public Rectangle expandToAspectRatio(double aspectRatio) {
		return m_rect.expandToAspectRatio(aspectRatio);
	}

	public String toString() {
		return m_rect.toString();
	}
	
	/**
	 * Ajoute un observateur au rectangle. Les observateurs sont notifiés à chaque changement du 
	 * rectangle par l'appel de leur méthode {@link #Listener.onRectangleChange()}
	 * 
	 * @param l observateur  à ajouter
	 */
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	/**
	 * Retourne une instance de Rectangle ayant les même propriétés que cet ObservableRectangle.
	 * @return
	 * 		Une instance de la classe Rectangle
	 */
	public Rectangle toRectangle(){
		return m_rect;
	}
	
	/**
	 * Supprime un observateur si il a été ajouté précédemment. Celui-ci ne sera plus notifié des modifications sur le rectangle.
	 * 
	 * @param l observateur à supprimer
	 */
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	private void notifyObservers(){
		Iterator<Listener> it = m_listeners.iterator();
		while(it.hasNext()){
			it.next().onRectangleChange(this);
		}
	}
	
	public interface Listener {
		
		/**
		 * Méthode appellée dès que le rectangle observé subit une modification
		 * @param rect
		 * 		Rectangle observable modifié
		 */
		public void onRectangleChange(ObservableRectangle rect);
	}
}
