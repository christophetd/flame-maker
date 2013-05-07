package ch.epfl.flamemaker.concurrent;

import static org.bridj.Pointer.allocateBytes;
import static org.bridj.Pointer.allocateFloats;
import static org.bridj.Pointer.allocateInts;
import static org.bridj.Pointer.allocateLongs;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

import org.bridj.Pointer;

import com.nativelibs4java.opencl.CLBuffer;
import com.nativelibs4java.opencl.CLContext;
import com.nativelibs4java.opencl.CLEvent;
import com.nativelibs4java.opencl.CLKernel;
import com.nativelibs4java.opencl.CLProgram;
import com.nativelibs4java.opencl.CLQueue;
import com.nativelibs4java.opencl.JavaCL;
import com.nativelibs4java.opencl.CLMem.Usage;
import com.nativelibs4java.util.IOUtils;

import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class OpenCLFlame extends Flame {
	
	private static final int KERNELS_COUNT = 1024;
	
	private static final int INLINE_TRANSFO_LENGTH = 13;
	
	public OpenCLFlame(List<FlameTransformation> transforms) {
		super(transforms);

		
	}
	
	@Override
	protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
			final int density) {
		
		CLContext context = JavaCL.createBestContext();
        CLQueue queue = context.createDefaultQueue();
        ByteOrder byteOrder = context.getByteOrder();
        
		Pointer<Byte> mapPtr = allocateBytes(width*height).order(byteOrder);
		Pointer<Integer> intensitiesPtr = allocateInts(width*height).order(byteOrder);
		Pointer<Float> trnsPtr = allocateFloats((getTransforms().size() + 1) * INLINE_TRANSFO_LENGTH).order(byteOrder);
		Pointer<Float> ptsPtr = allocateFloats(KERNELS_COUNT*3).order(byteOrder);
		
		float[] transforms = inlineTransforms(AffineTransformation.newScaling(
				(double) width / frame.width(),
				(double) height / frame.height()).composeWith(
				AffineTransformation.newTranslation(-frame.left(),
						-frame.bottom())));
		
		trnsPtr.setFloats(transforms);
		
		CLBuffer<Byte> map = context.createByteBuffer(Usage.InputOutput, mapPtr);
		CLBuffer<Integer> intensities = context.createIntBuffer(Usage.InputOutput, intensitiesPtr);
		CLBuffer<Float> transformsBuffer = context.createFloatBuffer(Usage.Input, trnsPtr);
		CLBuffer<Float> pointsBuffer = context.createFloatBuffer(Usage.InputOutput, ptsPtr);
		
		// Lis et compile le code du kernel
        String src = "";
		try {
			src = IOUtils.readText(OpenCLFlame.class.getClassLoader().getResource("renderer.cl"));
		} catch (IOException e) {
			System.err.println("Impossible de charger le fichier du kernel de rendu OpenCL");
			return null;
		}
		
        CLProgram program = context.createProgram(src);
        
        //Récupération des kernels
        CLKernel computeKernel = program.createKernel("compute");
        
		//Calcul de la durée de vie du random
		int random_life = computeRandomLife(getTransforms().size());
		Pointer<Long> seedsPtr = allocateLongs(KERNELS_COUNT).order(byteOrder);
		
		generateRandom(seedsPtr, KERNELS_COUNT);
		CLBuffer<Long> seeds = context.createBuffer(Usage.Input, seedsPtr);

		int percent = 0;
		
		CLEvent computeEvt = null;
  
		int iterations = density*width*height/KERNELS_COUNT/random_life;
		
		//Calcul de la fractale avec l'algorithme du chaos
		for(int i = 0 ; i < iterations && !isAborted() ; i++){
			computeKernel.setArgs(
	      		seeds, 
	      		map, 
	      		intensities, 
	      		width, 
	      		height, 
	      		transformsBuffer, 
	      		getTransforms().size(), 
	      		random_life, 
	      		pointsBuffer );
			
			computeEvt = computeKernel.enqueueNDRange(queue, new int[] { KERNELS_COUNT });
   
			if(i < iterations-1){
				generateRandom(seedsPtr, KERNELS_COUNT);
				seeds.write(queue, seedsPtr, true, computeEvt);
			}
    
			if(100*i/iterations > percent+4){
				percent = 100*i/iterations;
				triggerComputeProgress(percent);
			}
		}
		
		intensitiesPtr = intensities.read(queue, computeEvt);
		mapPtr = map.read(queue);
		
		//On libère les ressources inutiles
		seeds.release();
		seedsPtr.release();
		transformsBuffer.release();
		context.release();
		program.release();
		
		if(isAborted()){
			intensitiesPtr.release();
			mapPtr.release();
			return null;
		}
		
		// préparation des valeurs obtenues pour créer un accumulateur
		int[][] hitCountArray = new int[width][height];
		double[][] colorArray = new double[width][height];
		
		for(int i = 0 ; i < width ; i++){
			
			for(int j = 0 ; j < height ; j++){
	        	hitCountArray[i][height - j - 1] = intensitiesPtr.get(i+j*width);
	        	colorArray[i][height - j - 1] = mapPtr.get(i+j*width)/127.0;
			}
        }
		
		
		intensitiesPtr.release();
		mapPtr.release();
		
		triggerComputeProgress(100);
		
		return new FlameAccumulator(hitCountArray, colorArray);
	}
	
	/**
	 * Convertit les transformations en un tableau de floats utilisable par le kernel OpenCL.
	 */
	private float[] inlineTransforms(AffineTransformation cameraTransform){
		
		List<FlameTransformation> transforms = getTransforms();
		
		transforms.add(new FlameTransformation(cameraTransform, new double[] {1,0,0,0,0,0}));
		
		float[] ret = new float[transforms.size() * 13];
		
		for(int i = 0 ; i < transforms.size() ; i++){
			int offset = i*13;
			FlameTransformation trns = transforms.get(i);
			
			double[] coeffs = trns.affineTransformation().getMatrixCoeffs();
			for(int j = 0 ; j < 6 ; j++){
				ret[offset+j] = (float) coeffs[j];
			}
			
			ret[offset+6] = (float) getColorIndex(i);
			
			double[] weights = trns.weights();
			for(int j = 0 ; j < 6 ; j++){
				ret[offset+7+j] = (float) weights[j];
			}
		}
		
		return ret;
	}
	
	private int computeRandomLife(long divider){
    	long random = 0X7FFFFFFFFFFFFFFFL; // Largest long
    	
    	int i = 0;
    	while(random > divider){
    		i++;
        	random /= divider;
        }
    	
    	return i;
    }
    
	private void generateRandom(Pointer<Long> seedsPtr, int n){
    	for (int j = 0; j < n; j++) {
            seedsPtr.set(j, (long) (Math.random()*0X7FFFFFFFFFFFFFFFL)+1);
        }
    }
}
