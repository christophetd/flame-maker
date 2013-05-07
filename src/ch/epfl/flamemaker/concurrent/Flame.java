package ch.epfl.flamemaker.concurrent;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public abstract class Flame {
	/**
	 * Contient la liste des transformations caractérisant la fractale
	 */
	final private List<FlameTransformation> m_transforms;
	
	private List<Listener> m_listeners = new ArrayList<Listener>();

	/**
	 * Construit une nouvelle fractale à partir d'une liste de transformation la
	 * caractérisant
	 * 
	 * @param transforms
	 *            La liste des transformation
	 * @param strategy
	 * 			  Strategie employée pour le rendu
	 */
	public Flame(List<FlameTransformation> transforms){ 
		m_transforms = transforms;
	}
	
	public final void compute(final Rectangle frame, final int width, final int height,
			final int density, final List<FlameTransformation> transformations){
		
		/* On démarre le travail dans un nouveau thread. On utilise une classe anonyme pour encapsuler le thread
		 * puisqu'on veut uniquement exposer l'API compute() et abort() .
		 */
		Thread worker = new Thread(){
			@Override
			public void run(){
				doCompute(frame, width, height, density, transformations);
			}
		};
		
		worker.start();
	}
	
	public abstract void abort();
	
	protected abstract void doCompute(final Rectangle frame, final int width, final int height,
			final int density, final List<FlameTransformation> transformations);
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	protected void triggerComputeDone(){
		for(Listener l : m_listeners){
			l.onComputeDone();
		}
	}
	
	protected void triggerComputeProgress(int percent){
		for(Listener l : m_listeners){
			l.onComputeProgress(percent);
		}
	}
	
	protected List<FlameTransformation> getTransforms(){
		return m_transforms;
	}
	
	public interface Listener {
		
		public void onComputeDone();
		
		public void onComputeProgress(int percent);
	}

	/**
	 * Classe modélisant un bâtisseur pour une fractale Flame
	 */
	public static class Builder {
				
		/**
		 * La liste des bâtisseurs pour les transformations de la fractale Flame
		 * qui sera construite
		 */
		private List<FlameTransformation.Builder> m_transformationsBuilders;
	
		/**
		 * Construit un bâtisseur à partir d'une fractale existante
		 * 
		 * @param flame
		 *            La fractale flame
		 */
		public Builder(Flame flame) {
			m_transformationsBuilders = new ArrayList<FlameTransformation.Builder>();
			for(FlameTransformation transformation : flame.m_transforms) {
				m_transformationsBuilders.add(new FlameTransformation.Builder(transformation));
			}
		}

		/**
		 * @return Le nombre actuel de transformations de la fractale
		 */
		public int transformationsCount() {
			return m_transformationsBuilders.size();
		}
	
		/**
		 * Ajoute une transformation de type flame à la fractale
		 * 
		 * @param transformation
		 *            La transformation
		 */
		public void addTransformation(FlameTransformation transformation) {
			m_transformationsBuilders.add(new FlameTransformation.Builder(transformation));
		}
	
		/**
		 * @param index
		 * @return La composante affine de la transformation d'index
		 *         <i>index</i> de la fractale
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public AffineTransformation affineTransformation(int index) {
			checkIndex(index);
			
			return m_transformationsBuilders.get(index).affineTransformation();
		}
	
		/**
		 * Remplace la composante affine de la transformation d'index
		 * <i>index</i> par <i>newTransformation</i>
		 * 
		 * @param index
		 * @param newTransformation
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public void setAffineTransformation(int index,
				AffineTransformation newTransformation) {
	
			checkIndex(index);			
			m_transformationsBuilders.get(index).setAffineTransformation(newTransformation);
		}
	
		/**
		 * Retourne le poids de la variation <i>variation</i> pour la
		 * transformation d'index <i>index</i>
		 * 
		 * @param index
		 *            L'index de la transformation
		 * @param variation
		 *            La variation dont on veut récupérer le poids
		 * @return Le poids demandé
		 * 
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public double variationWeight(int index, Variation variation) {
			checkIndex(index);
			
			return m_transformationsBuilders.get(index).weight(variation.index());
		}
	
		/**
		 * Modifie le poids de la variation <i>variation</i> pour la
		 * transformation d'index <i>index</i>
		 * 
		 * @param index
		 *            L'index de la transformation
		 * @param variation
		 *            La variation dont on veut modifier le poids
		 * @param newWeight
		 *            Le nouveau poids à affecter à la variation, pour cette
		 *            transformation
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public void setVariationWeight(int index, Variation variation,
				double newWeight) {
	
			checkIndex(index);
			
			m_transformationsBuilders.get(index)
					.setWeight(variation.index(), newWeight);
		}
	
		/**
		 * Supprime le bâtisseur de transformation flame à l'index <i>index</i>
		 * 
		 * @param index
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public void removeTransformation(int index) {
			checkIndex(index);
			m_transformationsBuilders.remove(index);
		}
	
		/**
		 * Construit une fractale Flame à partir des informations récoltées
		 * @return La fractale Flame construite
		 */
		public Flame build() {
			List<FlameTransformation> builtTransformations = new ArrayList<FlameTransformation>();
			for(FlameTransformation.Builder transfoBuilder : m_transformationsBuilders) {
				builtTransformations.add(transfoBuilder.build());
			}
			
			
			for(FlameStrategy f : FlameStrategy.ALL_STARTEGIES){
				if(f.isSupported())
					return f.createStrategy(builtTransformations);
			}
			
			return null;
		}
	
		/**
		 * Vérifie si l'index passé en argument est valide pour la liste des
		 * bâtisseurs de transformation flame
		 * 
		 * @param index
		 *            L'index à vérifier
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		private void checkIndex(int index) {
			if (index < 0 || index >= m_transformationsBuilders.size()) {
				throw new IllegalArgumentException();
			}
		}

		public FlameTransformation getTransformation(int index) {
			return m_transformationsBuilders.get(index).build();
		}
	}
}
