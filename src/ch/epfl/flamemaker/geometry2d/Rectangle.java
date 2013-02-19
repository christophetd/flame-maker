package ch.epfl.flamemaker.geometry2d;

public class Rectangle {
	private Point m_center;
	private double m_width;
	private double m_height;
	
	public Rectangle(Point center, double width, double height) {
		if(width < 0 || height < 0) {
			throw new IllegalArgumentException("Width and height must be greater than zero");
		}
		
		m_center = center;
		m_width  = width;
		m_height = height;
	}
	
	public double left() {
		return m_center.x() - (m_width / 2.0);
	}
	
	public double right() {
		return m_center.x() + (m_width / 2.0);
	}
	
	public double bottom() {
		return m_center.y() - (m_height / 2.0);
	}
	
	public double top() {
		return m_center.y() + (m_height / 2.0);
	}
	
	public double width() {
		return m_width;
	}
	
	public double height() {
		return m_height;
	}

	public Point center() {
		return m_center;
	}
	
	public boolean contains(Point p) {
		return (p.x() >= this.left()   && p.x() < this.right())
			&& (p.y() >= this.bottom() && p.y() < this.top());
	}
	
	public double aspectRatio() {
		return (m_width / m_height);
	}
	
	public Rectangle expandToAspectRatio(double aspectRatio) {
		if(aspectRatio <= 0) {
			throw new IllegalArgumentException("Aspect ratio must be greater than zero");
		}
		
		/*
		 Le nouveau rectangle doit contenir entièrement l'ancien : on prend donc le maximum entre
		 la largeur / hauteur calculée selon le ratio donné et la largeur / hauteur actuelle, 
		 pour s'assurer de cela
		*/
		double newWidth  = Math.max(m_width, m_height * aspectRatio);
		double newHeight = Math.max(m_height, m_width / aspectRatio);
		
		// Test unitaire : newWidth / newHeight == aspectRatio
		
		return new Rectangle(m_center, newWidth, newHeight);
	}
	
	public String toString() {
		String output = "(";
		output += m_center.toString();
		output += ", " + m_width + ", " + m_height+")";
		
		return output;
	}
}
