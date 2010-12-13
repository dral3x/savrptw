package ia.vrptw;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VRPTWUtilsTest {

	VRPTWUtils cut;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDistance1() {
		double result = cut.distance(0, 0, 1, 1);
		double expected = Math.sqrt(2);
		
		assertEquals("errato calcolo distanza tra 0,0 e 1,1", expected, result, 0.0001);
	}

	@Test
	public void testDistance2() {
		double result = cut.distance(1, 1, 1, 1);
		double expected = 0;
		
		assertEquals("errato calcolo distanza tra 1,1 e 1,1", expected, result, 0.0001);
	}
	
	@Test
	public void testDistance3() {
		double result = cut.distance(10, 0, 10, 1);
		double expected = 1.0;
		
		assertEquals("errato calcolo distanza tra 10,0 e 10,1", expected, result, 0.0001);
	}
	
	@Test
	public void testDistance4() {
		double result = cut.distance(0, 10, 1, 10);
		double expected = 1.0;
		
		assertEquals("errato calcolo distanza tra 0,10 e 1,10", expected, result, 0.0001);
	}
}
