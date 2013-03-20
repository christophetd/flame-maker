package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.InterpolatedPalette;
import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlamePPMMaker {
	
	private static final ArrayList<Color> colors = new ArrayList<Color>();
	
	public static void main(String[] args) throws FileNotFoundException {
		
		colors.add(Color.RED); 
		colors.add(Color.GREEN); 
		colors.add(Color.BLUE);
		
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		transformations.add(new FlameTransformation(new AffineTransformation(
				0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7),
				new double[] { 0.5, 0, 0, 0.4, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3),
				new double[] { 1, 0, 0.1, 0, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3),
				new double[] { 1, 0, 0, 0, 0, 0 }));

		
		Rectangle viewport = new Rectangle(new Point(0.1, 0.1), 3, 3);
		int width = 500, height = 500, density = 50;

		Flame fractal = new Flame(transformations);
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		
		printFractal("turbulence", result);
	}
	
	private static void printFractal(String name, FlameAccumulator result) throws FileNotFoundException {
		
		System.out.println("Printing fractal : "+name);
		InterpolatedPalette palette = new InterpolatedPalette(colors);
		
		PrintStream file;

		file = new PrintStream("./"+name+".ppm");
		
		file.println("P3");
		file.println(result.width()+" "+result.height());
		file.println("100");
		for(int i = result.height()-1 ; i > 0 ; i--) {
			for(int j = 0; j < result.width(); j++) {
				Color currentColor = result.color(palette, Color.BLACK, j, i);
				int r = (int)(Color.sRGBEncode(currentColor.red(), 100));
				int v = (int)(Color.sRGBEncode(currentColor.green(), 100));
				int b = (int)(Color.sRGBEncode(currentColor.blue(), 100));
				
				file.print(r+" "+v+" "+b+" ");
			}
			file.print("\n");
		}
		file.close();
		System.out.println("Done.");
	}
}
