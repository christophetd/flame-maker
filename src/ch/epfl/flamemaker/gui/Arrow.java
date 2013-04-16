package ch.epfl.flamemaker.gui;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

public class Arrow {
	private Point m_from = new Point(-1, 0);
	private Point m_to = new Point(1, 0);
	private Point m_leftComponent;
	private Point m_rightComponent;
	public Arrow(Point from, Point to) {
		m_from = from;
		m_to = to;
		buildComponents();
	}
	
	private void buildComponents() {
		m_leftComponent = new Point(1, 1);
		m_rightComponent = new Point(-1, -1);
	}
	
	public void draw(Graphics2D g, Transformation mapper) {
		buildComponents();
		m_from = mapper.transformPoint(m_from);
		m_to = mapper.transformPoint(m_to);
		m_leftComponent = mapper.transformPoint(m_leftComponent);
		m_rightComponent = mapper.transformPoint(m_rightComponent);
		
		g.draw(new Line2D.Double(m_from.x(), m_from.y(), m_to.x(), m_to.y()));
		g.draw(new Line2D.Double(m_to.x(), m_to.y(), m_rightComponent.x(), m_rightComponent.y()));
		g.draw(new Line2D.Double(m_to.x(), m_to.y(), m_leftComponent.x(), m_leftComponent.y()));
	}
	
	public Arrow applyTransformation(Transformation transfo) {
		return new Arrow(transfo.transformPoint(m_from),transfo.transformPoint(m_to));
	}
}