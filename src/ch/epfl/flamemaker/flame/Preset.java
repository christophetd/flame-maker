/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Classe définissant un préréglage de FlameSet. 
 * Le champ statique ALL_PRESETS recense tous les presets enregistrés 
 * et le champ EMPTY_PRESET contient un set neutre (utile pour faire une nouvelle fractale à partir de zéro).
 * 
 * Un preset définit une liste de transformations pour créer une flame 
 * ainsi que des attributs d'affichage (palette, frame) et est défini par son nom (name).
 *
 */
public class Preset {
	
	/** Preset ne contenant qu'une transformation identité et une palette RGB par défaut */
	public static final Preset EMPTY_PRESET = new Preset(
			"",
			new Rectangle(new Point(0, 0), 3, 3),
			new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
			Arrays.asList(
					new FlameTransformation(
							new AffineTransformation(1, 0, 0, 0, 1, 0),
							new double[] {1, 0, 0, 0, 0, 0})
			));
	
	/**
	 * Contient tous les presets enregistrés dans l'application, rangés par ordre alphabétique.
	 */
	public static final List<Preset> ALL_PRESETS = Arrays.asList(
			new Preset(
					"Bapt",
					new Rectangle(new Point(0, 0), 5, 5),
					new InterpolatedPalette(Arrays.asList(new Color(1,1,1), new Color(0.6, 0.3, 0), new Color(0.1, 0, 0.4))),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(1, -0.3, 0, 1.2, -0.5, 0),
									new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(1, -0.3, 0.3, -.3, 1, 0.3),
	            					new double[] { 1, 0, .4, .4, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(0.3, .3 , -.3, .5, -.5, 0),
	            					new double[] { 1, -0, 0, -.6, .0, 0 })
			)),
			new Preset(
					"Fougère",
					new Rectangle(new Point(0, 4.5), 6, 10),
					new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(0, 0, 0, 0, 0.16, 0),
									new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6),
	            					new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(-0.15, 0.28 , 0, 0.26, 0.24, 0.44),
	            					new double[] { 1, 0, 0, 0, 0, 0 }),
        					new FlameTransformation(
        							new AffineTransformation(0.85, 0.04 , 0, -0.04, 0.85, 1.6),
	            					new double[] { 1, 0, 0, 0, 0, 0 })
			)),
			new Preset(
					"Oeil de verre",
					new Rectangle(new Point(0, 0), 15, 15),
					new InterpolatedPalette(Arrays.asList(new Color(1,1,1), new Color(0.6, 0.3, 0), new Color(0.1, 0, 0.4))),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(0.99, 0, 0, 0, 0.99, 0),
									new double[] {1, .9, 1, 0, 0, 0}),
	            			new FlameTransformation(
	            					new AffineTransformation(-0.70, -0.70, -0.9, 0.70, -0.70, 0),
	            					new double[] { 1, 0, .4, .4, 0, 0 })
			)),
			new Preset(
					"Shark fin",
					new Rectangle(new Point(-0.25, 0), 5, 4),
					new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
									new double[] { 1, 0.1, 0, 0, 0, 0 }),
							new FlameTransformation(
									new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), 
									new double[] { 0, 0, 0, 0, 0.8, 1 }),
							new FlameTransformation(
									new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9), 
									new double[] { 1, 0, 0, 0, 0, 0 })
			)),
			new Preset(
					"Sierpinski",
					new Rectangle(new Point(0.5, 0.5), 1, 1),
					new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(0.5, 0, 0, 0, 0.5, 0),
									new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0),
	            					new double[] { 1, 0, 0, 0, 0, 0 }),
	            			new FlameTransformation(
	            					new AffineTransformation(0.5, 0 , 0.25, 0, 0.5, 0.5),
	            					new double[] { 1, 0, 0, 0, 0, 0 })
			)),
			new Preset(
					"Turbulence",
					new Rectangle(new Point(0.1, 0.1), 3, 3),
					new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE)),
					Arrays.asList(
							new FlameTransformation(
									new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7),
									new double[] { 0.5, 0, 0, 0.4, 0, 0 }),

							new FlameTransformation(
									new AffineTransformation(0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3),
									new double[] { 1, 0, 0.1, 0, 0, 0 }),

							new FlameTransformation(
									new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3),
									new double[] { 1, 0, 0, 0, 0, 0 })
			)));
	
	
	/* Définition des attributs et méthodes d'instance */
	
	private String m_name;
	private Rectangle m_frame;
	private Palette m_palette;
	private List<FlameTransformation> m_transforms;
	
	/**
	 * Constructeur de preset, il est caché car tous les presets doivent être déclarés dans le champ 
	 * ALL_PRESETS pour que l'application puisse les retrouver et les proposer à l'utilisateur.
	 * 
	 * @param name nom du preset
	 * @param frame cadre de l'accumulateur de ce preset
	 * @param palette palette de couleurs utilisée pour le dessin de ce preset
	 * @param transformations liste des transformations utiles pour créer la fractale de ce preset
	 */
	private Preset(String name, Rectangle frame, Palette palette, List<FlameTransformation> transformations){
		m_name = name;
		m_frame = frame;
		m_palette = palette;
		m_transforms = transformations;
	};
	
	/**
	 * Retourne le nom d'affichage du preset
	 * @return une string contenant le nom de ce preset.
	 */
	public String name(){		return m_name; 		}
	
	/**
	 * Retourne le cadre associé à ce preset
	 * @return Rectangle, cadre de l'accumulateur de flame
	 */
	public Rectangle frame(){ 	return m_frame; 	}
	
	/**
	 * Retourne la palette associée à ce preset
	 * @return Palette, palette utilisée pour ce preset
	 */
	public Palette palette(){ 	return m_palette; 	}
	
	/**
	 * Retourne la liste des transformations du preset
	 * @return List de FlameTransformation pour construire la fractale du preset.
	 */
	public List<FlameTransformation> transformations(){ return m_transforms; }
}
