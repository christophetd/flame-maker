package ch.epfl.flamemaker.geometry2d;

public class AffineTransformation implements Transformation {
	
	final static public AffineTransformation IDENTITY = new AffineTransformation(1, 0, 0,
																				 0, 1, 0);
	private double m_a, m_b, m_c, m_d, m_e, m_f;
	
	public AffineTransformation(double a, double b, double c, double d, double e, double f) {
		m_a = a;
		m_b = b;
		m_c = c;
		m_d = d;
		m_e = e;
		m_f = f;
	}
	
	public Point transformPoint(Point p) {
		return new Point(m_a * p.x() + m_b * p.y() + m_c, 
						 m_d * p.x() + m_e * p.y() + m_f);
	}
	
	public AffineTransformation composeWith(AffineTransformation other) {
		return new AffineTransformation(m_a * other.m_a + m_b * other.m_d, 
										m_a * other.m_b + m_b * other.m_e, 
										m_a * other.m_c + m_b * other.m_f + m_c, 
										m_d * other.m_a + m_e * other.m_d, 
										m_d * other.m_b + m_e * other.m_e, 
										m_d * other.m_c + m_e * other.m_f + m_f);
	}
	
	public double translationX() {
		return m_c;
	}
	
	public double translationY() {
		return m_f;
	}
	
	public static AffineTransformation newTranslation(double dx, double dy) {
		return new AffineTransformation(1, 0, dx, 
										0, 1, dy);
	}
	
	public static AffineTransformation newRotation(double angle) {
		return new AffineTransformation(Math.cos(angle), -Math.sin(angle), 0, 
										Math.sin(angle),  Math.cos(angle), 0);
	}
	
	public static AffineTransformation newScaling(double sx, double sy) {
		return new AffineTransformation(sx, 0, 0,
										0, sy, 0);
	}
	
	public static AffineTransformation newShearX(double sx) {
		return new AffineTransformation(1, sx, 0, 
										0, 1, 0);
	}
	
	public static AffineTransformation newShearY(double sy) {
		return new AffineTransformation(1, 0, 0, 
										sy, 1, 0);
	}
}
