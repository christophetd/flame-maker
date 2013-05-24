package ch.epfl.flamemaker.flame;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Rectangle;


/**
 * Classe de base modélisant une fractale flame. Cette classe permet de définir une 
 * fractale par une liste de transformations mais elle doit être étendue pour pouvoir être calculée.<br>
 * <br>
 * Le calcul de la fractal est lancé par l'appel à la méthode {@link #compute(Rectangle, int, int, int)}.
 * Les classes filles doivent implémenter la méthode {@link #doCompute(Rectangle, int, int, int)} qui est
 * lancée dans un processus séparé.<br>
 * Le calcul se fait de manière asynchrone et le résultat est obtenu par le biais des observateurs ({@link Listener}).
 * 
 * @see FlameFactory
 */
public class Flame {
	
	//Contient la liste des transformations caractérisant la fractale
	final private List<FlameTransformation> m_transforms;
	
	// Observateurs écoutant cette fractale
	private List<Listener> m_listeners = new ArrayList<Listener>();
	
	// Indique si le processus de calcul doit être interrompu
	private boolean m_aborted = false;
	
	// Processus de calcul
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
	
	/**
	 * Lance un nouveau calcul de la fractale avec le cadre spécifié et dans un accumulateur de dimentions (width &times; height).
	 * L'avancement et le résultat du calcul peuvent être obtenus avec les méthodes {@link Listener#onComputeDone} et {@link Listener#onComputeDone}.<br />
	 * nb: Cette méthode n'est pas blocante et ne retourne pas le résultat du calcul.
	 * 
	 * @param frame Cadre délimitant la zone à dessiner
	 * @param width Largeur de l'accumulateur
	 * @param height hauteur de l'accumulateur
	 * @param density 
	 * 		Densité utilisée pour le calcul. La densité permet de calculer le nombre total de points générés. 
	 *		Ce nombre peut légèrement varier en fonction de l'implémentation de Flame utilisée, surtout pour de petites dimensions.
	 * 
	 * @see
	 * 		#addListener(Listener)
	 */
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
	
	/**
	 * Les classes filles de Flame doivent réimplémenter cette méthode avec une technique de calcul de la fractale.<br />
	 * Cette méthode est executée dans un processus dédié et peut donc bloquer pendant un certain 
	 * temps pourvu qu'elle signale régulièrement son avancement avec {@link #triggerComputeProgress(int) }<br />
	 * Elle retourne un accumulateur contenant le résultat du calcul.<br />
	 * <br />
	 * L'implémentation par défaut de cette méthode ne fait rien et lève l'exeption {@link UnsupportedOperationException }
	 * 
	 * @param frame Cadre délimitant la zone à dessiner
	 * @param width Largeur de l'accumulateur
	 * @param height hauteur de l'accumulateur
	 * @param density 
	 * 		Densité utilisée pour le calcul. Le nombre total de points calculés doit être au plus proche possible de 
	 * 		width * height * density.
	 * @return Accumulateur contenant le résultat du calcul.
	 * 
	 * @throws UnsupportedOperationException
	 */
	protected FlameAccumulator doCompute(final Rectangle frame, final int width, final int height,
			final int density){
		throw new UnsupportedOperationException("L'implémentation par défaut de flame ne permet pas le rendu !");
	}

	/**
	 * Retire les observateurs, termine le processus de calcul et libère les ressources pour qu'elles puissent être collectées par le garbage collector.
	 */
	public final void destroy(){
		synchronized(m_listeners) {
			m_listeners.clear();
		}
		abort();
	}
	
	/**
	 * Annule le calcul en cours s'il y en a un.
	 */
	public final void abort(){
		if(m_worker != null){
			m_aborted = true;
			try {
				m_worker.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_worker = null;
		}
	}
	
	/**
	 * Vérifie si le rendu doit être arrêté. Utilisez cette méthode dans la redéfinition de {@link #doCompute(Rectangle, int, int, int) } 
	 * pour arrêter le processus de calcul.
	 * @return true si le processus doit s'arrêter
	 */
	protected final boolean isAborted(){
		return m_aborted;
	}
	
	/**
	 * @return Une copie de la liste des transformations définissant la fractale.
	 */
	protected final List<FlameTransformation> getTransforms(){
		return new ArrayList<FlameTransformation>(m_transforms);
	}

	/**
	 * @param index
	 *            L'index de la transformation de laquelle on désire avoir
	 *            l'index de couleur
	 * @return L'index de couleur associé à la transformation
	 */
	protected final double getColorIndex(int index) {
	
		if (index >= 2) {
			double denominateur = Math.pow(2,
					Math.ceil(Math.log(index) / Math.log(2)));
	
			return ((2 * index - 1) % denominateur) / denominateur;
		} else {
			return index;
		}
	}

	
	/**
	 * Ajoute un observateur pour cette fractale
	 * @param l observateur à ajouter
	 * @see Flame.Listener
	 */
	public final void addListener(Listener l){
		synchronized(m_listeners) {
			m_listeners.add(l);
		}
	}
	
	/**
	 * Enlève un observateur pour cette fractale
	 * @param l observateur à enlever
	 * @see Flame.Listener
	 */
	public final void removeListener(Listener l){
		synchronized(m_listeners) {
			m_listeners.remove(l);
		}
	}
	
	/*
	 * Informe les observateurs de la fin du calcul
	 */
	private final void triggerComputeDone(FlameAccumulator acc){
		synchronized(m_listeners) {
			for(Listener l : m_listeners){
				l.onComputeDone(acc);
			}
		}
	}
	
	/**
	 * Informe les observateurs de l'avancement du calcul en cours. Doit être appelé durant le processus de calcul lors de la redéfinition de doCompute.
	 * Cette méthode ne doit pas être appelée trop souvent pour éviter d'impacter les performances.
	 * 
	 * @param percent pourcentage dans l'avancement du calcul.
	 */
	protected final void triggerComputeProgress(int percent){
		synchronized(m_listeners) {
			for(Listener l : m_listeners){
				l.onComputeProgress(percent);
			}
		}
	}
	
	/**
	 * Observateur pour la classe flame. 
	 * Permet de connaître l'avancement du rendu avec {@link #onComputeProgress(int)} 
	 * et d'en récupérer le résultat avec {@link #onComputeDone(FlameAccumulator)}
	 * 
	 * @see Flame#addListener(Listener)
	 */
	public interface Listener {
		
		/**
		 * Callback appelée quand le calcul de la fractale est terminé. Le résultat est passé en paramètre.
		 * @param acc résultat du calcul
		 */
		public void onComputeDone(FlameAccumulator acc);
		
		/**
		 * Callback appelée pour signaler l'avancement du calcul en cours.
		 * @param percent valeur comprise entre 0 et 100 donnant l'avancement du calcul.
		 */
		public void onComputeProgress(int percent);
	}

	/**
	 * Classe modélisant un bâtisseur pour une fractale Flame
	 */
	public final static class Builder {
				
		/* La liste des bâtisseurs pour les transformations de la fractale Flame
		 * qui sera construite */
		private List<FlameTransformation.Builder> m_transformationsBuilders;
		
		/* Strategie qui sera utilisée pour créer la fractale lors de l'appel à build()*/
		private FlameFactory m_strategy;
	
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
			
			for(FlameFactory f : FlameFactory.ALL_FACTORIES){
				if(f.isSupported()){
					m_strategy = f;
					m_strategy.enable();
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
		public Builder(Flame flame, FlameFactory strategy){
			m_transformationsBuilders = new ArrayList<FlameTransformation.Builder>();
			for(FlameTransformation transformation : flame.m_transforms) {
				m_transformationsBuilders.add(new FlameTransformation.Builder(transformation));
			}
			
			m_strategy = strategy;
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
		 * Retourne la transformation à l'index index.
		 * @param index index de la transformation à récupérer
		 * @return transformation d'index index
		 */
		public FlameTransformation getTransformation(int index) {
			return m_transformationsBuilders.get(index).build();
		}

		/**
		 * @return Le nombre actuel de transformations de la fractale
		 */
		public int getTransformationsCount() {
			return m_transformationsBuilders.size();
		}

		/**
		 * @param index
		 * @return La composante affine de la transformation d'index
		 *         <i>index</i> de la fractale
		 * @throws IllegalArgumentException
		 *             Si l'index n'est pas valide
		 */
		public AffineTransformation getAffineTransformation(int index) {
			checkIndex(index);
			
			return m_transformationsBuilders.get(index).affineTransformation();
		}
	
		/**
		 * Retourne la stratégie qui sera utilisée pour fabriquer la fractale
		 * @return stratégie actuellement sélectionnée.
		 */
		public FlameFactory getComputeStrategy(){
			return m_strategy;
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
		public double getVariationWeight(int index, Variation variation) {
			checkIndex(index);
			
			return m_transformationsBuilders.get(index).weight(variation.index());
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
		 * Change la stratégie de calcul de la fractale
		 * @param strategy
		 * 		Nouvelle stratégie à utiliser.
		 */
		public void setComputeStrategy(FlameFactory strategy){
			m_strategy = strategy;
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
	}
}
