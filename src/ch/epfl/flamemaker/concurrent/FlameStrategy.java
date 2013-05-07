package ch.epfl.flamemaker.concurrent;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

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
	
	public abstract Flame createStrategy(List<FlameTransformation> transformations);
	
	
	
	private static class OpenCLStrategy extends FlameStrategy {
		
		@Override
		public String name() {
			/* Contrairement à ce qu'on pourrait croire, ceci retourne systématiquement 
			 * la même instance de String et est par conséquent optimisé... */
			return "OpenCL";
		}

		@Override
		public boolean isSupported() {
			return false;
		}
		
		@Override
		public Flame createStrategy(List<FlameTransformation> transformations){
			return new CLFlame(transformations);
		}
		
		private static class CLFlame extends Flame {

			public CLFlame(List<FlameTransformation> transforms) {
				super(transforms);
				// TODO Auto-generated constructor stub
			}
			
			@Override
			public void abort() {
				System.out.println("Aborting !");
			}

			@Override
			protected void doCompute(final Rectangle frame, final int width, final int height,
					final int density, final List<FlameTransformation> transformations) {
				System.out.println("Computing !");
			}
		}
	}
	
	private static class DefaultStrategy extends FlameStrategy {
		
		@Override
		public String name() {
			return "default";
		}

		@Override
		public Flame createStrategy(List<FlameTransformation> transformations){
			return new DefaultFlame(transformations);
		}
		
		@Override
		public boolean isSupported() {
			return true;
		}
		
		private static class DefaultFlame extends Flame {

			public DefaultFlame(List<FlameTransformation> transforms) {
				super(transforms);
				// TODO Auto-generated constructor stub
			}
			
			@Override
			public void abort() {
				System.out.println("Aborting !");
			}

			@Override
			protected void doCompute(final Rectangle frame, final int width, final int height,
					final int density, final List<FlameTransformation> transformations) {
				System.out.println("Computing !");
			}
		}
	}
}
