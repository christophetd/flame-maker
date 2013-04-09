package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameMakerGUI {

	private Flame.Builder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private Rectangle frame;
	private int density;
	
	public FlameMakerGUI() {
		// Tableau des transformations
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();

		// Définition des transformations
		transformations.add(new FlameTransformation(new AffineTransformation(
				-0.4113504, -0.7124804, -0.4, 0.7124795, -0.4113508, 0.8),
				new double[] { 1, 0.1, 0, 0, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				-0.3957339, 0, -1.6, 0, -0.3957337, 0.2), new double[] { 0, 0,
				0, 0, 0.8, 1 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.4810169, 0, 1, 0, 0.4810169, 0.9), new double[] { 1, 0, 0, 0,
				0, 0 }));
		flameBuilder = new Flame.Builder(new Flame(transformations));
		backgroundColor = Color.BLACK;
		
		ArrayList<Color> paletteColors = new ArrayList<Color>();
		paletteColors.add(Color.RED);
		paletteColors.add(Color.GREEN);
		paletteColors.add(Color.BLUE);
		palette = new InterpolatedPalette(paletteColors);
		
		frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		
		density = 50;
		
	}
	
	public void start() {
		JFrame window = new JFrame("FlameMaker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.getContentPane().setLayout(new BorderLayout());
		
		JPanel	upperPanel = new JPanel(), 
				transformationsPanel = new JPanel(), 
				fractalPanel = new JPanel();
		
		window.getContentPane().add(upperPanel, BorderLayout.CENTER);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPanel.setLayout(new BorderLayout());
		transformationsPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
	
		
		//transformationsPanel.add(new AffineTransformationsComponent(flameBuilder, frame), BorderLayout.CENTER);
		fractalPanel.add(new FlameBuilderPreviewComponent(flameBuilder, backgroundColor, palette, frame, density), BorderLayout.CENTER);
		

		
		
		window.pack();
		window.setVisible(true);
	}
}
