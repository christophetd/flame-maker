package ch.epfl.flamemaker.flame;

import ch.epfl.flamemaker.geometry2d.*;

public class FlameTransformation implements Transformation{
	
	private final AffineTransformation m_affineTransfo;
	private final double[] m_weight;
	
	
	public FlameTransformation(AffineTransformation affineTransformation, double[] variationWeight) {
		if(variationWeight.length != 6){
			throw new IllegalArgumentException("variationWeight must have length 6");
		}
			
			
		m_affineTransfo = affineTransformation;
		m_weight = variationWeight.clone();
	}


	@Override
	public Point transformPoint(Point p) {
		
		Point tmp, ret = new Point(0,0);
		
		for(int i = 0 ; i < Variation.ALL_VARIATIONS.size() ; i++){
			if(m_weight[i] != 0){
				tmp = Variation.ALL_VARIATIONS.get(i).transformPoint(m_affineTransfo.transformPoint(p));
				ret =  new Point(ret.x()+m_weight[i]*tmp.x(), ret.y()+m_weight[i]*tmp.y());
			}
		}
		
		return ret;
	}


	public AffineTransformation affineTransformation() {
		return new AffineTransformation(m_affineTransfo);
	}

	
	public double weight(Variation variation) {
		int index = variation.index();
		if(index > 0 || index < m_weight.length) {
			throw new IllegalArgumentException("invalid index given");
		}
		return m_weight[index];
	}


	public double[] weights() {
		return m_weight.clone();
	}
}
