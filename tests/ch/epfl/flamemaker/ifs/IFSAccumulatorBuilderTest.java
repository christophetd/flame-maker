package ch.epfl.flamemaker.ifs;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class IFSAccumulatorBuilderTest {
	@Test
	public void testBuild() {
		IFSAccumulatorBuilder builder = new IFSAccumulatorBuilder(new Rectangle(new Point(63, 62), 100, 120), 20, 10);
		
		builder.hit(new Point(24,12));
		
		IFSAccumulator accu = builder.build();
		
		assertTrue(accu.isHit(2, 0));
		assertFalse(accu.isHit(1, 1));
		
	}
}
