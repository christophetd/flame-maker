package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * Classe modélisant une transformation de type Flame
 */
public class FlameTransformation implements Transformation {

	/**
	 * La composante affine de la transformation
	 */
	private final AffineTransformation m_affineTransfo;

	/**
	 * Un tableau contenant les poids des différentes variations
	 */
	private final double[] m_weight;

	/**
	 * Construit une transformation de type Flame à partir de sa composante
	 * affine et des poids des différentes variations
	 * 
	 * @param affineTransformation
	 *            La composante affine
	 * @param variationWeight
	 *            Les poids des différentes variations
	 * @throws IllegalArgumentException
	 *             Si le tableau des poids des différentes variations ne
	 *             contient pas 6 éléments
	 */
	public FlameTransformation(AffineTransformation affineTransformation,
			double[] variationWeight) {
		if (variationWeight.length != 6) {
			throw new IllegalArgumentException(
					"variationWeight must have length 6");
		}

		m_affineTransfo = affineTransformation;
		m_weight = variationWeight.clone();
	}

	/**
	 * @see ch.epfl.flamemaker.geometry2d.Transformation#transformPoint(ch.epfl.flamemaker.geometry2d.Point)
	 */
	@Override
	public Point transformPoint(Point p) {

		Point tmp, ret = new Point(0, 0);

		for (int i = 0; i < Variation.ALL_VARIATIONS.size(); i++) {
			if (m_weight[i] != 0) {
				tmp = Variation.ALL_VARIATIONS.get(i).transformPoint(
						m_affineTransfo.transformPoint(p));
				ret = new Point(ret.x() + m_weight[i] * tmp.x(), ret.y()
						+ m_weight[i] * tmp.y());
			}
		}

		return ret;
	}

	/**
	 * @return La composante affine de la transformation
	 */
	public AffineTransformation affineTransformation() {
		return new AffineTransformation(m_affineTransfo);
	}

	/**
	 * Retourne le poids de la variation concernée pour la transformation
	 * courante
	 * 
	 * @param variation
	 *            La variation dont on veut connaître le poids
	 * @return Le poids de la variation passée en paramètre
	 * @throws IllegalArgumentException
	 *             Si la variation a un index invalide
	 */
	public double weight(Variation variation) {
		int index = variation.index();
		if (index > 0 || index < m_weight.length) {
			throw new IllegalArgumentException("invalid index given");
		}
		return m_weight[index];
	}

	/**
	 * @return Le tableau des poids des différentes variations
	 */
	public double[] weights() {
		return m_weight.clone();
	}
}
