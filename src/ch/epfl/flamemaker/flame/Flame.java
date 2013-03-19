package ch.epfl.flamemaker.flame;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
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
		Random randomizer = new Random(2013);
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
			point = m_transforms.get(transformationNum).transformPoint(point);
			lastColor = (lastColor+getColorIndex(transformationNum))/2.0;
			
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
		Flame m_flame;
		
		public Builder(Flame flame){
			m_flame = flame;
		}
		
		public int transformationsCount() {
			return m_flame.m_transforms.size();
		}
		
		public void addTransformation(FlameTransformation transformation) {
			m_flame.m_transforms.add(transformation);
		}
		
		public AffineTransformation affineTransformation(int index) {
			checkIndex(index);
			return m_flame.m_transforms.get(index).affineTransformation();
		}
		
		public void setAffineTransformation(int index, AffineTransformation newTransformation) {
			checkIndex(index);
			FlameTransformation transformation = m_flame.m_transforms.get(index);
			m_flame.m_transforms.set(index, new FlameTransformation(newTransformation, transformation.weights()));
		}
		
		public double variationWeight(int index, Variation variation) {
			checkIndex(index);
			return m_flame.m_transforms.get(index).weight(variation);
		}
		
		public void setVariationWeight(int index, Variation variation, double newWeight) {
			checkIndex(index);
			FlameTransformation transformation = m_flame.m_transforms.get(index);
			double[] weights = transformation.weights();
			int weightIndex = variation.index();
			
			if(weightIndex > 0 && weightIndex < weights.length) {
				weights[weightIndex] = newWeight;
			}
			
			m_flame.m_transforms.set(index, new FlameTransformation(transformation.affineTransformation(), weights));
		}
		
		public void removeTransformation(int index) {
			checkIndex(index);
			m_flame.m_transforms.remove(index);
		}
		
		public Flame build() {
			return new Flame(m_flame.m_transforms);
		}
		
		public void checkIndex(int index) {
			if(index < 0 || index >= m_flame.m_transforms.size()) {
				throw new IllegalArgumentException("invalid index given");
			}
		}
	}


}













