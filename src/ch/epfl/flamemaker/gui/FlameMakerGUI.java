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
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameMakerGUI {

	private Flame.Builder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private Rectangle frame;
	private int density;
	
	public FlameMakerGUI() {
		flameBuilder = new Flame.Builder(new Flame(new ArrayList<FlameTransformation>()));
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
		
		JPanel upperPanel = new JPanel(), transformationsPanel = new JPanel(), fractalPanel = new JPanel();
		
		window.getContentPane().add(upperPanel, BorderLayout.PAGE_START);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
		
		transformationsPanel.add(new JLabel("Transformations will come here"));
		fractalPanel.add(new JLabel("Fractal will come here"));
		
		
		
		
		window.pack();
		window.setVisible(true);
	}
}
