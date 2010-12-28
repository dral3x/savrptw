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
	
	double initial_temperature;
	int customers;
	
	public VRPTWSolverThread(int id, VRPTWProblem problem, VRPTWSolution solution, LinkedList<VRPTWSolution> solutions, CyclicBarrier start, CyclicBarrier done) {
		_id = id;
		_old_solution = solution;
		_best_local_solution = solution;
		_problem = problem;
		_solutions = solutions;
		
		_start_barrier = start;
		_done_barrier = done;
		
		initial_temperature = VRPTWParameters.gamma * _old_solution.cost();
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
		
		double temperature = initial_temperature;
		
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
				_best_local_solution = annealing_step(_best_local_solution, temperature);

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
			
			temperature *= VRPTWParameters.beta;
		}
		
		if (debug) System.out.println("SolverThread "+_id+" termina");
	}
	
	
	protected static VRPTWSolution annealing_step(VRPTWSolution start_solution, double temperature) {
		VRPTWSolution newSolution = start_solution.clone();

		// sceglo la route da allungare
		int indexOfFirstRoute = new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue();
		VRPTWRoute r1 = newSolution.routes.get(indexOfFirstRoute);
		//System.out.println("scelto route: "+r1);
		
		// prendo il cliente più vicino alla route selezionata
		LinkedList<VRPTWCustomer> externalCustomers = new LinkedList<VRPTWCustomer>();
		for (VRPTWRoute r : newSolution.routes) {
			if (r != r1) {
				for (VRPTWCustomer c : r.customers) {
					externalCustomers.add(c);
				}
			}
		}
		Collections.sort(externalCustomers, new VRPTWCustomerNearestToRouteComparator(r1));
		VRPTWCustomer nearestCustomer = externalCustomers.remove();;
		//System.out.println("il customer più vicino è "+nearestCustomer);
		
		// lo rimuovo dalla sua strada originale
		for (VRPTWRoute r : newSolution.routes) {
			if (r.serve(nearestCustomer)) {
				r.removeCustomer(nearestCustomer);
				if (r.travelDistance() < 0.0001) { // se era un cliente solitario, rimuovo la ruote vuota
					newSolution.removeRoute(r);
				}
				break;
			}
		}
		
		// sposto il cliente vicino sulla strada scelta
		boolean insered = r1.addCustomerIfPossible(nearestCustomer);
		//System.out.println("E' stato inserito? "+insered);
		
		// se non viene inserito nella nuova strada, lo metto in una strada vergine
		if (!insered) {
			VRPTWRoute r = new VRPTWRoute(r1._warehouse, r1._initial_capacity);
			r.addCustomer(nearestCustomer);
			newSolution.addRoute(r);
		}
	
		// controllo se ho migliorato o meno		
		double cost_new = newSolution.cost();
		double cost_old = start_solution.cost();
		if (cost_new > cost_old) {
			System.out.print("thread: soluzione peggiore di quella di partenza: costo " + newSolution.cost() + " con " + newSolution.routes.size() + " mezzi");
			if (Math.random() < 0.5*(temperature/(temperature + VRPTWParameters.delta))) {
				System.out.println(" accettata comunque");
			} else {
				// rifiuto la nuova soluzione, torno alla vecchia
				newSolution = start_solution;
				System.out.println(" rifiutata");
			}
		} else {
			System.out.println("thread: soluzione migliore di quella di partenza: costo " + newSolution.cost() + " con " + newSolution.routes.size() + " mezzi");
		}

		
		return newSolution;		
	}
	
	protected static VRPTWSolution annealing_step_old(VRPTWSolution start_solution, double temperature) {

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
		VRPTWCustomer c1 = r1.customers.get(indexOfBestClient);
		r1.removeCustomer(c1);
		if (r1.travelDistance() < 0.0001) {
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
			
			r2 = new VRPTWRoute(r2._warehouse, r2._initial_capacity);
			r2.addCustomer(c1);
			newSolution.addRoute(r2);
		}
		
		// controllo se ho migliorato o meno		
		double cost_new = newSolution.cost();
		double cost_old = start_solution.cost();
		if (cost_old < cost_new && (Math.random()*(temperature/(temperature + VRPTWParameters.delta)) < 0.5)) {
			// rifiuto la nuova soluzione piÔøΩ cara della vecchia
			newSolution = start_solution;
		}
		
		return newSolution;
	}
}
