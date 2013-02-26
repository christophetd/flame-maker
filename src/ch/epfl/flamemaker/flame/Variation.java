package ch.epfl.flamemaker.flame;

import java.util.Arrays;
import java.util.List;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Transformation;

public abstract class Variation implements Transformation {
	private final int m_index;
	private final String m_name;
	
	public final static List<Variation> ALL_VARIATIONS =
			Arrays.asList(
					new Variation(0, "Linear") {
						public Point transformPoint(Point p) {
							return p;
						}
					}, 
					new Variation(1, "Sinusoidal") {
						public Point transformPoint(Point p) {
							return new Point(Math.sin(p.x()), Math.sin(p.y()));
						}
					}, 
					new Variation(2, "Spherical") {
						public Point transformPoint(Point p) {
							return new Point(p.x()/(p.r()*p.r()), 
											 p.y()/(p.r()*p.r()));
						}
					}, 
					new Variation(3, "Swirl") {
						public Point transformPoint(Point p) {
							return new Point(p.x()*Math.sin(p.r()*p.r()) - p.y()*Math.cos(p.r()*p.r()), 
											 p.x()*Math.cos(p.r()*p.r()) + p.y()*Math.sin(p.r()*p.r()));
						}
					}, 
					new Variation(4, "Horseshoe") {
						public Point transformPoint(Point p) {
							return new Point((p.x()-p.y())*(p.x()+p.y())/p.r(),
											 (2*p.x()*p.y())/p.r()); 
						}
					}, 
					new Variation(5, "Bubble") {
						public Point transformPoint(Point p) {
							return new Point(4*p.x()/(p.r()*p.r() + 4), 
											 4*p.y()/(p.r()*p.r() + 4));
						}
					}
			);
	public Variation(int index, String name) {
		m_index = index;
		m_name = name;
	}
	
	abstract public Point transformPoint(Point p);
	
	public String name() {
		return m_name;
	}
	
	public int index() {
		return m_index;
	}
}
