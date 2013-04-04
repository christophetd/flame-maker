package ch.epfl.flamemaker.geometry2d;

/**
 * Interface représentant une transformation géométrique de R<sup>2</sup> dans
 * R<sup>2</sup>.<br/>
 * La transformation agit sur des instances de {@link Point Point}.
 */
public interface Transformation {

	/**
	 * Retourne le point p transformé par cette transformation.
	 * 
	 * @param p
	 *            Le point à transformer
	 * @return Le résultat de l'application de la transformation au point
	 */
	Point transformPoint(Point p);
}
