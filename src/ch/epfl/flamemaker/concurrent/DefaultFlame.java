package ch.epfl.flamemaker.concurrent;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class DefaultFlame extends Flame {

	public DefaultFlame(List<FlameTransformation> transforms) {
		super(transforms);
		System.out.println("Constructed");
	}

	@Override
	protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
			final int density) {
		// On initialise un random déterminé par la graine 2013
		Random randomizer = new Random(2013);
		
		List<FlameTransformation> transformations = this.getTransforms();

		// Création des variables utilisées dans les boucles de calcul
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum;

		// On récupère une fois pour toutes la taille de la liste de
		// transformations
		int size = transformations.size();

		// Création du builder
		FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame,
				width, height);

		// Garde en mémoire la couleur du dernier point accumulé
		double lastColor = 0;

		// 20 premières itérations dans le vide pour l'algorithme du chaos
		for (int i = 0; i < k; i++) {
			transformationNum = randomizer.nextInt(size);
			point = transformations.get(transformationNum).transformPoint(point);
			lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
		}

		// Iterations accumulées pour le rendu
		int m = density * width * height;
		for (int i = 0; i < m && !isAborted() ; i++) {
			transformationNum = randomizer.nextInt(size);
			point = transformations.get(transformationNum).transformPoint(point);
			lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;

			builder.hit(point, lastColor);
		}

		// On construit l'accumulateur
		return builder.build();
	}
	
	/**
	 * @param index
	 *            L'index de la transformation de laquelle on désire avoir
	 *            l'index de couleur
	 * @return L'index de couleur associé à la transformation
	 */
	private double getColorIndex(int index) {

		if (index >= 2) {
			double denominateur = Math.pow(2,
					Math.ceil(Math.log(index) / Math.log(2)));

			return ((2 * index - 1) % denominateur) / denominateur;
		} else {
			return index;
		}
	}
}
