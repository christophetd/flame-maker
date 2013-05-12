package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.concurrent.Flame;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.ObservableRectangle;
import ch.epfl.flamemaker.geometry2d.Point;

public class FlameMakerGUI {

	private ObservableFlameBuilder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private ObservableRectangle frame;
	private int density;
	
	private int m_selectedTransformationId;
	private Set<Listener> m_listeners = new HashSet<Listener>();
	
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
		flameBuilder = new ObservableFlameBuilder(new Flame(transformations));
		backgroundColor = Color.BLACK;
		
		ArrayList<Color> paletteColors = new ArrayList<Color>();
		paletteColors.add(Color.RED);
		paletteColors.add(Color.GREEN);
		paletteColors.add(Color.BLUE);
		palette = new InterpolatedPalette(paletteColors);
		
		frame = new ObservableRectangle(new Point(-0.25, 0), 5, 4);
		
		density = 50;
		
	}
	
	public void start() {
		final FlameMakerGUI self = this;
		
		final JFrame window = new JFrame("FlameMaker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.getContentPane().setLayout(new BorderLayout());
		
		JPanel	upperPanel = new JPanel(), 
				lowerPanel = new JPanel(),
				transformationsPreviewPanel = new JPanel(),
				selectedTransformationEditPanel = new JPanel(),
				fractalPanel = new JPanel();
		
		final AffineTransformationsComponent affineTransformationComponent = new AffineTransformationsComponent(flameBuilder, frame);
		
		/* Upper panel */
		window.getContentPane().add(upperPanel, BorderLayout.CENTER);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
	
		
		transformationsPreviewPanel.add(affineTransformationComponent, BorderLayout.CENTER);
		fractalPanel.add(new FlameBuilderPreviewComponent(flameBuilder, backgroundColor, palette, frame, density), BorderLayout.CENTER);
		
		/* Lower panel */
		window.getContentPane().add(lowerPanel, BorderLayout.PAGE_END);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		
		final TransformationsEditPanel transformationsEditPanel = new TransformationsEditPanel(flameBuilder);
		// Panneau d'édition des transformations
		lowerPanel.add(transformationsEditPanel);
		
		/* TODO : rassembler du code dupliqué (ici et un peu plus bas) */
		transformationsEditPanel.addListener(new TransformationsEditPanel.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}
			
		});
		
		
		
		
		
		/* ---- Paneau d'édition de la transformation sélectionnée ---- */
		
		lowerPanel.add(selectedTransformationEditPanel);
		selectedTransformationEditPanel.setLayout(new BoxLayout(selectedTransformationEditPanel, BoxLayout.PAGE_AXIS));
		selectedTransformationEditPanel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));
		
		final AffineModificationComponent affineModificationComponent = new AffineModificationComponent(flameBuilder);
		affineModificationComponent.setSelectedTransformationIndex(0);
		affineTransformationComponent.highlightedTransformationIndex(0);
		
		/* TODO : rassembler du code dupliqué (ici et un peu plus haut) */
		affineTransformationComponent.addListener(new AffineTransformationsComponent.Listener(){

			@Override
			public void onTransformationSelected(int transfoId) {
				self.setSelectedTransformationId(transfoId);
			}
			
		});
		
		selectedTransformationEditPanel.add(affineModificationComponent);
		
		selectedTransformationEditPanel.add(new JSeparator());
		
		final WeightsModificationComponent weightsModificationComponent = new WeightsModificationComponent(flameBuilder);
		weightsModificationComponent.setSelectedTransformationIndex(0);
		selectedTransformationEditPanel.add(weightsModificationComponent);
		
		this.addListener(new Listener(){

			@Override
			public void onSelectedTransformationIdChange(int id) {
				affineTransformationComponent.highlightedTransformationIndex(id);
				affineModificationComponent.setSelectedTransformationIndex(id);
				transformationsEditPanel.setSelectedTransformationIndex(id);
				weightsModificationComponent.setSelectedTransformationIndex(id);
			}
			
		});
		
		/* -------- */
		MenuBar.build(window, flameBuilder, transformationsEditPanel.getListModel());
		
		window.pack();
		window.setLocationRelativeTo(null);
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
		if(m_selectedTransformationId == id)
			return;
		
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
