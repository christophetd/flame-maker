package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

public class FlameTransformation implements Transformation{
	
	private final AffineTransformation m_affineTransfo;
	private final double[] m_weight;
	
	
	FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight) {
		if(variationWeight.length != 6){
			throw new IllegalArgumentException("variationWeight must have length 6");
		}
			
			
		m_affineTransfo = affineTransformation;
		m_weight = variationWeight.clone();
	}


	@Override
	public Point transformPoint(Point p) {
		
		Point tmp, ret = new Point(0,0);
		Point aff = m_affineTransfo.transformPoint(p);
		
		for(int i = 0 ; i < Variation.ALL_VARIATIONS.size() ; i++){
			if(m_weight[i] != 0){
				tmp = Variation.ALL_VARIATIONS.get(i).transformPoint(aff);
				ret = new Point(ret.x()+m_weight[i]*tmp.x(), ret.y()+m_weight[i]*tmp.y());
			}
		}
		
		return ret;
	}
}
