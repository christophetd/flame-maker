package ch.epfl.flamemaker.concurrent;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class Flame {
	/**
	 * Contient la liste des transformations caractérisant la fractale
	 */
	final private List<FlameTransformation> m_transforms;
	
	private List<Listener> m_listeners = new ArrayList<Listener>();
	
	private boolean m_aborted = false;
	
	private Thread m_worker;

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
			final int density){
		
		if(m_worker != null){
			abort();
		}
		/* On démarre le travail dans un nouveau thread. On utilise une classe anonyme pour encapsuler le thread
		 * puisqu'on veut uniquement exposer l'API compute() et abort() .
		 */
		m_worker = new Thread(){
			@Override
			public void run(){
				FlameAccumulator acc = doCompute(frame, width, height, density);
				// Quand on a fini de calculer :
				triggerComputeDone(acc);
			}
		};
		m_aborted = false;
		m_worker.start();
		
	}
	
	public void destroy(){
		synchronized(m_listeners) {
			m_listeners.clear();
		}
		abort();
	}
	
	public final void abort(){
		m_aborted = true;
		try {
			m_worker.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_worker = null;
	}
	
	protected boolean isAborted(){
		return m_aborted;
	}
	
	protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
			final int density){
		throw new UnsupportedOperationException("L'implémentation par défaut de flame ne permet pas le rendu !");
	}
	
	public void addListener(Listener l){
		synchronized(m_listeners) {
			m_listeners.add(l);
		}
	}
	
	public void removeListener(Listener l){
		synchronized(m_listeners) {
			m_listeners.remove(l);
		}
	}
	
	protected void triggerComputeDone(FlameAccumulator acc){
		synchronized(m_listeners) {
			for(Listener l : m_listeners){
				l.onComputeDone(acc);
			}
		}
	}
	
	protected void triggerComputeProgress(int percent){
		synchronized(m_listeners) {
			for(Listener l : m_listeners){
				l.onComputeProgress(percent);
			}
		}
	}
	
	protected List<FlameTransformation> getTransforms(){
	return new ArrayList<FlameTransformation>(m_transforms);
	}
	
	/**
	 * @param index
	 *            L'index de la transformation de laquelle on désire avoir
	 *            l'index de couleur
	 * @return L'index de couleur associé à la transformation
	 */
	protected double getColorIndex(int index) {

		if (index >= 2) {
			double denominateur = Math.pow(2,
					Math.ceil(Math.log(index) / Math.log(2)));

			return ((2 * index - 1) % denominateur) / denominateur;
		} else {
			return index;
		}
	}
	
	public interface Listener {
		
		public void onComputeDone(FlameAccumulator acc);
		
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
		
		private FlameStrategy m_strategy;
	
		/**
		 * Construit un bâtisseur à partir d'une fractale existante et choisis la meilleure stratégie de calcul.
		 * 
		 * @param flame
		 *            La fractale flame
		 */
		public Builder(Flame flame) {
			m_transformationsBuilders = new ArrayList<FlameTransformation.Builder>();
			for(FlameTransformation transformation : flame.m_transforms) {
				m_transformationsBuilders.add(new FlameTransformation.Builder(transformation));
			}
			
			for(FlameStrategy f : FlameStrategy.ALL_STARTEGIES){
				if(f.isSupported()){
					m_strategy = f;
					m_strategy.activate();
					break;
				}
			}
		}
		
		/**
		 * Construit un bâtisseur à partir d'une fractale donnée et de la stratégie donnée
		 * 
		 * @param flame
		 * 			La fractale flame
		 * @param strategy
		 * 			La stratégie à utiliser
		 */
		public Builder(Flame flame, FlameStrategy strategy){
			m_transformationsBuilders = new ArrayList<FlameTransformation.Builder>();
			for(FlameTransformation transformation : flame.m_transforms) {
				m_transformationsBuilders.add(new FlameTransformation.Builder(transformation));
			}
			
			m_strategy = strategy;
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
		 * Change la stratégie de calcul de la fractale
		 * @param strategy
		 * 		Nouvelle stratégie à utiliser.
		 */
		public void setComputeStrategy(FlameStrategy strategy){
			m_strategy = strategy;
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
			
			return m_strategy.createFlame(builtTransformations);
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
		
		public FlameStrategy getComputeStrategy(){
			return m_strategy;
		}
	}
}