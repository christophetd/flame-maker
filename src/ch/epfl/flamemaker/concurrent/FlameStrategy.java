package ch.epfl.flamemaker.concurrent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.flame.FlameTransformation;

import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.util.IOUtils;

// TODO : déplacer les stratégies dans les fichiers appropriés

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
	
	public void activate() {}
	public void deactivate() {}
	
	public abstract Flame createFlame(List<FlameTransformation> transformations);
	
	
	
	private static class OpenCLStrategy extends FlameStrategy {
		
		private CLContext context;
		private CLProgram program;
		private CLKernel computeKernel;
		private CLQueue queue;
		
		@Override
		public String name() {
			/* Contrairement à ce qu'on pourrait croire, ceci retourne systématiquement 
			 * la même instance de String et est par conséquent optimisé... */
			return "OpenCL";
		}
		
		@Override
		public boolean isSupported() {
			try{
				return JavaCL.listPlatforms().length > 0;
			} catch(Error e){
				return false;
			}
		}
		
		@Override
		public Flame createFlame(List<FlameTransformation> transformations){
			return new OpenCLFlame(transformations, context, computeKernel, queue);
		}

		@Override
		public void activate() {
			if(context != null)
				return;
			context = JavaCL.createBestContext();
			
			// Lis et compile le code du kernel
	        String src = "";
			try {
				src = IOUtils.readText(FlameStrategy.class.getClassLoader().getResource("renderer.cl"));
			} catch (IOException e) {
				System.err.println("Impossible de charger le fichier du kernel de rendu OpenCL");
				return;
			}
			program = context.createProgram(src);
			queue = context.createDefaultQueue();
	        // Récupération des kernels
	        computeKernel = program.createKernel("compute");
		}

		@Override
		public void deactivate() {
			System.out.println("deactivate");
			context.release();
			program.release();
			queue.release();
			computeKernel.release();
			
			queue = null;
			program = null;
			computeKernel = null;
			context = null;
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
