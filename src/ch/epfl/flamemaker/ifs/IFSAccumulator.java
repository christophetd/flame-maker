package ch.epfl.flamemaker.ifs;

import java.util.Arrays;

public class IFSAccumulator {
	private boolean[][] m_grid;
	
	public IFSAccumulator(boolean[][] isHit){
		
		m_grid = new boolean[isHit.length][];
		for(int i = 0 ; i < isHit.length ; i++){
			m_grid[i] = Arrays.copyOf(isHit[i], isHit[i].length);
		}
		
	}
	
	public int width(){
		return m_grid.length;
	}
	
	public int height(){
		return m_grid[0].length;
	}
	
	public boolean isHit(int x, int y){
		if(x < 0 || x >= width() || y < 0 || y >= height()){
			throw new IndexOutOfBoundsException();
		}
		
		return m_grid[x][y];
	}

}
