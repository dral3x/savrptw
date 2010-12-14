package ia.vrptw;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VRPTWRouteTest {

	VRPTWRoute cut;
	
	@Before
	public void setUp() throws Exception {
		VRPTWCustomer warehouse = new VRPTWCustomer(1, 0.0, 0.0, 100, 0, 1000, 0);
		cut = new VRPTWRoute(warehouse, 100);
	}

	@After
	public void tearDown() throws Exception {
		cut = null;
	}

	@Test
	public void testTravelDistance1() {
		cut.addCustomer(new VRPTWCustomer(1, 0.0, 10.0, 100, 0, 1000, 0));

		assertEquals("errore nel calcolo della distanza", 10, cut.travelDistance(), 0.0001);
	}

	@Test
	public void testTravelDistance2() {
		cut.addCustomer(new VRPTWCustomer(1, 0.0, 10.0, 100, 0, 1000, 0));
		cut.addCustomer(new VRPTWCustomer(1, 10.0, 10.0, 100, 0, 1000, 0));

		assertEquals("errore nel calcolo della distanza con 2 fermate", 20, cut.travelDistance(), 0.0001);
	}

	@Test
	public void testTravelDistance3() {
		cut.addCustomer(new VRPTWCustomer(1, 0.0, 10.0, 100, 0, 1000, 0));
		cut.addCustomer(new VRPTWCustomer(1, 10.0, 10.0, 100, 0, 1000, 0));
		cut.addCustomer(new VRPTWCustomer(1, 20.0, 20.0, 100, 0, 1000, 0));

		assertEquals("errore nel calcolo della distanza con 2 fermate", 20+10*Math.sqrt(2), cut.travelDistance(), 0.0001);
	}

}
