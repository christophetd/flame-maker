package ch.epfl.flamemaker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlameMakerGUI {

	private ObservableFlameBuilder flameBuilder;
	private Color backgroundColor;
	private Palette palette;
	private Rectangle frame;
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
		
		frame = new Rectangle(new Point(-0.25, 0), 5, 4);
		
		density = 50;
		
	}
	
	public void start() {
		final FlameMakerGUI self = this;
		
		JFrame window = new JFrame("FlameMaker");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.getContentPane().setLayout(new BorderLayout());
		
		JPanel	upperPanel = new JPanel(), 
				lowerPanel = new JPanel(),
				transformationsEditPanel = new JPanel(),
				transformationsPreviewPanel = new JPanel(), 
				transformationsEditButtons = new JPanel(),
				selectedTransformationEditPanel = new JPanel(),
				fractalPanel = new JPanel();
		
		final AffineTransformationsComponent affineTransformationComponent = new AffineTransformationsComponent(flameBuilder, frame);
		
		/* Upper panel */
		window.getContentPane().add(upperPanel, BorderLayout.CENTER);
		upperPanel.setLayout(new GridLayout(1, 2));
		upperPanel.add(transformationsPreviewPanel);
		upperPanel.add(fractalPanel);
		
		transformationsPreviewPanel.setBackground(new java.awt.Color(229, 229, 229, 255));
		transformationsPreviewPanel.setLayout(new BorderLayout());
		transformationsPreviewPanel.setBorder(BorderFactory.createTitledBorder("Transformations affines"));
		
		this.addListener(new Listener(){

			@Override
			public void onSelectedTransformationIdChange(int id) {
				affineTransformationComponent.highlightedTransformationIndex(id);
			}
			
		});
		fractalPanel.setLayout(new BorderLayout());
		fractalPanel.setBorder(BorderFactory.createTitledBorder("Fractale"));
	
		
		transformationsPreviewPanel.add(affineTransformationComponent, BorderLayout.CENTER);
		fractalPanel.add(new FlameBuilderPreviewComponent(flameBuilder, backgroundColor, palette, frame, density), BorderLayout.CENTER);
		
		/* Lower panel */
		window.getContentPane().add(lowerPanel, BorderLayout.PAGE_END);
		lowerPanel.setLayout(new BoxLayout(lowerPanel, BoxLayout.LINE_AXIS));
		
		// Panneau d'édition des transformations
		lowerPanel.add(transformationsEditPanel);
		transformationsEditPanel.setLayout(new BorderLayout());
		transformationsEditPanel.setBorder(BorderFactory.createTitledBorder("Transformations"));
		
		flameBuilder.addTransformation(new FlameTransformation(new AffineTransformation(2, 1, 0, 1, 2, 0), new double[]{1, 0, 0, 0, 0.6, 0}));
		
		final TransformationsListModel listModel = new TransformationsListModel(flameBuilder);
		final JList transformationsList = new JList(listModel);
		transformationsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		transformationsList.setVisibleRowCount(5);
		transformationsList.setSelectedIndex(0);
		transformationsList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				self.setSelectedTransformationId(transformationsList.getSelectedIndex());
			}
		});
		JScrollPane transformationsPane = new JScrollPane(transformationsList);
		
		transformationsEditPanel.add(transformationsPane, BorderLayout.CENTER);
		transformationsEditPanel.add(transformationsEditButtons, BorderLayout.PAGE_END);
		
		transformationsEditButtons.setLayout(new GridLayout(1, 2));

		// Bouton 'supprimer'
		final 
		JButton deleteTransformationButton = new JButton("Supprimer");
		deleteTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedIndex = self.getSelectedTransformationId();

				if(selectedIndex != -1) {
					listModel.removeTransformation(selectedIndex);
					transformationsList.setSelectedIndex(Math.max(0, --selectedIndex));
				}
				if(self.flameBuilder.transformationsCount() == 1) {
					deleteTransformationButton.setEnabled(false);
				}
				affineTransformationComponent.repaint();
			}
		});
		
		JButton addTransformationButton = new JButton("Ajouter");
		transformationsEditButtons.add(addTransformationButton);
		transformationsEditButtons.add(deleteTransformationButton);
		
		
		addTransformationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				listModel.addTransformation(new FlameTransformation(
						AffineTransformation.IDENTITY, 
						new double[]{1, 0, 0, 0, 0, 0}						
				));
				int newHighlightedIndex = self.flameBuilder.transformationsCount()-1;
				self.setSelectedTransformationId(newHighlightedIndex);
				transformationsList.setSelectedIndex(newHighlightedIndex);
				if(!deleteTransformationButton.isEnabled() && self.flameBuilder.transformationsCount() > 1) {
					deleteTransformationButton.setEnabled(true);
				}
				affineTransformationComponent.repaint();
			}
		});
		
		/* ---- Paneau d'édition de la transformation sélectionnée ---- */
		
		lowerPanel.add(selectedTransformationEditPanel);
		selectedTransformationEditPanel.setLayout(new BoxLayout(selectedTransformationEditPanel, BoxLayout.PAGE_AXIS));
		selectedTransformationEditPanel.setBorder(BorderFactory.createTitledBorder("Transformation courante"));
		
		JPanel affineEditPanel = new JPanel();
		selectedTransformationEditPanel.add(affineEditPanel);
		
		GroupLayout affineGroup = new GroupLayout(affineEditPanel);
		
		affineEditPanel.setLayout(affineGroup);
		
		// Mise en page de l'édition de la partie affine
		SequentialGroup H = affineGroup.createSequentialGroup();
		affineGroup.setHorizontalGroup(H);
		SequentialGroup V = affineGroup.createSequentialGroup();
		affineGroup.setVerticalGroup(V);
		
		ParallelGroup H1 = affineGroup.createParallelGroup();
		H.addGroup(H1);
		ParallelGroup H2 = affineGroup.createParallelGroup();
		H.addGroup(H2);
		ParallelGroup H3 = affineGroup.createParallelGroup();
		H.addGroup(H3);
		ParallelGroup H4 = affineGroup.createParallelGroup();
		H.addGroup(H4);
		ParallelGroup H5 = affineGroup.createParallelGroup();
		H.addGroup(H5);
		ParallelGroup H6 = affineGroup.createParallelGroup();
		H.addGroup(H6);
		
		ParallelGroup V1 = affineGroup.createParallelGroup();
		V.addGroup(V1);
		ParallelGroup V2 = affineGroup.createParallelGroup();
		V.addGroup(V2);
		ParallelGroup V3 = affineGroup.createParallelGroup();
		V.addGroup(V3);
		ParallelGroup V4 = affineGroup.createParallelGroup();
		V.addGroup(V4);
		
		
		// Ligne "translation"
		JLabel translationLabel = new JLabel("Translation");
		H1.addComponent(translationLabel);
		V1.addComponent(translationLabel);
		
		JFormattedTextField translationFactor = new JFormattedTextField();
		translationFactor.setColumns(3);
		translationFactor.setValue(new Double(1));
		H2.addComponent(translationFactor);
		V1.addComponent(translationFactor);
		
		JButton translationLeftButton = new JButton("←");
		H3.addComponent(translationLeftButton);
		V1.addComponent(translationLeftButton);
		
		JButton translationRightButton = new JButton("→");
		H4.addComponent(translationRightButton);
		V1.addComponent(translationRightButton);
		
		JButton translationUpButton = new JButton("↑");
		H5.addComponent(translationUpButton);
		V1.addComponent(translationUpButton);
		
		JButton translationDownButton = new JButton("↓");
		H6.addComponent(translationDownButton);
		V1.addComponent(translationDownButton);
		
		
		// Ligne "Rotation"
		JLabel rotationLabel = new JLabel("Rotation");
		H1.addComponent(rotationLabel);
		V2.addComponent(rotationLabel);
		
		JFormattedTextField rotationFactor = new JFormattedTextField();
		rotationFactor.setValue(new Double(1));
		H2.addComponent(rotationFactor);
		V2.addComponent(rotationFactor);
		
		JButton rotationPositiveButton = new JButton("↺");
		H3.addComponent(rotationPositiveButton);
		V2.addComponent(rotationPositiveButton);
		
		JButton rotationNegativeButton = new JButton("↻");
		H4.addComponent(rotationNegativeButton);
		V2.addComponent(rotationNegativeButton);
		
		
		// Ligne "Dilatation"
		JLabel dilatationLabel = new JLabel("Dilatation");
		H1.addComponent(dilatationLabel);
		V3.addComponent(dilatationLabel);
		
		JFormattedTextField dilatationFactor = new JFormattedTextField();
		dilatationFactor.setValue(new Double(1));
		H2.addComponent(dilatationFactor);
		V3.addComponent(dilatationFactor);
		
		JButton dilatationHPlusButton = new JButton("+ ↔");
		H3.addComponent(dilatationHPlusButton);
		V3.addComponent(dilatationHPlusButton);
		
		JButton dilatationHMinusButton = new JButton("- ↔");
		H4.addComponent(dilatationHMinusButton);
		V3.addComponent(dilatationHMinusButton);
		
		JButton dilatationVPlusButton = new JButton("+ ↕");
		H5.addComponent(dilatationVPlusButton);
		V3.addComponent(dilatationVPlusButton);
		
		JButton dilatationVMinusButton = new JButton("- ↕");
		H6.addComponent(dilatationVMinusButton);
		V3.addComponent(dilatationVMinusButton);
		
		
		// Ligne "Transvection"
		JLabel transvectionLabel = new JLabel("Transvection");
		H1.addComponent(transvectionLabel);
		V4.addComponent(transvectionLabel);
		
		JFormattedTextField transvectionFactor = new JFormattedTextField();
		transvectionFactor.setValue(new Double(1));
		H2.addComponent(transvectionFactor);
		V4.addComponent(transvectionFactor);
		
		JButton transvectionLeftButton = new JButton("←");
		H3.addComponent(transvectionLeftButton);
		V4.addComponent(transvectionLeftButton);
		
		JButton transvectionRightButton = new JButton("→");
		H4.addComponent(transvectionRightButton);
		V4.addComponent(transvectionRightButton);
		
		JButton transvectionUpButton = new JButton("↑");
		H5.addComponent(transvectionUpButton);
		V4.addComponent(transvectionUpButton);
		
		JButton transvectionDownButton = new JButton("↓");
		H6.addComponent(transvectionDownButton);
		V4.addComponent(transvectionDownButton);
		

		// ← → ↑ ↓ ⟲ ⟳ ↔ ↕
		/* -------- */
		
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
