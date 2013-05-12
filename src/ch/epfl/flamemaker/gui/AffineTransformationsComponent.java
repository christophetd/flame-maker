package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.flamemaker.concurrent.ObservableFlameBuilder;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.ObservableRectangle;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

@SuppressWarnings("serial")
public class AffineTransformationsComponent extends JComponent implements ObservableFlameBuilder.Listener, MouseListener, ObservableRectangle.Listener {
	
	private ObservableFlameBuilder m_builder;
	
	private ObservableRectangle m_frame;

	private int m_highlightedTransformationIndex = 2;
	
	private List<Rectangle> m_boundingBoxes = new ArrayList<Rectangle>();
	
	private List<Listener> m_listeners = new LinkedList<Listener>();
	
	final private static Color BACKGROUND_COLOR = new Color(214, 217, 223, 255);
	
	public AffineTransformationsComponent(ObservableFlameBuilder builder, ObservableRectangle frame) {
		m_builder = builder;
		m_frame = frame;
		
		m_frame.addListener(this);
		
		m_builder.addListener(this);
		
		this.addMouseListener(this);
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
		double width = getWidth(), height = getHeight();
		
		if(width == 0 || height == 0)
			return;
		
		Rectangle realFrame = m_frame.expandToAspectRatio((double)width/height);
		
		Graphics2D g0 = (Graphics2D) g;
		
		// On affiche la grille
		printGrid(g0, realFrame);
		
		// Puis les transformations
		printTransformations(g0, realFrame);
	}
	
	private void printGrid(Graphics2D g, Rectangle frame) {
		
		// Ratio pour convertir les coordonnées de la frame vers celles de la vue
		double scaleRatio = getWidth()/frame.width();
		
		// On récupère la couleur actuelle pour la restaurer après l'affichage de la grille
		Color oldColor = g.getColor();
		
		g.setColor(new Color(200, 200, 200, 255));
		
		// On dessine le quadrillage
		double fX = Math.floor(frame.left());
		for(double x = fX ; x < frame.right(); x++) {
			double xPos = (x - frame.left())*scaleRatio;
			g.draw(new Line2D.Double(xPos, 0, xPos, getHeight()));
		}
		
		double fY = Math.floor(frame.bottom());
		for(double y = fY ; y < frame.top(); y++) {
			double yPos = (y - frame.bottom())*scaleRatio;
			g.draw(new Line2D.Double(0, getHeight() - yPos, getWidth(), getHeight() - yPos));
		}
		
		// Puis les axes principaux
		g.setColor(Color.white);
		
		int zeroX = (int)((-frame.left())*scaleRatio);
		if(zeroX > 0 && zeroX < getWidth()){
			g.draw(new Line2D.Double(zeroX, 0, zeroX, getHeight()));
			g.draw(new Line2D.Double(zeroX+1, 0, zeroX+1, getHeight()));
		}
		int zeroY = (int)((-frame.bottom())*scaleRatio);
		if(zeroY > 0 && zeroY < getHeight()){
			g.draw(new Line2D.Double(0, getHeight() - zeroY, getWidth(), getHeight() - zeroY));
			g.draw(new Line2D.Double(0, getHeight() - zeroY-1, getWidth(), getHeight() - zeroY-1));
		}
		
		g.setColor(oldColor);
	}
	
	public void printTransformations(Graphics2D g, Rectangle frame) {
		double scaleRatio = getWidth()/frame.width();
		
		m_boundingBoxes.clear();
		
		// On récupère la couleur actuelle pour la restaurer après l'affichage de la grille
		Color oldColor = g.getColor();
		
		g.setColor(Color.black);
		
		Transformation gridMapper =	AffineTransformation.newScaling(scaleRatio, scaleRatio)
				.composeWith(new AffineTransformation(1, 0, 0, 0, -1, 0))
				.composeWith(AffineTransformation.newTranslation(-frame.left(), -frame.top()));
		
		Transformation transfo;
		
		Arrow horizontalArrow, verticalArrow;
		Point[] horizontalArrowCoordinates = new Point[]{ new Point(-1, 0), new Point(1, 0) };
		Point[] verticalArrowCoordinates = new Point[]{ new Point(0, -1), new Point(0, 1) };
		
		// On commence par dessiner toutes les transformations, sauf celle qui est surlignée
		for(int numTransfo = 0; numTransfo < m_builder.transformationsCount(); numTransfo++) {
			
			transfo = m_builder.affineTransformation(numTransfo);

			horizontalArrow = new Arrow(horizontalArrowCoordinates[0], horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0], verticalArrowCoordinates[1]);
			
			horizontalArrow
				.applyTransformation(transfo)
				.applyTransformation(gridMapper);
			verticalArrow
				.applyTransformation(transfo)
				.applyTransformation(gridMapper);
			
			Rectangle bb = makeBoundingBox(horizontalArrow, verticalArrow);
			m_boundingBoxes.add(bb);
			
			if(numTransfo != m_highlightedTransformationIndex){
				horizontalArrow.draw(g);
				verticalArrow.draw(g);
			}
		}
		
		if(m_highlightedTransformationIndex != -1) {
			/* On dessine la transformation surlignée (en dernier pour qu'elle 
			 * s'affiche au dessus des autres s'il y a un chevauchement) */
			g.setColor(Color.red);

			transfo = m_builder.affineTransformation(m_highlightedTransformationIndex);
			horizontalArrow = new Arrow(horizontalArrowCoordinates[0], horizontalArrowCoordinates[1]);
			verticalArrow = new Arrow(verticalArrowCoordinates[0], verticalArrowCoordinates[1]);
			
			horizontalArrow
				.applyTransformation(transfo)
				.applyTransformation(gridMapper)
				.draw(g);
			verticalArrow
				.applyTransformation(transfo)
				.applyTransformation(gridMapper)
				.draw(g);
		}

		g.setColor(oldColor);
	}
	
	private static Rectangle makeBoundingBox(Arrow a1, Arrow a2){
		List<Point> points = Arrays.asList(
				a1.from(), a1.to(), a1.leftComponent(), a1.rightComponent(),
				a2.from(), a2.to(), a2.leftComponent(), a2.rightComponent());
		
		double minX = Double.MAX_VALUE, maxX = 0, minY = Double.MAX_VALUE, maxY = 0;
		
		for(Point p : points){
			minX = (p.x() < minX) ? p.x() : minX;
			minY = (p.y() < minY) ? p.y() : minY;
			maxX = (p.x() > maxX) ? p.x() : maxX;
			maxY = (p.y() > maxY) ? p.y() : maxY;
		}
		
		double width = maxX - minX;
		double height = maxY - minY;
		
		return new Rectangle(new Point(minX + width/2, minY + height/2), width, height);
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension((int) m_frame.width(), (int) m_frame.height());
	}

	@Override
	public void onFlameBuilderChange(ObservableFlameBuilder b) {
		repaint();
	}

	@Override
	public void onRectangleChange(ObservableRectangle rect) {
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent evt) {
		double minArea = Double.MAX_VALUE;
		int index = -1;
		Point p = new Point(evt.getX(), evt.getY());

		for(int i = 0 ; i < m_boundingBoxes.size() ; i++){
			Rectangle rect = m_boundingBoxes.get(i);
			
			if(rect.contains(p)){
				double area = rect.area();
				if( area < minArea){
					minArea = rect.area();
					index = i;
				}
			}
		}
		
		if(index != -1)
			notifyTransformationSelected(index);
	}
	
	public void addListener(Listener l){
		m_listeners.add(l);
	}
	
	public void removeListener(Listener l){
		m_listeners.remove(l);
	}
	
	private void notifyTransformationSelected(int id){
		for(Listener l : m_listeners){
			l.onTransformationSelected(id);
		}
	}
	
	public interface Listener {
		public void onTransformationSelected(int transfoId);
	}
}