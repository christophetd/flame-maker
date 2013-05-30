package ch.epfl.flamemaker.anim;

public interface Animable<T> {
	public T interpolate(Animable<T> other, double pos);
	public T get();
}
