package ch.epfl.flamemaker.flame;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Point;

/**
 * Classe modélisant une fractale de type Flame
 */
public class Flame {
	/**
	 * Contient la liste des transformations caractérisant la fractale
	 */
	final private List<FlameTransformation> m_transforms;

	/**
	 * Construit une nouvelle fractale à partir d'une liste de transformation la caractérisant
	 * @param transforms La liste des transformation
	 */
	public Flame(List<FlameTransformation> transforms) {
		m_transforms = transforms;
	}

	/**
	 * @param frame La région du plan dans laquelle calculer la fractale
	 * @param width La largeur de l'accumulateur à générer
	 * @param height La hauteur de l'accumulateur à générer
	 * @param density La densité utilisée pour générer les points de la fractale (influe sur le nombre de points calculés par l'algorithme)
	 * @return L'accumulateur contenant les points de la fractale
	 */
	public FlameAccumulator compute(Rectangle frame, int width, int height,
			int density) {
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum = 0;
		Random randomizer = new Random(2013);
		int size = m_transforms.size();
		FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame,
				width, height);
		double lastColor = 0;

		for (int i = 0; i < k; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
			lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
		}

		int m = density * width * height;
		for (int i = 0; i < m; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
			lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;

			builder.hit(point, lastColor);
		}

		return builder.build();
	}

	/**
	 * @param index L'index de la transformation de laquelle on désire avoir l'index de couleur
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

	/**
	 * Classe modélisant un bâtisseur pour une fractale Flame
	 */
	public static class Builder {
		/**
		 * La fractale 
		 */
		Flame m_flame;

		/**
		 * Construit un bâtisseur à partir d'une fractale existante
		 * @param flame La fractale flame
		 */
		public Builder(Flame flame) {
			m_flame = flame;
		}

		/**
		 * @return Le nombre de transformations de la fractale
		 */
		public int transformationsCount() {
			return m_flame.m_transforms.size();
		}

		/**
		 * Ajoute une transformation de type flame à la fractale
		 * @param transformation La transformation
		 */
		public void addTransformation(FlameTransformation transformation) {
			m_flame.m_transforms.add(transformation);
		}

		/**
		 * @param index
		 * @return La transformation de la fractale d'index <i>index</i>
		 */
		public AffineTransformation affineTransformation(int index) {
			checkIndex(index);
			return m_flame.m_transforms.get(index).affineTransformation();
		}

		/**
		 * Remplace la transformation à l'index <i>index</i> par <i>newTransformation</i>
		 * @param index
		 * @param newTransformation
		 */
		public void setAffineTransformation(int index,
				AffineTransformation newTransformation) {
			checkIndex(index);
			FlameTransformation transformation = m_flame.m_transforms
					.get(index);
			m_flame.m_transforms.set(index, new FlameTransformation(
					newTransformation, transformation.weights()));
		}

		/**
		 * @param index L'index de la transformation
		 * @param variation La variation dont on veut récupérer le poids
		 * @return Le poids de la variation <i>variation</i> pour la transformation d'index <i>index</i>
		 */
		public double variationWeight(int index, Variation variation) {
			checkIndex(index);
			return m_flame.m_transforms.get(index).weight(variation);
		}

		/**
		 * Modifie le poids de la variation <i>variation</i> pour la transformation d'index <i>index</i>
		 * @param index L'index de la transformation
		 * @param variation  La variation dont on veut modifier le poids
		 * @param newWeight Le nouveau poids à affecter à la variation, pour cette transformation
		 */
		public void setVariationWeight(int index, Variation variation,
				double newWeight) {
			checkIndex(index);
			FlameTransformation transformation = m_flame.m_transforms
					.get(index);
			double[] weights = transformation.weights();
			int weightIndex = variation.index();

			if (weightIndex > 0 && weightIndex < weights.length) {
				weights[weightIndex] = newWeight;
			}

			m_flame.m_transforms.set(index, new FlameTransformation(
					transformation.affineTransformation(), weights));
		}

		/**
		 * Supprime la transformation à l'index <i>index</i>
		 * @param index
		 */
		public void removeTransformation(int index) {
			checkIndex(index);
			m_flame.m_transforms.remove(index);
		}

		/**
		 * @return Une fractale Flame à partir des informations récoltées
		 */
		public Flame build() {
			return new Flame(m_flame.m_transforms);
		}

		/**
		 * Vérifie si l'index passé en argument est valide pour la liste des transformations
		 * @param index L'index à vérifier
		 * @throws IllegalArgumentException Si l'index n'est pas valide
		 */
		public void checkIndex(int index) {
			if (index < 0 || index >= m_flame.m_transforms.size()) {
				throw new IllegalArgumentException("invalid index given");
			}
		}
	}

}
