package ch.epfl.flamemaker.concurrent;

import java.util.Arrays;
import java.util.List;

import com.nativelibs4java.opencl.JavaCL;

import ch.epfl.flamemaker.flame.FlameTransformation;

public abstract class FlameStrategy{
	
	/**
	 * Liste des stratégies disponibles pour le rendu de la fractale triée par ordre de compatibilité
	 * (moins compatible en premier, plus compatible à la fin)
	 */
	public static final List<FlameStrategy> ALL_STARTEGIES = Arrays.asList(
		new OpenCLStrategy(),
		new DefaultStrategy()
	);
	
	public abstract String name();
	
	public abstract boolean isSupported();
	
	public abstract Flame createFlame(List<FlameTransformation> transformations);
	
	
	
	private static class OpenCLStrategy extends FlameStrategy {
		
		@Override
		public String name() {
			/* Contrairement à ce qu'on pourrait croire, ceci retourne systématiquement 
			 * la même instance de String et est par conséquent optimisé... */
			return "OpenCL";
		}

		// TODO : tester ce bout de code !
		@Override
		public boolean isSupported() {
			return JavaCL.listPlatforms().length > 0;
		}
		
		@Override
		public Flame createFlame(List<FlameTransformation> transformations){
			return new OpenCLFlame(transformations);
		}
	}
	
	private static class DefaultStrategy extends FlameStrategy {
		
		@Override
		public String name() {
			return "default";
		}
		
		@Override
		public boolean isSupported() {
			return true;
		}

		@Override
		public Flame createFlame(List<FlameTransformation> transformations){
			return new DefaultFlame(transformations);
		}
		
	}
}
