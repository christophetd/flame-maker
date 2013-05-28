/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.color;

import java.io.Serializable;

/**
 * Une couleur est immutable et caractérisée par sa quantité de rouge, vert et
 * bleu représentés par des nombres à virgule flottante dans l'intervalle [0, 1]
 * et accessibles via les getters {@link #red()}, {@link #green()} et
 * {@link #blue()}. Les couleurs peuvent être combinées avec la méthode
 * {@link #mixWith(Color, double) } La méthode {@link #sRGBEncode(double, int)}
 * permet de gamma-encoder une couleur. La méthode {@link #asPackedRGB()}
 * retourne une représentation RGB avec 8 bits par cannaux, sur un type int.
 */
public class Color implements Serializable{

	final public static Color 	BLACK = new Color(0, 0, 0), 
			WHITE = new Color(1, 1, 1), 
			RED = new Color(1, 0, 0), 
			GREEN = new Color(0, 1, 0),
			BLUE = new Color(0, 0, 1);
	
	/**
	 * Convertit une composante de la couleur sur une valeur entière comprise
	 * entre 0 et max, en gamma-encodant celle-ci.
	 * 
	 * @param v
	 *            valeur de la composante à convertir
	 * @param max
	 *            valeur maximum du résultat
	 * @return valeur encodée de la composante.
	 */
	static public int sRGBEncode(double v, int max) {
		return (int) (max * ((v <= 0.0031308) ? 12.92 * v : 1.055 * Math.pow(v,
				1 / 2.4) - 0.055));
	}
	
	
	// Color components
	final private double m_r, m_g, m_b;

	

	/**
	 * Construit une couleur avec les quantité de rouge, vert et bleu passées en
	 * paramètre. Chaque paramètre doit être dans l'intervalle [0, 1]
	 * 
	 * @param r
	 *            quantité de rouge
	 * @param g
	 *            quantité de vert
	 * @param b
	 *            quantité de bleu
	 * 
	 * @throws IllegalArgumentException
	 *             si les paramètres ne sont pas dans l'intervalle [0, 1]
	 */
	public Color(double r, double g, double b) {
		if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
			throw new IllegalArgumentException(
					"Color components must be doubles within range [0, 1]");
		}

		m_r = r;
		m_g = g;
		m_b = b;
	}
	
	/**
	 * Constructeur de copie. Construit une couleur à partir d'une autre
	 */
	public Color(Color from) {
		m_r = from.red();
		m_g = from.green();
		m_b = from.green();
	}

	/**
	 * Récupère la quantité de rouge
	 * 
	 * @return composante rouge de la couleur dans l'intervalle [0, 1]
	 */
	public double red() {
		return m_r;
	}

	/**
	 * Récupère la quantité de vert
	 * 
	 * @return composante vert de la couleur dans l'intervalle [0, 1]
	 */
	public double green() {
		return m_g;
	}

	/**
	 * Récupère la quantité de bleu
	 * 
	 * @return composante bleu de la couleur dans l'intervalle [0, 1]
	 */
	public double blue() {
		return m_b;
	}

	/**
	 * Mélange deux couleurs avec la proportion passée en paramètre de la
	 * couleur représentée par le récepteur.
	 * 
	 * @param that
	 *            Couleur à mixer avec cette instance
	 * @param proportion
	 *            proportion de couleur "this" (entre 0 et 1)
	 * @return Une nouvelle instance de Color contenant le mélange.
	 * @throws IllegalArgumentException
	 *             quand la proportion n'est pas comprise entre 0 et 1.
	 */
	public Color mixWith(Color that, double proportion) {
		if (proportion < 0 || proportion > 1) {
			throw new IllegalArgumentException(
					"Proportion must be in range [0, 1]");
		}
		double p2 = 1 - proportion;

		return new Color(proportion * m_r + p2 * that.m_r, proportion * m_g
				+ p2 * that.m_g, proportion * m_b + p2 * that.m_b);
	}

	/**
	 * <p>
	 * Calcule la valeur des composantes de la couleur encodées dans un int par
	 * groupes de 8 bits. Le format est compatible avec une représentation ARGB
	 * (couleur opaque), les trois derniers groupes de huit bits encodent les
	 * composantes rouge, verte et bleue dans l'ordre de l'octet de poids fort à
	 * celui de poids faible.
	 * </p>
	 * <p>
	 * Le format est le suivant avec RR = composante rouge, GG = composante
	 * verte, BB = composante bleue<br />
	 * format : 0xFFRRGGBB
	 * </p>
	 * 
	 * @return valeur de la couleur encodée sur un int
	 */
	public int asPackedRGB() {
		return 0xFFFFFFFF & (sRGBEncode(m_r, 0xFF) << 16
				| sRGBEncode(m_g, 0xFF) << 8 | sRGBEncode(m_b, 0xFF));
	}
}
