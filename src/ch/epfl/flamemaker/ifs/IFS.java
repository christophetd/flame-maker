package ch.epfl.flamemaker.ifs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.*;

public class IFS {

	private List<AffineTransformation> m_transforms;
	
	public IFS(List<AffineTransformation> transformations){
		m_transforms = new ArrayList<AffineTransformation>(transformations);
	}
	
	public IFSAccumulator compute(Rectangle frame, int width, int height, int density){
		Point point = new Point(0, 0);
		int k = 20;
		int transformationNum = 0;
		Random randomizer = new Random();
		int size = m_transforms.size();
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(frame, width, height);
				
		for(int i = 0; i < k; i++) {
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
