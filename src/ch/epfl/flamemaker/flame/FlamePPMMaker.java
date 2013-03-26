package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.InterpolatedPalette;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Classe de tests pour le rendu intermédiaire. Génère une fractale "shark fin" et une
 * "turbulence" selon les modèles fournis dans l'énoncé.
 * Affiche des informations sur l'avancement du rendu dans la sortie standard.
 */
public class FlamePPMMaker {
	
	/**
	 * Liste des couleurs utilisées pour le rendu des fractales.
	 */
	private static final ArrayList<Color> colors = new ArrayList<Color>();
	
	/**
	 * Point d'entrée du test. Initialise les couleurs et lance le rendu des deux fractales.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		colors.add(Color.RED); 
		colors.add(Color.GREEN); 
		colors.add(Color.BLUE);
		
		makeTurbulence();
		makeSharkFin();
	}
	
	/**
	 * Génère la fractale de type turbulence
	 * @throws FileNotFoundException
	 */
	private static void makeTurbulence() throws FileNotFoundException{
		System.out.println("Génération de turbulence");
		
		//Tableau des transformations
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		
		//Définition des transformations
		transformations.add(new FlameTransformation(new AffineTransformation(
				0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7),
				new double[] { 0.5, 0, 0, 0.4, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3),
				new double[] { 1, 0, 0.1, 0, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3),
				new double[] { 1, 0, 0, 0, 0, 0 }));

		//Zone de la fractale à dessiner
		Rectangle viewport = new Rectangle(new Point(0.1, 0.1), 3, 3);
		
		//Propriétés de l'accumulateur
		int width = 500, height = 500, density = 50;

		//Création et génération de la fractale
		Flame fractal = new Flame(transformations);
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		
		//Ecriture sur le disque
		printFractal("turbulence", result);
	}
	
	/**
	 * Génère la fractale de type shark fin
	 * @throws FileNotFoundException
	 */
	private static void makeSharkFin() throws FileNotFoundException {
		
		System.out.println("Generation de shark fin");
		
		//Tableau des transformations
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		
		//Définition des transformations
		transformations.add(new FlameTransformation(new AffineTransformation(
				-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
				new double[] { 1, 0.1, 0, 0, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				-0.3957339, 0, -1.6, 0, -0.3957337, 0.2),
				new double[] { 0, 0, 0, 0, 0.8, 1 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.4810169, 0, 1, 0, 0.4810169, 0.9),
				new double[] { 1, 0, 0, 0, 0, 0 }));

		//Zone de la fractale à dessiner
		Rectangle viewport = new Rectangle(new Point(-0.25, 0), 5, 4);
		
		//Propriétés de l'accumulateur
		int width = 500, height = 400, density = 50;

		//Création et génération de la fractale
		Flame fractal = new Flame(transformations);
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		
		//Ecriture sur le disque
		printFractal("shark-fin", result);
	}
	
	/**
	 * Ecris une fractale sur le disque dur en format ppm. 
	 * Le fichier de sortie se nomme name.ppm où name est donnée en argument.
	 * 
	 * @param name nom de la fractale
	 * @param result accumulateur résultant d'une fractale générée
	 * @throws FileNotFoundException lorsqu'une erreur avec le système de fichier survient
	 */
	private static void printFractal(String name, FlameAccumulator result) throws FileNotFoundException {
		
		System.out.println("Ecriture de la fractale : "+name);
		InterpolatedPalette palette = new InterpolatedPalette(colors);
		
		//Ouverture du fichier de sortie
		PrintStream file;
		file = new PrintStream("./"+name+".ppm");
		
		//Ecriture de l'en-tête du format ppm
		file.print("P3\n");
		file.print(result.width()+" "+result.height()+"\n");
		file.print("100\n");
		
		//Ecriture du corps de l'image
		/* On parcours l'accumulateur dans le sens inverse en hauteur pour gérer 
		 * la différence de repère entre la fractale (bas-en-haut) et le format ppm (haut-en-bas)
		 */
		for(int i = result.height()-1 ; i >= 0 ; i--) {
			for(int j = 0; j < result.width(); j++) {
				
				//On récupère les composantes de la couleur du pixel gamma-encodées
				Color currentColor = result.color(palette, Color.BLACK, j, i);
				int r = (int)(Color.sRGBEncode(currentColor.red(), 100));
				int v = (int)(Color.sRGBEncode(currentColor.green(), 100));
				int b = (int)(Color.sRGBEncode(currentColor.blue(), 100));
				
				//Et on les écrit
				file.print(r+" "+v+" "+b+" ");
			}
			file.print("\n");
		}
		
		//Fermeture du fichier
		file.close();
		System.out.println("Fini.");
	}
}
