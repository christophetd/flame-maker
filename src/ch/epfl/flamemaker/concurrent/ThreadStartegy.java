package ch.epfl.flamemaker.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class ThreadStartegy extends FlameStrategy {

	private int m_coreCount = Runtime.getRuntime().availableProcessors();
	
	@Override
	public String name() {
		return "Multicoeur";
	}

	@Override
	public boolean isSupported() {
		return m_coreCount > 1;
	}

	@Override
	public Flame createFlame(List<FlameTransformation> transformations) {
		return new ThreadFlame(transformations);
	}
	
	private class ThreadFlame extends Flame {
		
		public final int PROGRESS_DEFINITION = 5;
		
		private FlameAccumulator.Builder m_builder;
		
		private int totalProgress = 0;
		
		public ThreadFlame(List<FlameTransformation> transforms) {
			super(transforms);
		}
	
		@Override
		protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
				final int density) {
			
			triggerComputeProgress(0);
			
			// Création du builder
			m_builder = new FlameAccumulator.Builder(frame, width, height);
			
			ArrayList<Worker> workers = new ArrayList<Worker>(m_coreCount);
			
			for(int i = 0 ; i < m_coreCount ; i++){
				Worker w = new Worker(this, frame, density * width * height / m_coreCount);
				
				workers.add(w);
				w.start();
			}
			
			for(int i = 0 ; i < workers.size() ; i++){
				try {
					workers.get(i).join();
				} catch (InterruptedException e) {
					continue;
				}
			}
			
			// On construit l'accumulateur
			return m_builder.build();
		}
		
		private void onThreadProgress(){
			totalProgress += PROGRESS_DEFINITION;
			triggerComputeProgress(totalProgress / m_coreCount);
		}
		
		private class Worker extends Thread {
			
			private int m;
			private ThreadFlame host;
			
			public Worker(final ThreadFlame host, final Rectangle frame, final int iterations){
				m = iterations;
				this.host = host;
			}
			@Override
			public void run(){
				// On initialise un random
				Random randomizer = new Random();
				
				List<FlameTransformation> transformations = getTransforms();
		
				// Création des variables utilisées dans les boucles de calcul
				Point point = new Point(0, 0);
				int k = 20;
				int transformationNum;
		
				// On récupère une fois pour toutes la taille de la liste de
				// transformations
				int size = transformations.size();
		
				// Garde en mémoire la couleur du dernier point accumulé
				double lastColor = 0;
				
				// 20 premières itérations dans le vide pour l'algorithme du chaos
				for (int i = 0; i < k; i++) {
					transformationNum = randomizer.nextInt(size);
					point = transformations.get(transformationNum).transformPoint(point);
					lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
				}

				// Iterations accumulées pour le rendu
				int progressStep = m/100;
				int progress = 0;
				
				for (int i = 0; i < m && !isAborted() ; i++) {
					transformationNum = randomizer.nextInt(size);
					point = transformations.get(transformationNum).transformPoint(point);
					lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
					
					m_builder.hit(point, lastColor);
					
					if(i >= (progress + PROGRESS_DEFINITION)*progressStep){
						progress += PROGRESS_DEFINITION;
						host.onThreadProgress();
					}
				}
			}
		}
	}
}
