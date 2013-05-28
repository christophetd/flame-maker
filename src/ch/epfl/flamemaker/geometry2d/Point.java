/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.geometry2d;

import java.io.Serializable;


/**
 * Classe non mutable représentant un point dans l'espace à deux dimensions.<br />
 * Ses coordonnées cartésiennes et polaires peuvent être obtenues en virgule
 * flottante avec les getters {@link #x()}, {@link #y()}, {@link #r()} et
 * {@link #theta()}
 */
public class Point implements Serializable {

	/**
	 * L'origine du repère, définie comme le point de coordonnées cartésiennes
	 * (0;0)
	 */
	final public static Point ORIGIN = new Point(0, 0);

	/**
	 * Les coordonnées cartésiennes du point
	 */
	final private double m_x, m_y;

	/**
	 * Crée un point à partir de ses coordonnées cartésiennes
	 * 
	 * @param x
	 *            Abscisse du point
	 * @param y
	 *            Ordonnée du point
	 */
	public Point(double x, double y) {
		m_x = x;
		m_y = y;
	}

	/**
	 * @return L'abscisse du point.
	 */
	public double x() {
		return m_x;
	}

	/**
	 * @return L'ordonnée du point.
	 */
	public double y() {
		return m_y;
	}

	/**
	 * @return La distance à l'origine du point (coordonnée polaire r)
	 */
	public double r() {
		return Math.sqrt(m_x * m_x + m_y * m_y);
	}

	/**
	 * @return L'angle du point par rapport à l'axe des abscisses (coordonnée
	 *         polaire &theta;)
	 */
	public double theta() {
		return Math.atan2(m_y, m_x);
	}

	@Override
	public String toString() {
		return "(" + m_x + "," + m_y + ")";
	}
}
