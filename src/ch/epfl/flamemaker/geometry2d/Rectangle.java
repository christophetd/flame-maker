package ch.epfl.flamemaker.geometry2d;

/**
 * Classe non mutable représentant un rectangle dans l'espace à deux dimentions.<br />
 * Le rectangle est caractérisé par son centre ({@link Point Point}), sa largeur
 * et sa hauteur (double).
 */
public class Rectangle {
	/**
	 * Le centre du rectangle
	 */
	private Point m_center;

	/**
	 * La largeur du rectangle
	 */
	private double m_width;

	/**
	 * La hauteur du rectangle
	 */
	private double m_height;

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
	public Rectangle(Point center, double width, double height) {
		if (width < 0 || height < 0) {
			throw new IllegalArgumentException(
					"La largeur et la hauteur du rectangle doivent etre strictement positives");
		}

		m_center = center;
		m_width = width;
		m_height = height;
	}

	/**
	 * @return La plus petite coordonnée horizontale du rectangle.
	 */
	public double left() {
		return m_center.x() - (m_width / 2.0);
	}

	/**
	 * @return La plus grande coordonnée horizontale du rectangle.
	 */
	public double right() {
		return m_center.x() + (m_width / 2.0);
	}

	/**
	 * @return La plus petite coordonnée verticale du rectangle
	 */
	public double bottom() {
		return m_center.y() - (m_height / 2.0);
	}

	/**
	 * Retourne la plus grande coordonnée verticale du rectangle
	 */
	public double top() {
		return m_center.y() + (m_height / 2.0);
	}

	/**
	 * @return La largeur du rectangle
	 */
	public double width() {
		return m_width;
	}

	/**
	 * @return La hauteur du rectangle
	 */
	public double height() {
		return m_height;
	}

	/**
	 * @return Le centre du rectangle
	 */
	public Point center() {
		return m_center;
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
	 * Calcule l'aire du rectangle et la retourne
	 * @return
	 * 		largeur * hauteur
	 */
	public double area(){
		return m_height * m_width;
	}

	/**
	 * @return Le rapport largeur/hauteur du rectangle
	 */
	public double aspectRatio() {
		return (m_width / m_height);
	}

	
	public boolean equals(Rectangle other){
		return other.m_width == m_width && other.m_height == m_height 
				&& other.m_center.x() == m_center.x() && other.m_center.y() == m_center.y();
	}
	
	public int hashCode(){
		return ((((((int)m_width << 8) ^ (int)m_height) << 8) ^ (int)m_center.x()) << 8) ^ (int)m_center.y();
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
		if (aspectRatio <= 0) {
			throw new IllegalArgumentException(
					"Le nouveau ratio du rectangle doit etre strictement positif (donné : "+aspectRatio+")");
		}

		/*
		 * Le nouveau rectangle doit contenir entièrement l'ancien : on prend
		 * donc le maximum entre la largeur / hauteur calculée selon le ratio
		 * donné et la largeur / hauteur actuelle, pour s'assurer de cela
		 */
		double newWidth = Math.max(m_width, m_height * aspectRatio);
		double newHeight = Math.max(m_height, m_width / aspectRatio);

		return new Rectangle(m_center, newWidth, newHeight);
	}

	public String toString() {
		String output = "(";
		output += m_center.toString();
		output += ", " + m_width + ", " + m_height + ")";

		return output;
	}
}
