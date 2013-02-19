package ch.epfl.flamemaker.geometry2d;

public class Point {
	
	public static final Point ORIGIN = new Point(0,0);
	
	private double m_x, m_y;
	
	public Point(double x, double y){
		m_x = x;
		m_y = y;
	}
	
	public double x(){
		return m_x;
	}
	
	public double y(){
		return m_y;
	}
	
	public double r(){
		return Math.sqrt(m_x*m_x + m_y*m_y);
	}
	
	public double theta(){
		return Math.atan2(m_y, m_x);
	}
	
	public String toString(){
		return "("+m_x+","+m_y+")";
	}
}
