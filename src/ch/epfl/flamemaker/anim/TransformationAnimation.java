package ch.epfl.flamemaker.anim;

import ch.epfl.flamemaker.flame.FlameTransformation;

public class TransformationAnimation extends Animation<AnimableTransformation, FlameTransformation> {
	
	public TransformationAnimation(AnimableTransformation t0){
		set(t0, 0);
	}
	
	public TransformationAnimation(TransformationAnimation other){
		super(other);
	}
}
