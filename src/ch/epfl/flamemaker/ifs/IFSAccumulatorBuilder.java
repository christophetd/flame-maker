package ch.epfl.flamemaker.ifs;

import ch.epfl.flamemaker.geometry2d.*;

public class IFSAccumulatorBuilder {
	
	private boolean[][] m_grid;
	
	/** Transformation qui convertit un point du dessin vers une case de l'accumulateur */
	private Transformation m_transform;
	
	/**
	 * Initialise l'AccumulatorBuilder avec la hauteur et largeur spécifiée, pour la région du plan définie par frame.
	 * @param frame région du plan visée
	 * @param width largeur de l'accumulateur
	 * @param height hauteur de l'accumulateur
	 */
	public IFSAccumulatorBuilder(Rectangle frame, int width, int height){
		if(width < 0 || height < 0)
			throw new IllegalArgumentException("width and height must be positive");
		
		m_grid = new boolean[width][height];
		
		//On crée la transformation qui passe d'un point du plan à un point de l'accumulateur
		m_transform = AffineTransformation.newScaling((double)width/frame.width(), (double)height/frame.height())
			.composeWith(AffineTransformation.newTranslation(-frame.left(), -frame.bottom()));
	}
	
	/**
	 * Signale la présence d'un nouveau point à la position définie par p
	 * @param p position du point dans le plan
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
	 * Construit un accumulateur avec les données récoltées
	 */
	public IFSAccumulator build(){
		return new IFSAccumulator(m_grid);
	}
}
