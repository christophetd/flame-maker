package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Test;


public class PointTest {
	private static double DELTA = 0.000000001;
	
	@Test
	public void testPoint() {
		new Point(1.5, 2.0);
	}
	
	@Test
	public void testX() {
		Point p = new Point(1.337, 2);
		assertEquals(p.x(), 1.337, DELTA);
	}
	
	@Test
	public void testY() {
		Point p = new Point(1.337, 2);
		assertEquals(p.y(), 2, DELTA);
	}
	
	@Test
	public void testR() {
		Point p = new Point(2, 1);
		assertEquals(p.r(), 2.2360679775, DELTA);
	}
	
	@Test
	public void testTheta() {
		Point p = new Point(-2, -2);
		assertEquals(p.theta(), -2.356194490, DELTA);
	}
}
