package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

public class FlameMakerGUI {

	private Flame.Builder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private Rectangle frame;
	private int density;
	
	private int m_selectedTransformationId;
	private TreeSet<Listener> m_listeners;
	
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
				lowerPanel = new JPanel(),
				transformationsEditPanel = new JPanel(),
				transformationsPreviewPanel = new JPanel(), 
				fractalPanel = new JPanel();
		
		/* Upper panel */
		window.getContentPane().add(upperPanel, BorderLayout.CENTER);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPreviewPanel.setBackground(new java.awt.Color(9, 9, 9, 10));
		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
	
		
		transformationsPreviewPanel.add(new AffineTransformationsComponent(flameBuilder, frame), BorderLayout.CENTER);
		fractalPanel.add(new FlameBuilderPreviewComponent(flameBuilder, backgroundColor, palette, frame, density), BorderLayout.CENTER);
		
		/* Lower panel */
		window.getContentPane().add(lowerPanel, BorderLayout.PAGE_END);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		
		// Panneau d'édition des transformations
		lowerPanel.add(transformationsEditPanel);
		transformationsEditPanel.setLayout(new BorderLayout());
		
		flameBuilder.addTransformation(new FlameTransformation(new AffineTransformation(1, 0, 1, 0, 0.6, 0), new double[]{1, 0, 0, 0, 0.6, 0}));
		
		JList transformationsList = new JList(new TransformationsListModel(flameBuilder));
		transformationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transformationsList.setVisibleRowCount(3);
		transformationsList.setSelectedIndex(0);
		
		JScrollPane transformationsPane = new JScrollPane(transformationsList);
		
		Object transformationsEditButtons = null; // todo
		
		transformationsEditPanel.add(transformationsPane, BorderLayout.CENTER);
		//transformationsEditPanel.add(transformationsEditButtons, BorderLayout.PAGE_END);
		
		
		window.pack();
		window.setVisible(true);
	}
	
	/**
	 * Gets the currently selected transformation id
	 */
	public int getSelectedTransformationId(){
		return m_selectedTransformationId;
	}
	
	/**
	 * Sets the currently selected transformation id
	 * @param id of the transformation
	 */
	public void setSelectedTransformationId(int id){
		m_selectedTransformationId = id;

		Iterator<Listener> it = m_listeners.iterator();
		while(it.hasNext()){
			it.next().onSelectedTransformationIdChange(id);
		}
	}
	
	/**
	 * Adds a listener notified when a global GUI value change (ie. selectedTransformationId)
	 * @param l listener to add
	 * @see #removeListener
	 */
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	/**
	 * Removes a listener.
	 * @param l listener to remove
	 * @see #addListener
	 */
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	/**
	 *	Interface for GUI listeners.
	 */
	public interface Listener{
		public void onSelectedTransformationIdChange(int id);
	}
}
