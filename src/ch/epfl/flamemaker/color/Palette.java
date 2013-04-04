package ch.epfl.flamemaker.color;


/**
 * Interface décrivant le comportement d'une palette de couleur. Une palette
 * représente un ensemble de couleurs correspondant à un index compris entre 0
 * et 1 inclus. L'unique méthode permettant de récupérer une couleur pour un
 * index donné est colorForIndex().
 */
public interface Palette {

	/**
	 * Récupère la couleur de la palette pour un index donné
	 * 
	 * @param index
	 *            correspondant à la couleur voulue compris entre 0 et 1
	 * @return instance de la couleur correspondant à l'index
	 * @throws IllegalArgumentException
	 *             si l'index n'est pas dans l'intervalle [0, 1]
	 */
	Color colorForIndex(double index);

}
