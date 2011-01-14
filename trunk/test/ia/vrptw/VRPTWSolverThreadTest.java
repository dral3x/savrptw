package ia.vrptw;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VRPTWSolverThreadTest {

	VRPTWSolverThread cut;
	VRPTWProblem problem;
	
	@Before
	public void setUp() throws Exception {
		problem = createMock(VRPTWProblem.class);
		expect(problem.getInstanceName()).andReturn("IstanzaTest").anyTimes();
	}

	@After
	public void tearDown() throws Exception {
		problem = null;
	}

	@Test
	public void testAnnealing_step() {
		
		VRPTWCustomer w = new VRPTWCustomer(1, 40, 50, 0, 0, 200, 0);
		expect(problem.getWarehouse()).andReturn(w).anyTimes();
		
		VRPTWCustomer c1 = new VRPTWCustomer(2, 40, 60, 40, 10, 20, 10);
		VRPTWCustomer c2 = new VRPTWCustomer(3, 40, 70, 40, 20, 40, 10);
		VRPTWCustomer c3 = new VRPTWCustomer(4, 50, 70, 40, 40, 60, 10);
		
		// before
		VRPTWRoute r1 = new VRPTWRoute(w, 100);
		r1.addCustomer(c1);
		r1.addCustomer(c3);
		VRPTWRoute r2 = new VRPTWRoute(w, 100);
		r2.addCustomer(c2);
		VRPTWSolution before = new VRPTWSolution(problem);
		before.addRoute(r1);
		before.addRoute(r2);
		before.show();
		
		// expected
		VRPTWRoute r3 = new VRPTWRoute(w, 100);
		r3.addCustomer(c1);
		r3.addCustomer(c2);
		r3.addCustomer(c3);
		
		VRPTWSolution expected = new VRPTWSolution(problem);
		expected.addRoute(r3);
		//before.show();
		
		replay(problem);
		
		VRPTWSolution after = VRPTWSolverThread.annealing_step(before);
		after.show();
		
		assertEquals("soluzione non corretta", expected, after);
		
	}

}
