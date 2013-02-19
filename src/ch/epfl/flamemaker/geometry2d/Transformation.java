package ch.epfl.flamemaker.geometry2d;

/**
 * Interface représentant une transformation géométrique de R<sup>2</sup> dans R<sup>2</sup>.<br/>
 * La transformation agit sur des instances de {@link Point Point}.
 */
public interface Transformation {

	/**
	 * Retourne le point p transformé par cette application.
	 * @param p point à transformer
	 * @return transformation de p par cette application 
	 */
	Point transformPoint(Point p);
}
