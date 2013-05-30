package ch.epfl.flamemaker.anim;

public interface Animable<T> {
	public T interpolate(Animable<T> other, int dt, int t0);
	public T get();
}
