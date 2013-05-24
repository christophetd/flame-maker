package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

/**
 * Définit une façon de calculer une fractale Flame par une variante du pattern factory.<br>
 * <br>
 * Cette classe indique les propriétés de la technique de rendu ( {@link #name()}, {@link #isSupported() } )
 * et gère les ressources de celles-ci grâce à {@link #enable()} et {@link #disable() }.<br>
 * <br>
 * Elle doit instancier une sous-classe de Flame implémentant la technique de rendu en question grâce
 * à la méthode {@link #createFlame(List) }
 *
 */
public abstract class FlameFactory{
	
	/**
	 * Liste des stratégies disponibles pour le rendu de la fractale triée par ordre de compatibilité
	 * (moins compatible en premier, plus compatible à la fin)
	 */
	public static final List<FlameFactory> ALL_FACTORIES = Arrays.asList(
		new OpenCLFlameFactory(),
		new ThreadFlameFactory(),
		new DefaultFlameFactory()
	);
	
	/**
	 * Nom de la technique de rendu
	 * @return Une String contenant le nom de la technique de rendu
	 */
	public abstract String name();
	
	/**
	 * Indique si la technique est supportée par l'environnement d'execution.
	 * @return true si elle est supportée, false sinon.
	 */
	public abstract boolean isSupported();
	
	/**
	 * Cette méthode peut être redéfinie pour allouer certaines ressources partagées par les différentes instances
	 * de Flame issue de cette stratégie de calcul.<br>
	 * Faire ici toutes les initialisations qui n'ont pas besoin de se trouver dans le rendu pour accélérer ce dernier.
	 * 
	 * @see #disable()
	 */
	public void enable() {}
	
	/**
	 * Libère toutes les ressources allouées avec activate. 
	 * Deactivate est toujours appelée si l'utilisateur n'a plus besoin de cette stratégie sauf s'il ferme le programme avant.
	 * 
	 * @see #enable()
	 */
	public void disable() {}
	
	/**
	 * Instancie une nouvelle Flame implémentant la stratégie courante.
	 * Il est préférable de créer une classe imbriquée privée étendant Flame
	 * et de l'instancier grâce à cette méthode afin de respecter le principe d'encapsulation.
	 * 
	 * @param transformations liste des transformations à passer au constructeur de Flame
	 * @return Une instance de Flame implémentant la stratégie de calcul
	 */
	public abstract Flame createFlame(List<FlameTransformation> transformations);
}
