package ch.epfl.flamemaker.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;

public class AffineTransformationsComponent extends JComponent {
	
	private Flame.Builder m_builder;
	
	private Rectangle m_frame;

	private int m_highlightedTransformationIndex = -1;
		
	public AffineTransformationsComponent(Flame.Builder builder, Rectangle frame) {
		m_builder = builder;
		m_frame = frame;
	}
	
	public void highlightedTransformationIndex(int index) {
		if(m_highlightedTransformationIndex != -1 && index != m_highlightedTransformationIndex) {
			m_highlightedTransformationIndex = index;
			repaint();
		}
	}
	
	public int highlightedTransformationIndex() {
		return m_highlightedTransformationIndex;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		/* Works with or without, dafuq?
		double ratio = getWidth()/getHeight();
		if(ratio > 0) {
			m_frame = m_frame.expandToAspectRatio(ratio);
		} */
		Graphics2D g0 = ((Graphics2D) g);
		g0.setColor(new Color(9, 9, 9, 10));
		
		double yunity = getHeight()/10;
		for(double y = 0; y < getHeight(); y+= yunity) {
			g0.draw(new Line2D.Double(0, y, getWidth(), y));
		}
		
		double xunity = getWidth()/10;
		for(double x = 0; x < getWidth(); x += xunity) {
			g0.draw(new Line2D.Double(x, 0, x, getHeight()));
		}
		
		g0.setColor(Color.white);
		double mainLineX = getWidth()/2;
		g0.draw(new Line2D.Double(mainLineX, 0, mainLineX, getHeight()));
		g0.draw(new Line2D.Double(mainLineX+1, 0, mainLineX+1, getHeight()));
		
		double mainLineY = getHeight()/2;
		g0.draw(new Line2D.Double(0, mainLineY, getWidth(), mainLineY));
		g0.draw(new Line2D.Double(0, mainLineY+1, getWidth(), mainLineY+1));
		
		g0.setColor(Color.black);
		
		Point from = new Point(1, 1), to = new Point(3,3);

		Transformation t = AffineTransformation.newTranslation(getWidth()/2, getHeight()/2)
				.composeWith(AffineTransformation.newScaling(xunity, yunity))
				.composeWith(AffineTransformation.newRotation(-Math.PI/2));
		
		Point from2 = t.transformPoint(from);
		Point to2 = t.transformPoint(to);
		g0.draw(new Line2D.Double(from2.x(), from2.y(), to2.x(), to2.y()));
		// Add transformations
//		AffineTransformation transfo;
//		for(int i = 0; i < m_builder.transformationsCount(); i++) {
//			transfo = m_builder.affineTransformation(i);
//			Point r = transfo.transformPoint(new Point(1, 1));
//			g0.draw(new Line2D.Double(xunity, yunity, xunity*r.x(), yunity*r.y()));
//			break;
		//}
		
	}
	
	@Override
	public Dimension getPreferredSize(){
		
//		return new Dimension((int) m_frame.width(), (int) m_frame.height());
		return new Dimension(50, 50);
	}
}




