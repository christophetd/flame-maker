package ch.epfl.flamemaker.flame;


public class Worker extends Thread {
	FlameAccumulator.Builder m_builder;
	Flame m_fractal;
	int m_width, m_height;
	int m_density;
	 
	Worker(Flame fractal, FlameAccumulator.Builder builder, int width, int height, int density) {
		 m_fractal = fractal;
		 m_builder = builder;
		 m_width = width;
		 m_height = height;
		 m_density = density;
	}
	
	public void run() {
	    m_fractal.compute(m_builder, m_width, m_height, m_density);
	}
}
