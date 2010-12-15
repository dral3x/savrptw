package ia.vrptw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class VRPTWSolverThread implements Runnable {
	
	boolean debug = false;
	
	int _id;
	boolean go;
	VRPTWSolution _best_local_solution;
	VRPTWSolution _coworker_solution;
	VRPTWSolution _old_solution;
	VRPTWProblem _problem;
	
	LinkedList<VRPTWSolution> _solutions;
	CyclicBarrier _start_barrier;
	CyclicBarrier _done_barrier;
	
	VRPTWSolverThread _coworker;
	
	double temperature;
	int customers;
	
	public VRPTWSolverThread(int id, VRPTWProblem problem, VRPTWSolution solution, LinkedList<VRPTWSolution> solutions, CyclicBarrier start, CyclicBarrier done) {
		_id = id;
		_old_solution = solution;
		_best_local_solution = solution;
		_problem = problem;
		_solutions = solutions;
		
		_start_barrier = start;
		_done_barrier = done;
		
		temperature = VRPTWParameters.gamma * _old_solution.cost();
		customers = problem.getNumberOfCustomers();
		
		_coworker_solution = null;
		go = true;
	}
	
	public void activateDebugMode() {
		debug = true;
	}
	
	public void setCoWorker(VRPTWSolverThread socio) {
		_coworker = socio;
	}
	
	public void setCoWorkerSolution(VRPTWSolution another_solution) {
		if (another_solution == null)
			throw new IllegalArgumentException("ricevo una soluzione null dal collega!");
		_coworker_solution = another_solution;
	}
	
//	public VRPTWSolution getSolution() {
//		return _best_local_solution;
//	}
	
	public void stop() {
		go = false;
	}
	
	public int getID() {
		return _id;
	}

	@Override
	public void run() {
		
		if (debug) System.out.println("SolverThread "+_id+" avviato!");
		
		// attende partenza 
		try {
			_start_barrier.await();
		} catch (BrokenBarrierException e) {
			go = false;
		} catch (InterruptedException e) {
			go = false;
		}
		
		while (go) {
			
			// faccio n iterazioni, confronto la mia soluzione con quella del collega e ciclo
			// avanti così fino a n^2 spostamenti, dopo i quali consegno la soluzione al "capo"
			for (int iteration=0; iteration < customers*customers; iteration++ ) {
				//if (debug) System.out.println("thread-"+_id+" iterazione "+iteration);
				_best_local_solution = annealing_step(_best_local_solution);

				// ogni n iterazioni (n = numero di clienti) coopero col "vicino"
				if (_coworker != null && iteration % customers == customers-1) { // co-operate
					if (debug) System.out.println("thread-"+_id+" consegno al collega "+_coworker.getID()+" la mia soluzione");
					synchronized(_coworker) {
						_coworker.setCoWorkerSolution(_best_local_solution);
						_coworker.notify();
					}

					synchronized(this) {
						while (_coworker_solution == null) {
							try {
								if (debug) System.out.println("thread-"+_id+" attendo il collega "+_coworker.getID());
								this.wait();
							} catch (InterruptedException e) {
								go = false;
								break;
							}
						}
					}


					// prendo la solutione del vicino se è migliore della  mia
					if (debug) System.out.println("thread-"+_id+" controllo solutione arrivata dal collega "+_coworker.getID());
					if (_coworker_solution.cost() < _best_local_solution.cost()) {
						_best_local_solution = _coworker_solution;
					}
					_coworker_solution = null;
				}
			}
			
			// consegna la soluzione trovata
			if (debug) System.out.println("thread-"+_id+" consegna soluzione best_local");
			synchronized(_solutions) {
				_solutions.add(_best_local_solution);
				//_best_local_solution.show();
			}
		
			
			try {
				// attendo che tutti i thread consegnino la loro soluzione
				_done_barrier.await();
				
				// attendo una notify dal solver per riprendere il lavoro
				if (debug) System.out.println("thread-"+_id+" in attesa di iniziare di nuovo");
				_start_barrier.await();
			} catch (BrokenBarrierException e) {
				go = false;
			} catch (InterruptedException e) {
				go = false;
			}
			
		}
		
		if (debug) System.out.println("SolverThread "+_id+" termina");
	}
	
	private VRPTWSolution annealing_step(VRPTWSolution start_solution) {

//		try {
//			Thread.sleep(Math.round(Math.random()*10));
//		} catch (InterruptedException e) { }

		
		VRPTWSolution newSolution = start_solution.clone();
		
		// On every step a neighbor solution is determined by either mov-
		// ing the best customer from one route to the best place (in
		// terms of the solution cost) of another route (perhaps empty) or
		// by selecting the best customer and moving it to the best place
		// within its route. All the routes mentioned above are
		// chosen randomly.
		// The neighbor solutions of lower costs obtained
		// in this way are always accepted. The solutions of higher costs
		// are accepted with the probability
		// T_i / (T_i + delta)
		
		// sceglo il cliente da spostare
		VRPTWRoute r1 = newSolution.routes.get(new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue());
		int indexOfBestClient = new Long(Math.round(Math.random()*(r1.customers.size()-1))).intValue();
		VRPTWCustomer c1 = r1.customers.remove(indexOfBestClient);
		if (r1.customers.size() == 0) {
			newSolution.removeRoute(r1);
		}

		// scelto la destinazione
		VRPTWRoute r2 = null;
		int indexOfR2 = -1;

		// sposto il miglior cliente dalla prima alla seconda strada
		boolean insered = false;
		int attempt = 0;
		do {
			indexOfR2 = new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue();
			r2 = newSolution.routes.get(indexOfR2);
			insered = r2.addCustomerIfPossible(c1);
			attempt++;
		} while (!insered && attempt<VRPTWParameters.insertion_attempt);
		if (!insered) {
			r2 = new VRPTWRoute(_problem.getWarehouse(), _problem.getVehicleCapacity());
			r2.addCustomer(c1);
			newSolution.addRoute(r2);
		}
		
		// controllo se ho migliorato o meno		
		double cost_new = newSolution.cost();
		double cost_old = start_solution.cost();
		if (cost_old < cost_new && (Math.random()*(cost_new/(cost_new + VRPTWParameters.delta)) >= 0.5)) {
			// rifiuto la nuova soluzione più cara della vecchia
			newSolution = start_solution;
		}
		
		return newSolution;
	}
}
