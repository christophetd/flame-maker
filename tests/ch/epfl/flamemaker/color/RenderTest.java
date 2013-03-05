package ch.epfl.flamemaker.color;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ch.epfl.flamemaker.InterpolatedPalette;
import ch.epfl.flamemaker.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameAccumulator;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class RenderTest {
	
	public static void main(String[] args) throws Exception {
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
		
		double start = System.currentTimeMillis();
		System.out.println("Generating fractal...");
		
		FlameAccumulator result = fractal.compute(viewport, width, height, density);
		
		double time = System.currentTimeMillis()-start;
		System.out.println("Generated in "+time/1000+"s");
		System.out.println("Printing fractal...");
		
		int r, v, b;
		Color currentColor;
		List<Color> c = new ArrayList<Color>();
		c.add(Color.RED); c.add(Color.GREEN); c.add(Color.BLUE);
		Palette palette = new InterpolatedPalette(c);
		PrintStream file = new PrintStream("./fractal.ppm");
		file.println("P3");
		file.println(result.width()+" "+result.height());
		file.println("100");
		for(int i = result.height()-1 ; i > 0 ; i--) {
			for(int j = 0; j < result.width(); j++) {
				currentColor = result.color(palette, Color.BLACK, j, i);
				file.print((int)(currentColor.red()*100)+" "+(int)(currentColor.green()*100)+" "+(int)(currentColor.blue()*100)+" ");
			}
			file.print("\n");
		}
		file.close();
		System.out.println("Done");
	}
}
