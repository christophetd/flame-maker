package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder.Listener;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

@SuppressWarnings("serial")
public class AffineTransformationsComponent extends JComponent implements Listener {
	
	private ObservableFlameBuilder m_builder;
	
	private Rectangle m_frame;

	private int m_highlightedTransformationIndex = 2;
	
	private double m_dimension;
	
	private double m_mainLineX;
	private double m_mainLineY;
	
	final private static int UNITIES_PER_MIN_DIMENSION = 6;
	final private static Color BACKGROUND_COLOR = new Color(214, 217, 223, 255);
	
	private double m_unity;
	public AffineTransformationsComponent(ObservableFlameBuilder builder, Rectangle frame) {
		m_builder = builder;
		m_frame = frame;
		
		m_builder.addListener(this);
	}
	
	public void highlightedTransformationIndex(int index) {
		if(index != -1 && index != m_highlightedTransformationIndex) {
			m_highlightedTransformationIndex = index;
			repaint();
		}
	}
	
	public int highlightedTransformationIndex() {
		return m_highlightedTransformationIndex;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		/* Works with or without, dafuq? */
		double ratio = getWidth()/getHeight();
		if(ratio > 0) {
			m_frame = m_frame.expandToAspectRatio(ratio);
		} 
		/**/
		
		Graphics2D g0 = ((Graphics2D) g);
		m_dimension = Math.min(getHeight(), getWidth());
		
		m_unity = m_dimension / UNITIES_PER_MIN_DIMENSION;
		
		// On affiche la grille
		printGrid(g0);
		
		// Puis les transformations
		printTransformations(g0);
	}
	
	private void printGrid(Graphics2D g) {
		// On récupère la couleur actuelle pour la restaurer après l'affichage de la grille
		Color oldColor = g.getColor();
		
		g.setColor(new Color(200, 200, 200, 255));
		
		// On dessine le quadrillage
		for(double x = 0; x < getWidth(); x+=m_unity) {
			g.draw(new Line2D.Double(x, 0, x, getHeight()));
		}
		for(double y = 0; y < getHeight(); y+=m_unity) {
			g.draw(new Line2D.Double(0, y, getWidth(), y));
		}
		
		// Puis les axes principaux
		g.setColor(Color.white);
		
		m_mainLineX = Math.floor((getWidth()/m_unity)/2)*m_unity;
		g.draw(new Line2D.Double(m_mainLineX, 0, m_mainLineX, getHeight()));
		g.draw(new Line2D.Double(m_mainLineX+1, 0, m_mainLineX+1, getHeight()));
		
		m_mainLineY = Math.ceil((getHeight()/m_unity)/2)*m_unity;
		g.draw(new Line2D.Double(0, m_mainLineY, getWidth(), m_mainLineY));
		g.draw(new Line2D.Double(0, m_mainLineY+1, getWidth(), m_mainLineY+1));
		
		g.setColor(oldColor);
	}
	
	public void printTransformations(Graphics2D g) {
		// On récupère la couleur actuelle pour la restaurer après l'affichage de la grille
		Color oldColor = g.getColor();
		
		g.setColor(Color.black);
		
		Transformation gridMapper =	AffineTransformation.newTranslation(m_mainLineX, m_mainLineY)
				.composeWith(AffineTransformation.newScaling(m_unity, m_unity))
				.composeWith(new AffineTransformation(1, 0, 0, 0, -1, 0));
		
		Transformation transfo;
		
		Arrow horizontalArrow, verticalArrow;
		Point[] horizontalArrowCoordinates = new Point[]{ new Point(-1, 0), new Point(1, 0) };
		Point[] verticalArrowCoordinates = new Point[]{ new Point(0, -1), new Point(0, 1) };
		
		// On commence par dessiner toutes les transformations, sauf celle qui est surlignée
		for(int numTransfo = 0; numTransfo < m_builder.transformationsCount(); numTransfo++) {
			if(numTransfo == m_highlightedTransformationIndex) continue;
			
			transfo = m_builder.affineTransformation(numTransfo);

			horizontalArrow = new Arrow(horizontalArrowCoordinates[0], horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0], verticalArrowCoordinates[1]);
			
			horizontalArrow.applyTransformation(transfo).draw(g, gridMapper);
			verticalArrow.applyTransformation(transfo).draw(g, gridMapper);
		}
		if(m_highlightedTransformationIndex != -1) {
			/* On dessine la transformation surlignée (en dernier pour qu'elle 
			 * s'affiche au dessus des autres s'il y a un chevauchement) */
			g.setColor(Color.red);

			transfo = m_builder.affineTransformation(m_highlightedTransformationIndex);
			horizontalArrow = new Arrow(horizontalArrowCoordinates[0], horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0], verticalArrowCoordinates[1]);
			
			horizontalArrow.applyTransformation(transfo).draw(g, gridMapper);
			verticalArrow.applyTransformation(transfo).draw(g, gridMapper);
		}

		g.setColor(oldColor);
	}
	
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension((int) m_frame.width(), (int) m_frame.height());
	}

	@Override
	public void onFlameBuilderChange(ObservableFlameBuilder b) {
		repaint();
	}
	
}