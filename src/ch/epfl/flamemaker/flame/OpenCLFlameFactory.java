/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.flame;

import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.allocateInts;
import static org.bridj.Pointer.allocateLongs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Random;

import org.bridj.Pointer;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.util.IOUtils;


/**
 * Implémente une flame avec un rendu par OpenCL. De nombreux algorithmes du chaos sont lancés en 
 * parallèle sur du matériel adapté au calcul parallel (ex: carte graphique).
 * Cette implementation nécessite des drivers spécifiques mais offre les meilleures performances sur du matériel adéquat.<br>
 * <br>
 * Utilise le kernel de rendu OpenCL du fichier bin/renderer.cl
 */
class OpenCLFlameFactory extends FlameFactory {
	
	private CLContext context;
	private CLProgram program;
	private CLKernel computeKernel;
	private CLQueue queue;
	
	@Override
	public String name() {
		return "OpenCL";
	}
	
	@Override
	public boolean isSupported() {
		try{
			/* 
			 * Si aucun driver OpenCL est disponible, cette ligne lance une erreur,
			 * sinon, on vérifie qu'il y ai quand même une plateforme disponible.
			 */
			return JavaCL.listPlatforms().length > 0;
		} catch(Error e){
			// On rattrape l'éventuelle erreur et on signale que la stratégie n'est pas supportée
			return false;
		}
	}
	
	@Override
	public Flame createFlame(List<FlameTransformation> transformations){
		return new OpenCLFlame(transformations);
	}

	@Override
	public void enable() {
		if(context != null)
			return;
		
		// On crée le contexte JavaCL
		context = JavaCL.createBestContext();
		// Crée la queue par défaut
		queue = context.createDefaultQueue();
		
		// Lis et compile le code du kernel
        String src = "";
		try {
			src = IOUtils.readText(FlameFactory.class.getClassLoader().getResource("renderer.cl"));
		} catch (IOException e) {
			System.err.println("Impossible de charger le fichier du kernel de rendu OpenCL");
			return;
		}
		program = context.createProgram(src);
        // Récupération du kernel
        computeKernel = program.createKernel("compute");
	}
	
	@Override
	public void disable() {
		/* 
		 * On libère toutes les ressources allouées pour openCL pour permettre à d'autres applications de les 
		 * utiliser ou pour permettre la mise en veille du matériel.
		 * ( Portion de code testée avec optimus de nvidia, la carte graphique passe en veille ).
		 */
		context.release();
		program.release();
		queue.release();
		computeKernel.release();
		
		queue = null;
		program = null;
		computeKernel = null;
		context = null;
		
		/* 
		 * Force la garbage collection pour libérer les ressources
		 * (sans cette ligne, on constate que la carte graphique reste active un certain temps)
		 */
		System.gc();
	}

	private class OpenCLFlame extends Flame {
		
		// Nombre maximum de travaux par vague
		private static final long MAX_KERNEL_COUNT = 49152;
		
		// Taille d'une transformation lorsqu'elle est convertie en tableau de floats
		private static final int INLINE_TRANSFO_LENGTH = 13;

		
		public OpenCLFlame(List<FlameTransformation> transforms) {
			super(transforms);
		}
		
		@Override
		protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
				final int density) {
			
			/* Configuration du rendu en fonction de la taille de l'accumulateur */
			
			// Nombre de travaux par vague
			int kernel_count = (int) Math.min(MAX_KERNEL_COUNT, (long)density*width*height);
			// Nombre de vagues
			int iterations = Math.max(1, density*width*height/kernel_count/50);
			// Nombre d'itération par travail
			int kernel_iterations = density*width*height/kernel_count/iterations;
			
	        ByteOrder byteOrder = context.getByteOrder();
	        
	        /* 		Allocution des plages mémoire pour le calcul	 */
	        
	        // Couleurs de l'accumulateur
			Pointer<Float> colPtr = allocateFloats(width*height).order(byteOrder);
			// Intensités de l'accumulateur
			Pointer<Integer> intensitiesPtr = allocateInts(width*height).order(byteOrder);
			// Transformations de la fractale
			Pointer<Float> trnsPtr = allocateFloats((getTransforms().size() + 1) * INLINE_TRANSFO_LENGTH).order(byteOrder);
			// Points itérés de l'algo. du chaos
			Pointer<Float> ptsPtr = allocateFloats(kernel_count*3).order(byteOrder);
			
			// On convertit les transformations en tableaux de floats pour les envoyer au kernel
			float[] transforms = inlineTransforms(AffineTransformation.newScaling(
					(double) width / frame.width(),
					(double) height / frame.height()).composeWith(
					AffineTransformation.newTranslation(-frame.left(),
							-frame.bottom())));
			
			trnsPtr.setFloats(transforms);
			
			/*		 Allocution des buffers d'entrée/sortie		*/
			
			CLBuffer<Float> colors = context.createFloatBuffer(Usage.InputOutput, colPtr);
			CLBuffer<Integer> intensities = context.createIntBuffer(Usage.InputOutput, intensitiesPtr);
			CLBuffer<Float> transformsBuffer = context.createFloatBuffer(Usage.Input, trnsPtr);
			CLBuffer<Float> pointsBuffer = context.createFloatBuffer(Usage.InputOutput, ptsPtr);
			
			// Graines pour le générateur de nombres pseudo-aléatoire
			Pointer<Long> seedsPtr = allocateLongs(kernel_count).order(byteOrder);
			generateSeeds(seedsPtr, kernel_count);
			
			CLBuffer<Long> seeds = context.createBuffer(Usage.InputOutput, seedsPtr);
	  
			// Garde en mémoire l'avancement
			int percent = 0;
			
			// Evenement utilisé pour synchroniser les vagues
			CLEvent computeEvt = null;
			
			//Calcul de la fractale avec l'algorithme du chaos
			for(int i = 0 ; i < iterations && !isAborted() ; i++){
				
				// On passe les arguments au noyau
				computeKernel.setArgs(
					seeds,
		      		colors,
		      		intensities,
		      		width,
		      		height,
		      		transformsBuffer,
		      		getTransforms().size(),
		      		kernel_iterations,
		      		pointsBuffer );
				
				// Et on lance kernel_count travaux sur ce noyau.
				computeEvt = computeKernel.enqueueNDRange(queue, new int[] { kernel_count });
				
				// Wait for it....
				computeEvt.waitFor();
				
				// Si on a un avancement significatif, on informe les observateurs
				if(100*i/iterations > percent){
					percent = 100*i/iterations;
					triggerComputeProgress(percent);
				}
			}
			
			// Lecture des intensités
			intensitiesPtr = intensities.read(queue, computeEvt);
			// Lecture des couleurs
			colPtr = colors.read(queue);
			
			//On libère les ressources inutiles
			seeds.release();
			seedsPtr.release();
			transformsBuffer.release();
			
			// Si le rendu était annulé, on libère et on arrête
			if(isAborted()){
				intensitiesPtr.release();
				colPtr.release();
				return null;
			}
			
			// préparation des valeurs obtenues pour créer un accumulateur
			int[][] hitCountArray = new int[width][height];
			double[][] colorArray = new double[width][height];
			
			// Construction des données de l'accumulateur
			for(int i = 0 ; i < width ; i++){
				for(int j = 0 ; j < height ; j++){
		        	hitCountArray[i][height - j - 1] = intensitiesPtr.get(i+j*width);
		        	colorArray[i][height - j - 1] = colPtr.get(i+j*width);
				}
	        }
			
			// On libère ce qui reste
			intensitiesPtr.release();
			colPtr.release();
			
			// Et on retourne un nouvel accumulateur tout beau tout frais :)
			return new FlameAccumulator(hitCountArray, colorArray);
		}
		
		/**
		 * Convertit les transformations en un tableau de floats utilisable par le kernel OpenCL selon le format suivant :<br>
		 * [ linera matrix (6) | color index (1) | variations weights (6) ]<br>
		 * En rajoutant la transformation de la caméra à la fin.
		 * @param cameraTransfomr transformation de la camera (ajoutée à la fin du tableau)
		 * @return transformations sous forme de tableau de floats
		 */
		private float[] inlineTransforms(AffineTransformation cameraTransform){
			
			List<FlameTransformation> transforms = getTransforms();
			
			// On ajoute la transformation de caméra à la fin.
			transforms.add(new FlameTransformation(cameraTransform, new double[] {1,0,0,0,0,0}));
			
			// On prépare un tableau de floats de taille adéquate
			float[] ret = new float[transforms.size() * 13];
			
			
			for(int i = 0 ; i < transforms.size() ; i++){
				int offset = i*13;
				FlameTransformation trns = transforms.get(i);
				
				// Remplissage des coefficients de la matrice (indices 0 à 5)
				double[] coeffs = trns.affineTransformation().getMatrixCoeffs();
				for(int j = 0 ; j < 6 ; j++){
					ret[offset+j] = (float) coeffs[j];
				}
				
				// Color index à l'indice 6
				ret[offset+6] = (float) getColorIndex(i);
				
				// Remplissage des poids des variations (indices 7 à 12)
				double[] weights = trns.weights();
				for(int j = 0 ; j < 6 ; j++){
					ret[offset+7+j] = (float) weights[j];
				}
			}
			
			return ret;
		}
	    
		/**
		 * Génère un tableau de graines pour alimenter le générateur de nombre pseudo-aléatoires du kernel
		 * @param seedsPtr pointeur sur le buffer de graines
		 * @param n nombre de graines à générer
		 */
		private void generateSeeds(Pointer<Long> seedsPtr, int n){
			//Initialisation du random avec une graine pour éviter de voir le bruit "bouger" entre deux rendus
			Random rand = new Random(2013);
	    	for (int j = 0; j < n; j++) {
	            seedsPtr.set(j,rand.nextLong());
	        }
	    }
	}
}