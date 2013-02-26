package ch.epfl.flamemaker.flame;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.Point;

public class Flame {
	final private List<FlameTransformation> m_transforms;
	
	public Flame(List<FlameTransformation> transforms) {
		m_transforms = transforms;
	}
	
	public void compute(FlameAccumulator.Builder builder, int width, int height, long density) {
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum = 0;
		Random randomizer = new Random();
		int size = m_transforms.size();
		
		for(int i = 0; i < k; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
		}
		long m = density * width * height;
		for(long i = 0; i < m; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
			builder.hit(point);
		}
	}
}
