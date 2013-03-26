package ch.epfl.flamemaker.ifs;

import java.util.Arrays;

/**
 * Classe modélisant un accumulateur pour une fractale de type IFS
 */
public class IFSAccumulator {
	private boolean[][] m_grid;
	
	
	/**
	 * Crée un nouvel accumulateur à partir du tableau isHit (en effectuant une copie profonde)
	 * @param isHit tableau de booléens représentant l'appartenance de chaque point à l'accumuulateur
	 */
	public IFSAccumulator(boolean[][] isHit){
		
		m_grid = new boolean[isHit.length][];
		for(int i = 0 ; i < isHit.length ; i++){
			m_grid[i] = Arrays.copyOf(isHit[i], isHit[i].length);
		}
		
	}
	
	/**
	 * @return La largeur de l'accumulateur
	 */
	public int width(){
		return m_grid.length;
	}
	
	/**
	 * @return La hauteur de l'accumulateur
	 */
	public int height(){
		return m_grid[0].length;
	}
	
	/**
	 * Vérifie si la case de coordonnées (x;y) est touchée par un point de la fractale
	 * @return true si la case contient un point de la fractale
	 */
	public boolean isHit(int x, int y){
		if(x < 0 || x >= width() || y < 0 || y >= height()){
			throw new IndexOutOfBoundsException();
		}
		
		return m_grid[x][y];
	}

}
