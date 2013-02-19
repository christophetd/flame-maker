package ch.epfl.flamemaker.ifs;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class IFSAccumulatorBuilderTest {
	@Test
	public void testBuild() {
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(new Rectangle(new Point(5, 4), 6, 6), 3, 3);
		
		builder.hit(new Point(3,2));
		
		IFSAccumulator accu = builder.build();
		
		assertTrue(accu.isHit(0, 0));
		assertFalse(accu.isHit(1, 1));
		
	}
}
