/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

/**
 * Classe modélisant une variation
 */
public enum Variations implements Transformation {
	LINEAR(0, "Linear"){
		public Point transformPoint(Point p) {
			return p;
		}
	},
	SINUSOIDAL(1, "Sinusoidal") {
		public Point transformPoint(Point p) {
			return new Point(Math.sin(p.x()), Math.sin(p.y()));
		}
	},
	SPHERICAL(2, "Spherical") {
		public Point transformPoint(Point p) {
			return new Point(p.x() / (p.r() * p.r()), p.y()
					/ (p.r() * p.r()));
		}
	},
	SWIRL(3, "Swirl") {
		public Point transformPoint(Point p) {
			return new Point(p.x() * Math.sin(p.r() * p.r()) - p.y()
					* Math.cos(p.r() * p.r()), p.x()
					* Math.cos(p.r() * p.r()) + p.y()
					* Math.sin(p.r() * p.r()));
		}
	}, 
	HORSESHOE(4, "Horseshoe") {
		public Point transformPoint(Point p) {
			return new Point((p.x() - p.y()) * (p.x() + p.y()) / p.r(),
					(2 * p.x() * p.y()) / p.r());
		}
	},
	BUBBLE(5, "Bubble") {
		public Point transformPoint(Point p) {
			return new Point(4 * p.x() / (p.r() * p.r() + 4), 4 * p.y()
					/ (p.r() * p.r() + 4));
		}
	}
	;
	/*
	 * L'index de la variation
	 */
	private final int m_index;

	/**
	 * Le nom de la variation
	 */
	private final String m_name;

	/**
	 * Construit une nouvelle variation à partir de son index dans la liste des
	 * variations prédéfinies et de son nom
	 * 
	 * @param index
	 *            L'index de la variation
	 * @param name
	 *            Le nom de la variation
	 */
	private Variations(int index, String name) {
		m_index = index;
		m_name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.epfl.flamemaker.geometry2d.Transformation#transformPoint(ch.epfl.
	 * flamemaker.geometry2d.Point)
	 */
	abstract public Point transformPoint(Point p);

	/**
	 * @return Le nom affichable de la variation
	 */
	public String printableName() {
		return m_name;
	}

	/**
	 * @return L'index associé à la variation
	 */
	public int index() {
		return m_index;
	}
}
