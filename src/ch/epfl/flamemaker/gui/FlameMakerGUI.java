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
				0.7124807, -0.4113509, -0.3, 0.4113513, 0.7124808, -0.7),
				new double[] { 0.5, 0, 0, 0.4, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.3731078, -0.64624117, 0.4, 0.6462414, 0.3731076, 0.3),
				new double[] { 1, 0, 0.1, 0, 0, 0 }));

		transformations.add(new FlameTransformation(new AffineTransformation(
				0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3),
				new double[] { 1, 0, 0, 0, 0, 0 }));
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
		
		transformationsPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
		
		transformationsPanel.add(new JLabel("You lost"));
		fractalPanel.add(new FlameBuilderPreviewComponent(flameBuilder, backgroundColor, palette, frame, density));
		
		
		
		
		window.pack();
		window.setVisible(true);
	}
}
