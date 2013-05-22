package ch.epfl.flamemaker.flame;

import java.util.List;
import java.util.Random;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Implémente la stratégie de calcul par défaut. Un algorithme du chaos calcule tous les points de la fractale dans un seul processus.
 * Cette stratégie est compatible avec toutes les machines capable de faire tourner flame-maker.
 */
class DefaultStrategy extends FlameStrategy {
	
	// Définit le "pas" entre chaque appel à triggerProgressCompute
	private static final int PROGRESS_DEFINITION = 5;

	@Override
	public String name() {
		return "Standard";
	}
	
	@Override
	public boolean isSupported() {
		return true; // Cette stratégie est tout le temps supportée
	}

	@Override
	public Flame createFlame(List<FlameTransformation> transformations){
		return new DefaultFlame(transformations);
	}
	
	// Implémentation de Flame avec l'algorithme par défaut.
	private class DefaultFlame extends Flame {
		
		public DefaultFlame(List<FlameTransformation> transforms) {
			super(transforms);
		}
	
		@Override
		protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
				final int density) {
			
			// On signale tout de suite que le calcul a commencé
			triggerComputeProgress(0);
			
			// On initialise un random déterminé par la graine 2013
			Random randomizer = new Random(2013);
			
			List<FlameTransformation> transformations = this.getTransforms();
	
			// Création des variables utilisées dans les boucles de calcul
			Point point = new Point(0, 0);
			int k = 20;
			int transformationNum;
	
			// On récupère une fois pour toutes la taille de la liste de
			// transformations
			int size = transformations.size();
	
			// Création du builder
			FlameAccumulator.Builder builder = new FlameAccumulator.Builder(frame,
					width, height);
	
			// Garde en mémoire la couleur du dernier point accumulé
			double lastColor = 0;
			
			// 20 premières itérations dans le vide pour l'algorithme du chaos
			for (int i = 0; i < k; i++) {
				transformationNum = randomizer.nextInt(size);
				point = transformations.get(transformationNum).transformPoint(point);
				lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
			}
	
			// Iterations accumulées pour le rendu
			int m = density * width * height;
			int progressStep = m/100;
			int progress = 0;
			
			for (int i = 0; i < m && !isAborted() ; i++) {
				transformationNum = randomizer.nextInt(size);
				point = transformations.get(transformationNum).transformPoint(point);
				lastColor = (lastColor + getColorIndex(transformationNum)) / 2.0;
				
				builder.hit(point, lastColor);
				
				// Si on a suffisamment avancé, on informe l'extérieur de l'avancement.
				if(i >= (progress + PROGRESS_DEFINITION)*progressStep){
					progress += PROGRESS_DEFINITION;
					triggerComputeProgress(progress);
				}
			}
			
			// On construit l'accumulateur
			return builder.build();
		}
	}
}
