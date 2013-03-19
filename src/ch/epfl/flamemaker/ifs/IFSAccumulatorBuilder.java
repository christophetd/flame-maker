package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

/**
 * Classe construisant peu à peu un accumulateur pour une fractale de type IFS
 */
public class IFSAccumulatorBuilder {
	
	/**
	 * 
	 */
	private boolean[][] m_grid;
	
	/** Transformation qui convertit un point du dessin vers une case de l'accumulateur */
	private Transformation m_transform;
	
	/**
	 * Initialise l'AccumulatorBuilder avec la hauteur et largeur spécifiée, pour la région du plan définie par frame.
	 * @param frame La région du plan visée
	 * @param width La largeur de l'accumulateur
	 * @param height La hauteur de l'accumulateur
	 */
	public IFSAccumulatorBuilder(Rectangle frame, int width, int height){
		if(width < 0 || height < 0)
			throw new IllegalArgumentException("width and height must be positive");
		
		m_grid = new boolean[width][height];
		
		//Transformation permettant d'associer un point du plan à un point de l'accumulateur
		m_transform = AffineTransformation.newScaling((double)width/frame.width(), (double)height/frame.height())
			.composeWith(AffineTransformation.newTranslation(-frame.left(), -frame.bottom()));
	}
	
	/**
	 * Signale la présence d'un nouveau point à la position définie par p
	 * @param p Point correspondant à position du nouveau point dans le plan
	 */
	public void hit(Point p){
		Point coord = m_transform.transformPoint(p);
		
		int x = (int) Math.floor(coord.x());
		int y = (int) Math.floor(coord.y());
		
		if(x >= 0 && x < m_grid.length && y >= 0 && y < m_grid[0].length){
			m_grid[x][y] = true;
		}
	}
	
	/**
	 * @return Un accumulateur construit à partir des données récoltées
	 */
	public IFSAccumulator build(){
		return new IFSAccumulator(m_grid);
	}
}
