package ch.epfl.flamemaker.geometry2d;

/**
 * Classe non mutable représentant un rectangle dans l'espace à deux dimentions.<br />
 * Le rectangle est caractérisé par son centre ({@link Point Point}), sa largeur et sa hauteur (double).
 */
public class Rectangle {
	private Point m_center;
	private double m_width;
	private double m_height;
	
	/**
	 * Construit un rectangle de centre center, de largeur width et de hauteur height.
	 */
	public Rectangle(Point center, double width, double height) {
		if(width < 0 || height < 0) {
			throw new IllegalArgumentException("Width and height must be greater than zero");
		}
		
		m_center = center;
		m_width  = width;
		m_height = height;
	}
	
	/**
	 * retourne la plus petite coordonnée x du rectangle.
	 */
	public double left() {
		return m_center.x() - (m_width / 2.0);
	}
	
	/**
	 * retourne la plus grande coordonnée x du rectangle.
	 */
	public double right() {
		return m_center.x() + (m_width / 2.0);
	}
	
	/**
	 * retourne la plus petite coordonnée y du rectangle
	 */
	public double bottom() {
		return m_center.y() - (m_height / 2.0);
	}
	
	/**
	 * retourne la plus grande coordonnée y du rectangle
	 */
	public double top() {
		return m_center.y() + (m_height / 2.0);
	}
	
	/**
	 * retourne la largeur du rectangle
	 */
	public double width() {
		return m_width;
	}
	
	/**
	 * Retourne la hauteur du rectangle
	 */
	public double height() {
		return m_height;
	}

	/**
	 * Retourne le point au centre du rectangle
	 */
	public Point center() {
		return m_center;
	}
	
	/**
	 * Teste si un point p appartient au rectangle. Le point appartient au rectangle ssi x >= left, x < right, y > top et y < bottom
	 * @return true si p appartient au rectangle.
	 */
	public boolean contains(Point p) {
		return (p.x() >= this.left()   && p.x() < this.right())
			&& (p.y() >= this.bottom() && p.y() < this.top());
	}
	
	/**
	 * Retourne le rapport largeur/hauteur
	 */
	public double aspectRatio() {
		return (m_width / m_height);
	}
	
	/**
	 * retourne le plus petit rectangle ayant le même centre que le récepteur, 
	 * le rapport largeur/hauteur aspectRatio et contenant totalement le récepteur 
	 * (c-à-d que tout point contenu dans le récepteur est également contenu dans le 
	 * rectangle retourné). Lève l'exception IllegalArgumentException si le rapport 
	 * passé est négatif ou nul.
	 */
	public Rectangle expandToAspectRatio(double aspectRatio) {
		if(aspectRatio <= 0) {
			throw new IllegalArgumentException("Aspect ratio must be greater than zero");
		}
		
		/*
		 Le nouveau rectangle doit contenir entièrement l'ancien : on prend donc le maximum entre
		 la largeur / hauteur calculée selon le ratio donné et la largeur / hauteur actuelle, 
		 pour s'assurer de cela
		*/
		double newWidth  = Math.max(m_width, m_height * aspectRatio);
		double newHeight = Math.max(m_height, m_width / aspectRatio);
		
		// Test unitaire : newWidth / newHeight == aspectRatio
		
		return new Rectangle(m_center, newWidth, newHeight);
	}
	
	public String toString() {
		String output = "(";
		output += m_center.toString();
		output += ", " + m_width + ", " + m_height+")";
		
		return output;
	}
}
