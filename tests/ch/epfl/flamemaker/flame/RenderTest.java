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
		/*
		
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
		
		/*
		 * Settings for fractal turbulence
		 * Comment next line to test */
		 //*
		  ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		  transformations.add(new FlameTransformation(
				  new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7), 
				  new double[]{ 0.5, 0, 0, 0.4, 0, 0 }
		  ));
		  
		  transformations.add(new FlameTransformation(
				  new AffineTransformation(0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3), 
				  new double[]{ 1, 0, 0.1, 0, 0, 0 }
		  ));
		  
		  transformations.add(new FlameTransformation(
				  new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3), 
				  new double[]{ 1, 0, 0, 0, 0, 0 }
		  ));
		  
		  Rectangle viewport = new Rectangle(new Point(0.1, 0.1), 3, 3);
			int width = 500, height = 500, density = 50;
		 //*/
		Flame fractal = new Flame(transformations);
	
		double start = System.currentTimeMillis();
		System.out.println("Generating fractal...");
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		double time = System.currentTimeMillis()-start;
		System.out.println("Generated in "+time/1000+"s");
		System.out.println("Printing fractal...");
		PrintStream file = new PrintStream("./fractal.pgm");
		
		file.println("P2");
		file.println(result.width()+" "+result.height());
		file.println("100");
		
		for(int i = result.height()-1 ; i > 0 ; i--) {
			for(int j = 0; j < result.width(); j++) {
				file.print((int)(result.intensity(j, i)*100)+" ");
			}
			file.print("\n");
		}
		file.close();
		
	}
}