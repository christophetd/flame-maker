package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * Classe modélisant une fractale IFS
 *
 */
public class IFS {

	// La liste des transformations affines associées à la fractale
	private List<AffineTransformation> m_transforms;
	
	/**
	 * Crée une fractale IFS à partir d'une liste de transformations affines
	 * @param transformations La liste des transformations
	 */
	public IFS(List<AffineTransformation> transformations){
		m_transforms = new ArrayList<AffineTransformation>(transformations);
	}
	
	/**
	 * @param frame La région du plan dans laquelle calculer la fractale
	 * @param width La largeur de l'accumulateur à générer
	 * @param height La hauteur de l'accumulateur à générer
	 * @param density La densité utilisée pour générer les points de la fractale (influe sur le nombre de points calculés par l'algorithme)
	 * @return IFSAccumulator l'accumulateur contenant les points de la fractale
	 */
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density){
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum = 0;
		Random randomizer = new Random();
		int size = m_transforms.size();
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(frame, width, height);
				
		for(int i = 0; i < k; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
		}
		
		int m = density * width * height;
		for(int i = 0; i < m; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
			builder.hit(point);
		}
		
		return builder.build();
	}
}
