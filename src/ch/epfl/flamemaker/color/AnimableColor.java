package ch.epfl.flamemaker.color;

import ch.epfl.flamemaker.anim.Animable;

class AnimableColor extends Color implements Animable<Color>{
	private static final long serialVersionUID = 1L;

	public AnimableColor(Color from) {
		super(from);
	}

	@Override
	public Color interpolate(Animable<Color> other, double pos) {
		return other.get().mixWith(this, pos);
	}

	@Override
	public Color get() {
		return this;
	}

}
