/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.flame;

import java.awt.image.BufferedImage;

import ch.epfl.flamemaker.FlameSet;

/**
 *	Classe utilitaire
 */
public class FlameUtils {
	
	/**
	 * @param accumulator
	 *            L'accumulateur de la fractale
	 * @param set
	 *            Le FlameSet à utiliser
	 * @return Une BufferedImage contenant la représentation graphique de la
	 *         fractale
	 */
	public static BufferedImage generateBufferedImage(FlameAccumulator accumulator, FlameSet set) {
		BufferedImage img = new BufferedImage(accumulator.width(),
				accumulator.height(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < accumulator.width(); x++) {
			for (int y = 0; y < accumulator.height(); y++) {
				// On met à jour la couleur du pixel courant
				img.setRGB(x, accumulator.height() - y -1, accumulator.color(set.getPalette(), set.getBackgroundColor(), x, y).asPackedRGB());
			}
		}
		
		return img;
	}
}
