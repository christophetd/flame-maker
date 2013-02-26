package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;


public class FlameAccumulator {

	private int[][] m_grid;
	private double m_denominator;
	
	private FlameAccumulator(int[][] hitCount) {
		
		int maxHit = 0;
		
		m_grid = new int[hitCount.length][];
		for(int i = 0 ; i < hitCount.length ; i++){
			m_grid[i] = new int[hitCount[i].length];
			for(int j = 0 ; j < hitCount.length ; j++){
				m_grid[i][j] = hitCount[i][j];
				if(maxHit < hitCount[i][j]){
					maxHit = hitCount[i][j];
				}
			}
		}
		
		m_denominator = Math.log(maxHit+1);
	}

	public int width(){
		return m_grid.length;
	}
	
	public int height(){
		return m_grid[0].length;
	}
	
	double intensity(int x, int y){
		if(x < 0 || y < 0 || x > m_grid.length || y > m_grid[0].length){
			throw new IndexOutOfBoundsException();
		}
		
		return Math.log(m_grid[x][y]+1)/m_denominator;
	}
	
	public static class Builder {
		
		private int[][] m_grid;
		private Transformation m_transform;
		
		Builder(Rectangle frame, int width, int height){
			if(width <= 0 || height <= 0){
				throw new IllegalArgumentException("width and height must be positive");
			}
		
			m_grid = new int[width][height];
		
			//On crée la transformation qui passe d'un point du plan à un point de l'accumulateur
			m_transform = AffineTransformation.newScaling((double)width/frame.width(), (double)height/frame.height())
				.composeWith(AffineTransformation.newTranslation(-frame.left(), -frame.bottom()));
		}
		
		public void hit(Point p){
			Point coord = m_transform.transformPoint(p);
			
			int x = (int) Math.floor(coord.x());
			int y = (int) Math.floor(coord.y());
			
			if(x >= 0 && x < m_grid.length && y >= 0 && y < m_grid[0].length){
				m_grid[x][y]++;
			}
		}
		
		/**
		 * Construit un accumulateur avec les données récoltées
		 */
		public FlameAccumulator build(){
			return new FlameAccumulator(m_grid);
		}
	}
}
