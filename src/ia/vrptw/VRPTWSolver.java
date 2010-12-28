package ia.vrptw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWSolver {

	/*
	 * Main per avviare il solver sul problema specificato.
	 * Questa classe e' sostanzialmente P0 nel paper.
	 */
	public static void main(String[] args) throws InterruptedException {
		VRPTWProblem problem = new VRPTWProblem("RC101", 50, 200);
		//problem.show();
		VRPTWSolver solver = new VRPTWSolver(1); // processori
		solver.activateDebugMode();
		System.out.println("* inizio ottimizzazione *");
		VRPTWSolution solution = solver.resolve(problem);
		System.out.println("* ottimizzazione terminata *");
		solution.show();
	}

	private VRPTWSolverThread[] threads;
	private int _processors;
	private boolean debug = false;

	public VRPTWSolver(int processors) {
		if (processors > 1 && processors % 2 == 1)
			throw new IllegalArgumentException("numero di thread non pari");
		
		_processors = processors;
		threads = new VRPTWSolverThread[_processors];
	}
	
	public void activateDebugMode() {
		debug = true;
	}

	public VRPTWSolution resolve(VRPTWProblem problem) throws InterruptedException {
		// scelgo una soluzione da cui partire (a caso?)
		VRPTWSolution finalSolution = generateFirstSolution(problem);
		System.out.println("Soluzione di partenza: costo " + finalSolution.cost() + " con " + finalSolution.routes.size() + " mezzi");
		//finalSolution.show();
		
		LinkedList<VRPTWSolution> solutions = new LinkedList<VRPTWSolution>();
		
		// istanzio i thread paralleli
		threads = new VRPTWSolverThread[_processors];
		CyclicBarrier _start_barrier = new CyclicBarrier(_processors+1);
		CyclicBarrier _done_barrier = new CyclicBarrier(_processors+1);
		//CyclicBarrier _cooperate_barrier = new CyclicBarrier(_processors);
	     
		for (int i=0; i<_processors; i++) {
			threads[i] = new VRPTWSolverThread(i, problem, finalSolution, solutions, _start_barrier, _done_barrier);
			//if (debug)
			//	threads[i].activateDebugMode();
			//if (i>0)
			//	threads[i].setCoWorker(threads[i-1]);
		}
		//threads[0].setCoWorker(threads[_processors-1]);
		
		// faccio partire i thread paralleli dalla soluzione generata
		for (int i=0; i<_processors; i++) {
			new Thread(threads[i]).start();
		}
		
		int equilibrium = 0;
		while (equilibrium < VRPTWParameters.tau) {
			//if (debug)
			//	System.out.println("Solver: giro "+equilibrium);
			
			try {
				_start_barrier.await();
				_start_barrier.reset();
				
				//if (debug) System.out.println("Solver: attendo che tutti i risolutori abbiano consegnato qualcosa");
				_done_barrier.await();
				_done_barrier.reset();
			} catch (InterruptedException e) {
				break;
			} catch (BrokenBarrierException e) {
				break;
			}

			if (solutions.size() != _processors) { 
				System.err.println("QUALCUNO HA MANCATO LA CONSEGNA!");
				System.exit(1);
			}
			
			// scelgo la soluzione migliore tra quelle trovate finora
			//if (debug) System.out.println("Solver: scelgo la soluzione migliore tra quelle consegnate");
			VRPTWSolution bestSolution = solutions.remove();
			while (solutions.size() > 0) {
				VRPTWSolution s = solutions.remove();
				if (s.cost()<bestSolution.cost()) {
					bestSolution = s;
				}
			}
			
			// controllo se  migliore di quella che avevo prima
			if (bestSolution.cost() < finalSolution.cost()) {
				finalSolution = bestSolution;
				equilibrium = 0;
				System.out.println("Trovata soluzione migliore ... costo " + finalSolution.cost() + " con " + finalSolution.routes.size() + " mezzi");
			} else {
				equilibrium ++;
			}
			
		}

		// fermo i thread se non ho fatto progressi
		for (int i=0; i<_processors; i++) {
			threads[i].stop();
		}
		try {
			_start_barrier.await();
		} catch (BrokenBarrierException e) { }
		
		// ritorno la meglio soluzione trovata finora
		return finalSolution;
	}
	
	private VRPTWSolution generateFirstSolution(VRPTWProblem problem)  {
		// TODO da implementare seriamente
		
		VRPTWSolution solution = new VRPTWSolution(problem);
		
		int vehicle = 0;
		VRPTWCustomer warehouse = null;
		
		LinkedList<VRPTWCustomer> customerToServe = new LinkedList<VRPTWCustomer>();
		for (VRPTWCustomer c : problem.customers) {
			if (c.isWarehouse())
				warehouse = c;
			else
				customerToServe.add(c);
		}
		Collections.sort(customerToServe, new VRPTWCustomerEndTimeWindowComparator());
				
		// finch ci sono ancora clienti, bisogna servirli
		VRPTWRoute route = new VRPTWRoute(warehouse, problem.getVehicleCapacity());
		while (!customerToServe.isEmpty()) {
			VRPTWCustomer customer = customerToServe.remove();
			boolean capacity_test = route.getRemainCapacity()-customer._demand > 0; // ho ancora spazio nel camion per quello che il cliente di turno vuole
			boolean timewindow_test = route.travelDistance() < customer._due_date; // ce la faccio a portarglielo dentro alla sua deadline
			if (!timewindow_test || !capacity_test) {
				solution.addRoute(route);
				route = new VRPTWRoute(warehouse, problem.getVehicleCapacity());
			}
			route.addCustomer(customer);
		}
		if (route.size()>0) {
			solution.addRoute(route);
		}
		
		return solution;
	}

}
