package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Stratégie de calcul multicoeur. Le principe est le même que celui de la stratégie par défaut, 
 * mais on fait tourner n algorithmes du chaos en parallèle qui calculent chacun n fois moins de points
 * que dans le cas du défaut avec n le nombre de processeurs disponibles dans la machine virtuelle java.<br>
 * <br>
 * Cette stratégie n'est supportée que s'il y a plus d'un processeur disponible.
 *
 */
public class ThreadStartegy extends FlameStrategy {

	// Nombre de processeurs disponibles.
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
	
	// Implémentation multicoeur de la class Flame
	private class ThreadFlame extends Flame {
		
		// "pas" avec lequel on informe d'une progression du calcul
		private final int PROGRESS_DEFINITION = 5;
		
		// Le constructeur d'accumulateur utilisé.
		private FlameAccumulator.Builder m_builder;
		
		// Progression totale tous processus confondus
		private int totalProgress = 0;
		
		public ThreadFlame(List<FlameTransformation> transforms) {
			super(transforms);
		}
	
		@Override
		protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
				final int density) {
			
			//On informe que le calcul à commencé
			triggerComputeProgress(0);
			
			// Création du builder
			m_builder = new FlameAccumulator.Builder(frame, width, height);
			
			// Liste des unités de travail
			ArrayList<Worker> workers = new ArrayList<Worker>(m_coreCount);
			
			// On démarre n unités de travail
			for(int i = 0 ; i < m_coreCount ; i++){
				Worker w = new Worker(frame, density * width * height / m_coreCount);
				
				workers.add(w);
				w.start();
			}
			
			// Puis on attends que chacune de ces unités ai fini
			for(int i = 0 ; i < workers.size() ; i++){
				try {
					workers.get(i).join();
				} catch (InterruptedException e) {
					continue;
				}
			}
			
			// Finalement, on construit l'accumulateur
			return m_builder.build();
		}
		
		// Méthode apellée par les unités de traitement pour signaler leur progression
		private void onThreadProgress(){
			totalProgress += PROGRESS_DEFINITION;
			triggerComputeProgress(totalProgress / m_coreCount);
		}
		
		/*
		 * Classe implémentant un processus de calcul parallèle.
		 */
		private class Worker extends Thread {
			
			// nombre total d'itérations pour ce processus
			private final int m;
			
			/**
			 * Construit un processus de calcul. Ce processus va calculer un nombre "iterations" de points pour la fractale "host" 
			 * @param frame cadre pour le dessin de la fractale
			 * @param iterations nombre de points à calculer
			 */
			public Worker(final Rectangle frame, final int iterations){
				m = iterations;
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
					
					// On signale la classe englobante de l'avancement
					if(i >= (progress + PROGRESS_DEFINITION)*progressStep){
						progress += PROGRESS_DEFINITION;
						onThreadProgress();
					}
				}
			}
		}
	}
}
