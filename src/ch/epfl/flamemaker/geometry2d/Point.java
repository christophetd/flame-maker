package ch.epfl.flamemaker.geometry2d;

/**
 * Classe non mutable représentant un point dans l'espace à deux dimentions.<br />
 * Ses coordonnées cartésiennes et polaires peuvent être obtenues en virgule
 * flottante avec les getters {@link #x()}, {@link #y()}, {@link #r()} et
 * {@link #theta()}
 */
public class Point {

	public static final Point ORIGIN = new Point(0, 0);

	private double m_x, m_y;

	/**
	 * Construit un point de coordonnées carthésiennes (x, y)
	 */
	public Point(double x, double y) {
		m_x = x;
		m_y = y;
	}

	/**
	 * Retourne l'abscisse des coordonnées carthésiennes du point.
	 */
	public double x() {
		return m_x;
	}

	/**
	 * Retourne l'ordonnée des coordonnées carthésiennes du point.
	 */
	public double y() {
		return m_y;
	}

	/**
	 * Retourne le rayon des coordonnées polaires du point.
	 */
	public double r() {
		return Math.sqrt(m_x * m_x + m_y * m_y);
	}

	/**
	 * Retourne la composante &theta; des coordonnées polaires du point.
	 */
	public double theta() {
		return Math.atan2(m_y, m_x);
	}

	public String toString() {
		return "(" + m_x + "," + m_y + ")";
	}
}
