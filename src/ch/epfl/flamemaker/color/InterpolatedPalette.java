/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.color;

import java.util.List;

/**
 * Classe non mutable représentant une palette interpolante. Les couleurs
 * fournies dans le constructeur sont réparties uniformément sur l'espace des
 * valeurs entre 0 et 1. La méthode colorForIndex récupère alors la couleur
 * correspondant à l'interpolation des deux couleurs adjacentes, pondérée par la
 * distance séparant l'index de chaque couleur.
 * 
 */
public class InterpolatedPalette implements Palette {

	private static final long serialVersionUID = -78786374427341248L;
	
	// Liste des couleurs réparties uniformément entre 0 et 1 dans l'ordre de
	// cette liste.
	private List<Color> m_colors;

	/**
	 * Crée une palette interpolante avec la liste de couleurs passée en
	 * paramètre. La liste doit contenir au minimum 2 couleurs.
	 * 
	 * @param colors
	 *            liste des couleurs à répartir entre 0 et 1
	 * @throws IllegalArgumentException
	 *             quand la liste contient moins de deux couleurs.
	 */
	public InterpolatedPalette(List<Color> colors) {
		if (colors.size() < 2) {
			throw new IllegalArgumentException(
					"Une palette interpolante doit avoir au moins 2 couleurs");
		}
		m_colors = colors;
	}

	/**
	 * Calcule la couleur à l'index index par interpolation des couleurs de la
	 * liste
	 * 
	 * @param index
	 *            : Index de la couleur à récupérer
	 * @return La couleur correspondant à l'index.
	 * @throws IllegalArgumentException
	 *             lorsque l'index n'est pas dans l'intervalle [0, 1]
	 */
	@Override
	public Color colorForIndex(double index) {
		if (index < 0 || index > 1) {
			throw new IllegalArgumentException("L'index doit être entre 0 et 1");
		}

		int nbColors = m_colors.size() - 1;

		double lowColor = Math.floor(nbColors * index);
		double highColorWeight = index * nbColors - lowColor;
		double highColor = lowColor + 1;

		// Cas particulier quand index = 1.0
		if (highColor == m_colors.size()) {
			return m_colors.get(m_colors.size() - 1);
		}
		return m_colors.get((int) highColor).mixWith(
				m_colors.get((int) lowColor), highColorWeight);
	}

}
