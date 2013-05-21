/**
 * @author Hadrien Milano <Sciper : 224340>
 * @author Christophe Tafani-Dereeper <Sciper : 223529>
 */

package ch.epfl.flamemaker.gui;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

public class Arrow {
	private double H = 0.0707;
	private Point m_from;
	private Point m_to;
	private Point m_leftComponent;
	private Point m_rightComponent;

	public Arrow(Point from, Point to) {
		m_from = from;
		m_to = to;

		buildComponents();
	}

	private void buildComponents() {
		double dx = m_to.x() - m_from.x();
		double dy = m_to.y() - m_from.y();

		double l = Math.sqrt(dx * dx + dy * dy);
		double alpha = Math.atan2(dx, dy);

		double beta = alpha - Math.PI / 4;

		double x0 = l * H * Math.sin(beta);
		double y0 = l * H * Math.cos(beta);

		double x1 = l * H * (Math.sin(beta + Math.PI / 2));
		double y1 = l * H * (Math.cos(beta + Math.PI / 2));

		m_leftComponent = new Point(m_to.x() - x0, m_to.y() - y0);
		m_rightComponent = new Point(m_to.x() - x1, m_to.y() - y1);
	}

	public void draw(Graphics2D g) {

		g.draw(new Line2D.Double(m_from.x(), m_from.y(), m_to.x(), m_to.y()));
		g.draw(new Line2D.Double(m_to.x(), m_to.y(), m_rightComponent.x(),
				m_rightComponent.y()));
		g.draw(new Line2D.Double(m_to.x(), m_to.y(), m_leftComponent.x(),
				m_leftComponent.y()));
	}

	public Arrow applyTransformation(Transformation transfo) {
		m_from = transfo.transformPoint(m_from);
		m_to = transfo.transformPoint(m_to);
		m_leftComponent = transfo.transformPoint(m_leftComponent);
		m_rightComponent = transfo.transformPoint(m_rightComponent);

		return this;
	}

	public Point from() {
		return new Point(m_from.x(), m_from.y());
	}

	public Point to() {
		return new Point(m_to.x(), m_to.y());
	}

	public Point leftComponent() {
		return new Point(m_leftComponent.x(), m_leftComponent.y());
	}

	public Point rightComponent() {
		return new Point(m_rightComponent.x(), m_rightComponent.y());
	}
}