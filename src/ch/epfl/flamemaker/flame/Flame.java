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
		double lastColor=0, currentColor = 0;
		
		for(int i = 0; i < k; i++) {
			transformationNum = randomizer.nextInt(size);
			point = m_transforms.get(transformationNum).transformPoint(point);
			lastColor = (lastColor+getColorIndex(transformationNum))/2.0;
		}
		
		int m = density * width * height;
		for(int i = 0; i < m; i++) {
			transformationNum = randomizer.nextInt(size);
			currentColor = getColorIndex(transformationNum);
			point = m_transforms.get(transformationNum).transformPoint(point);
			lastColor = (lastColor+currentColor)/2.0;
			builder.hit(point, lastColor);
		}
		
		return builder.build();
	}
	
	private double getColorIndex(int index) {
		
		if(index >= 2){
			double denominateur = Math.pow(2, Math.ceil(Math.log(index)/Math.log(2)));
			
			return ((2*index - 1) % denominateur) / denominateur;
		} else {
			return index;
		}
	}
	
	public static class Builder{
		public Builder(Flame flame){
			
		}
	}
}


