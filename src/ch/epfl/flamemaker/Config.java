/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker;

/**
 * Variables de configuration globales.
 */
public class Config {
	
	/**
	 * Nombre maximum de transformations pour une fractale. Certaines techniques de rendu peuvent en
	 * avoir besoin si elles évoluent avec une quantité de mémoire définie.
	 */
	public static final int MAX_TRANSFO_COUNT = 16;
	
	private Config(){};
}
