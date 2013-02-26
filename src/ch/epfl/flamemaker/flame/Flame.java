package ch.epfl.flamemaker.flame;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Point;

public class Flame {
	final private List<FlameTransformation> m_transforms;
	
	public Flame(List<FlameTransformation> transforms) {
		m_transforms = transforms;
	}
	
	public FlameAccumulator compute(Rectangle frame, int width, int height, int density) {
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum = 0;
		Random randomizer = new Random();
		int size = m_transforms.size();
		FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame, width, height);
		
		for(int i = 0; i < k; k++) {
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
