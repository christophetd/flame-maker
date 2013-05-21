/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.flame.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

@SuppressWarnings("serial")
public class AffineTransformationsComponent extends JComponent implements
		ObservableFlameBuilder.Listener {

	private ObservableFlameBuilder m_builder;

	private Rectangle m_frame;

	private int m_highlightedTransformationIndex = 0;

	public AffineTransformationsComponent(ObservableFlameBuilder builder,
			Rectangle frame) {
		m_builder = builder;
		m_frame = frame;

		m_builder.addListener(this);
	}

	public void highlightedTransformationIndex(int index) {
		if (index != -1 && index != m_highlightedTransformationIndex) {
			m_highlightedTransformationIndex = index;
			repaint();
		}
	}

	public int highlightedTransformationIndex() {
		return m_highlightedTransformationIndex;
	}

	@Override
	public void paintComponent(Graphics g) {
		double width = getWidth(), height = getHeight();

		if (width == 0 || height == 0)
			return;

		Rectangle realFrame = m_frame.expandToAspectRatio((double) width
				/ height);

		Graphics2D g0 = (Graphics2D) g;

		// On affiche la grille
		printGrid(g0, realFrame);

		// Puis les transformations
		printTransformations(g0, realFrame);
	}

	private void printGrid(Graphics2D g, Rectangle frame) {

		// Ratio pour convertir les coordonnées de la frame vers celles de la
		// vue
		double scaleRatio = getWidth() / frame.width();

		// On récupère la couleur actuelle pour la restaurer après l'affichage
		// de la grille
		Color oldColor = g.getColor();

		g.setColor(new Color(200, 200, 200, 255));

		// On dessine le quadrillage
		double fX = Math.floor(frame.left());
		for (double x = fX; x < frame.right(); x++) {
			double xPos = (x - frame.left()) * scaleRatio;
			g.draw(new Line2D.Double(xPos, 0, xPos, getHeight()));
		}

		double fY = Math.floor(frame.bottom());
		for (double y = fY; y < frame.top(); y++) {
			double yPos = (y - frame.bottom()) * scaleRatio;
			g.draw(new Line2D.Double(0, getHeight() - yPos, getWidth(),
					getHeight() - yPos));
		}

		// Puis les axes principaux
		g.setColor(Color.white);

		int zeroX = (int) ((-frame.left()) * scaleRatio);
		if (zeroX > 0 && zeroX < getWidth()) {
			g.draw(new Line2D.Double(zeroX, 0, zeroX, getHeight()));
			g.draw(new Line2D.Double(zeroX + 1, 0, zeroX + 1, getHeight()));
		}
		int zeroY = (int) ((-frame.bottom()) * scaleRatio);
		if (zeroY > 0 && zeroY < getHeight()) {
			g.draw(new Line2D.Double(0, getHeight() - zeroY, getWidth(),
					getHeight() - zeroY));
			g.draw(new Line2D.Double(0, getHeight() - zeroY - 1, getWidth(),
					getHeight() - zeroY - 1));
		}

		g.setColor(oldColor);
	}

	public void printTransformations(Graphics2D g, Rectangle frame) {
		double scaleRatio = getWidth() / frame.width();

		// On récupère la couleur actuelle pour la restaurer après l'affichage
		// de la grille
		Color oldColor = g.getColor();

		g.setColor(Color.black);

		Transformation gridMapper = AffineTransformation
				.newScaling(scaleRatio, scaleRatio)
				.composeWith(new AffineTransformation(1, 0, 0, 0, -1, 0))
				.composeWith(
						AffineTransformation.newTranslation(-frame.left(),
								-frame.top()));

		Transformation transfo;

		Arrow horizontalArrow, verticalArrow;
		Point[] horizontalArrowCoordinates = new Point[] { new Point(-1, 0),
				new Point(1, 0) };
		Point[] verticalArrowCoordinates = new Point[] { new Point(0, -1),
				new Point(0, 1) };

		// On commence par dessiner toutes les transformations, sauf celle qui
		// est surlignée
		for (int numTransfo = 0; numTransfo < m_builder.transformationsCount(); numTransfo++) {

			transfo = m_builder.affineTransformation(numTransfo);

			horizontalArrow = new Arrow(horizontalArrowCoordinates[0],
					horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0],
					verticalArrowCoordinates[1]);

			horizontalArrow.applyTransformation(transfo).applyTransformation(
					gridMapper);
			verticalArrow.applyTransformation(transfo).applyTransformation(
					gridMapper);

			if (numTransfo != m_highlightedTransformationIndex) {
				horizontalArrow.draw(g);
				verticalArrow.draw(g);
			}
		}

		if (m_highlightedTransformationIndex != -1) {
			/*
			 * On dessine la transformation surlignée (en dernier pour qu'elle
			 * s'affiche au dessus des autres s'il y a un chevauchement)
			 */
			g.setColor(Color.red);

			transfo = m_builder
					.affineTransformation(m_highlightedTransformationIndex);
			horizontalArrow = new Arrow(horizontalArrowCoordinates[0],
					horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0],
					verticalArrowCoordinates[1]);

			horizontalArrow.applyTransformation(transfo)
					.applyTransformation(gridMapper).draw(g);
			verticalArrow.applyTransformation(transfo)
					.applyTransformation(gridMapper).draw(g);
		}

		g.setColor(oldColor);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension((int) m_frame.width(), (int) m_frame.height());
	}

	@Override
	public void onFlameBuilderChange(ObservableFlameBuilder b) {
		repaint();
	}
}