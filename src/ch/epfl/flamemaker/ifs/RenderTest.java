package ch.epfl.flamemaker.ifs;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class RenderTest {
	public static void main(String args[]) throws Exception {
		PrintStream file = new PrintStream("/home/christophetd/fractal.pbm");
		
		/*
		 * Transformations for fougère
		 * ArrayList<AffineTransformation> fTransformations = new ArrayList<AffineTransformation>();
		fTransformations.add(new AffineTransformation(0, 0, 0, 0, 0.16, 0));
		fTransformations.add(new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6));
		fTransformations.add(new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44));
		fTransformations.add(new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6));
		*/
		ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();
		transformations.add(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5));
		
		IFS fractal = new IFS(transformations);
		IFSAccumulator result = fractal.compute(new Rectangle(new Point(0.5, 0.5), 3, 3), 300, 300, 3);
		

		
		file.println("P1");
		file.println(result.width()+" "+result.height());
		
		for(int i = 0; i < result.width(); i++) {
			for(int j = 0; j < result.height(); j++) {
				file.print(result.isHit(i, j) ? "1 " : "0 ");
			}
			file.print("\n");
		}
		file.close();
	}
}
