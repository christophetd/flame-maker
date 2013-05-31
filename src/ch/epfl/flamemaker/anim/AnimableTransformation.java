package ch.epfl.flamemaker.anim;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;

public class AnimableTransformation implements Animable<FlameTransformation> {
	
	public static final AnimableTransformation IDENTITY = new AnimableTransformation(FlameTransformation.IDENTITY);
	
	private final FlameTransformation m_transform;
	
	public AnimableTransformation(FlameTransformation transform){
		m_transform = transform;
	}
	
	public AnimableTransformation(AffineTransformation affineTransformation, double[] variationWeight){
		m_transform = new FlameTransformation(affineTransformation, variationWeight);
	}
	
	@Override
	public FlameTransformation interpolate(Animable<FlameTransformation> other, double pos) {
		
		FlameTransformation t2 = other.get();
		
		double[] weights1 = m_transform.weights();
		double[] weights2 = t2.weights();
		double[] weights = new double[weights1.length];
		
		for(int i = 0 ; i < weights.length ; i++){
			weights[i] = composeValues(weights1[i], weights2[i], pos);
		}
		
		double[] aff1 = m_transform.affineTransformation().getMatrixCoeffs();
		double[] aff2 = t2.affineTransformation().getMatrixCoeffs();
		double[] coeffs = new double[6];
		for(int i = 0 ; i < 6 ; i++){
			coeffs[i] = composeValues(aff1[i], aff2[i], pos);
		}
		
		return new FlameTransformation(new AffineTransformation(coeffs) , weights);
	}
	
	private double composeValues(double d1, double d2, double weight){
		double wMin = Math.min(d1, d2);
		double wMax = Math.max(d1, d2);
		double pos = (wMin == d1) ? weight : 1 - weight;
		return wMin + (wMax - wMin)*pos;
	}

	@Override
	public FlameTransformation get() {
		return m_transform;
	}
	
	
}
