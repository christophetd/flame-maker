package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;


public abstract class FlameStrategy{
	
	/**
	 * Liste des stratégies disponibles pour le rendu de la fractale triée par ordre de compatibilité
	 * (moins compatible en premier, plus compatible à la fin)
	 */
	public static final List<FlameStrategy> ALL_STARTEGIES = Arrays.asList(
		new OpenCLStrategy(),
		new ThreadStartegy(),
		new DefaultStrategy()
	);
	
	public abstract String name();
	
	public abstract boolean isSupported();
	
	public void activate() {}
	public void deactivate() {}
	
	public abstract Flame createFlame(List<FlameTransformation> transformations);
}
