package ch.epfl.flamemaker.flame;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class RenderTest {
	public static void main(String args[]) throws Exception {
		
		/*
		 * Settings for triangle de Sierpinski. Comment next line to test.
		 */
		//*
		
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		
		transformations.add(new FlameTransformation(
				new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
				new double[]{1, 0.1, 0, 0, 0, 0}));
		
		transformations.add(new FlameTransformation(
				new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2),
				new double[]{0, 0, 0, 0, 0.8, 1}));
		
		transformations.add(new FlameTransformation(
				new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9),
				new double[]{1, 0, 0, 0, 0, 0}));
		
		Rectangle viewport = new Rectangle(new Point(-0.25, 0), 5, 4);
		int width = 500, height = 400, density = 50;
		//*/
		
		Flame fractal = new Flame(transformations);
		
		System.out.println("Generating fractal...");
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		
		System.out.println("Printing fractal...");
		PrintStream file = new PrintStream("./fractal.pgm");
		
		file.println("P2");
		file.println(result.width()+" "+result.height());
		file.println("100");
		
		for(int i = result.height()-1 ; i > 0 ; i--) {
			System.out.println(100*(result.height()-i)/result.height());
			for(int j = 0; j < result.width(); j++) {
				file.print((int)(result.intensity(j, i)*100)+" ");
			}
			file.print("\n");
		}
		file.close();
		
		System.out.println("Done");
	}
}
