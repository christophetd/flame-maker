package ch.epfl.flamemaker.geometry2d;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AffineTransformationTest {
	private Point p = new Point(1, 1);
	private Point p2 = new Point(2, 1);
	private static double DELTA = 0.000000001;
	

	@Test
	public void testTranslation() {
		AffineTransformation translation = AffineTransformation.newTranslation(2, 2);
		Point result = translation.transformPoint(p);
		assertEquals(result.x(), 3, DELTA);
		assertEquals(result.y(), 3, DELTA);
	}
	
	@Test
	public void testRotation() {
		AffineTransformation rotation = AffineTransformation.newRotation(Math.PI);
		Point r = rotation.transformPoint(p);
		assertEquals(r.x(), -1, DELTA);
		assertEquals(r.y(), -1, DELTA);
		
		rotation = AffineTransformation.newRotation(Math.PI * 2);
		Point r2 = rotation.transformPoint(p2);
		assertEquals(r2.x(), p2.x(), DELTA);
		assertEquals(r2.y(), p2.y(), DELTA);
	}
	
	@Test
	public void testScaling() {
		AffineTransformation scaling = AffineTransformation.newScaling(3, 2);
		Point r = scaling.transformPoint(p2);
		
		assertEquals(r.x(), p2.x()*3, DELTA);
		assertEquals(r.y(), p2.y()*2, DELTA);
		
	}
	
	@Test
	public void testShearX() {
		AffineTransformation shearX = AffineTransformation.newShearX(3);
		Point r = shearX.transformPoint(p);
		
		assertEquals(r.x(), p.x()+3, DELTA);
		assertEquals(r.y(), p.y(), DELTA);
	}
	
	@Test
	public void testShearY() {
		AffineTransformation shearY = AffineTransformation.newShearY(-4);
		Point r = shearY.transformPoint(p);
		
		assertEquals(r.x(), p.x(), DELTA);
		assertEquals(r.y(), p.y()-4, DELTA);
	}
	
	@Test
	public void testComposition() {
		AffineTransformation shearX = AffineTransformation.newShearX(2);
		AffineTransformation shearY = AffineTransformation.newShearY(-1);
		AffineTransformation translation = shearX.composeWith(shearY);
		
		Point r = translation.transformPoint(p);
		
		assertEquals(1, r.x(), DELTA);
		assertEquals(0, r.y(), DELTA);
		
		Point r2 = translation.transformPoint(p2);
		
		assertEquals(0, r2.x(), DELTA);
		assertEquals(-1, r2.y(), DELTA);			
	}
	
	
	

}
