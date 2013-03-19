package ch.epfl.flamemaker.color;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.InterpolatedPalette;
import ch.epfl.flamemaker.Palette;

/**
 * Palette contenant un nombre donné de couleurs aléatoires. Le nombre de couleurs est passé 
 * au constructeur et la couleur récupérée avec {@link #colorForIndex(double)} est une interpolation
 * des couleurs définies de façon aléatoire à la construction.
 */
public class RandomPalette implements Palette{

	InterpolatedPalette m_palette;

	/**
	 * Construit une nouvelle palette aléatoire avec le nombre de couleurs spécifié
	 * @param nbColors nombre de couleurs choisies aléatoirement
	 */
	public RandomPalette(int nbColors){
		if(nbColors < 2)
			throw new IllegalArgumentException("Must have at least 2 colors");

		List<Color> colors = new ArrayList<Color>();

		for(int i = 0 ; i < nbColors ; i++){
			colors.add(new Color(Math.random(), Math.random(), Math.random()));
		}

		m_palette = new InterpolatedPalette(colors);
	}

	/**
	 * Calcule une interpolation des couleurs sur la palette aléatoire.
	 * @return couleur calculée
	 * @throws IllegalArgumentException lorsque l'index n'est pas dans l'intervalle [0, 1]
	 */
	@Override
	public Color colorForIndex(double index) {
		return m_palette.colorForIndex(index);
	}

}
