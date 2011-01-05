package ia.vrptw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;


public class VRPTWSolverThread implements Runnable {
	
	static boolean debug = false;
	
	int _id;
	boolean go;
	VRPTWSolution _best_local_solution;
	VRPTWSolution _coworker_solution;
	VRPTWSolution _old_solution;
	VRPTWProblem _problem;
	
	LinkedList<VRPTWSolution> _solutions;
	CyclicBarrier _start_barrier;
	CyclicBarrier _done_barrier;
	CyclicBarrier _cooperate;
	
	VRPTWSolverThread _coworker_prev, _coworker_next;
	
	double initial_temperature;
	int customers;
	
	public VRPTWSolverThread(int id, VRPTWProblem problem, VRPTWSolution solution, LinkedList<VRPTWSolution> solutions, CyclicBarrier start, CyclicBarrier done, CyclicBarrier cooperate) {
		_id = id;
		_old_solution = solution;
		_best_local_solution = solution;
		_problem = problem;
		_solutions = solutions;
		
		_start_barrier = start;
		_done_barrier = done;
		_cooperate = cooperate;
		
		initial_temperature = VRPTWParameters.gamma * _old_solution.cost();
		customers = problem.getNumberOfCustomers();
		
		_coworker_solution = null;
		go = true;
	}
	
	public void activateDebugMode() {
		debug = true;
	}
	
	public void setCoWorkerPrev(VRPTWSolverThread socio) {
		_coworker_prev = socio;
	}
	
	public void setCoWorkerNext(VRPTWSolverThread socio) {
		_coworker_next = socio;
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
			// avanti cos� fino a n^2 spostamenti, dopo i quali consegno la soluzione al "capo"
			for (int iteration=0; iteration < customers*customers; iteration++ ) {

				_best_local_solution = annealing_step(_best_local_solution, temperature);

				// ogni n iterazioni (n = numero di clienti) coopero col "vicino"
				if ( ( (_coworker_next != null) || (_coworker_prev != null) ) &&  iteration % customers == customers-1) {
					if (_id == 0) {
						// Attende gli altri thread per cooperare
						try {
							_cooperate.await();
							_cooperate.reset();
						} catch (BrokenBarrierException e) {
							go = false;
						} catch (InterruptedException e) {
							go = false;
						}
						
						// thread-0 invia il proprio risultato a thread-1
						if (debug) System.out.println("thread-"+_id+" consegno al collega "+_coworker_next.getID()+" la mia soluzione");
						synchronized(_coworker_next) {
							_coworker_next.setCoWorkerSolution(_best_local_solution);
							_coworker_next.notify();
						}
					} else {
						// Attende gli altri thread per cooperare
						try {
							_cooperate.await();
						} catch (BrokenBarrierException e) {
							go = false;
						} catch (InterruptedException e) {
							go = false;
						}
						
						// Attente la soluzione dal processore precedente
						synchronized(this) {
							while (_coworker_solution == null) {
								try {
									if (debug) System.out.println("thread-"+_id+" attendo il collega "+_coworker_prev.getID());
									this.wait();
								} catch (InterruptedException e) {
									go = false;
									break;
								}
							}
						}
						// Soluzione ricevuta
						// Prendo la solutione del vicino se e' migliore della mia
						if (debug) System.out.println("thread-"+_id+" controllo solutione arrivata dal collega "+_coworker_prev.getID());
						if (_coworker_solution.cost() < _best_local_solution.cost()) {
							_best_local_solution = _coworker_solution.clone();	//FIXME clone() non penso serva
						}
						// Inoltro la soluzione migliore al processore successivo
						if (_coworker_next != null) {
							if (debug) System.out.println("thread-"+_id+" consegno al collega "+_coworker_next.getID()+" la soluzione migliore");
							synchronized(_coworker_next) {
								_coworker_next.setCoWorkerSolution(_best_local_solution);
								_coworker_next.notify();
							}
						}
					}
					_coworker_solution = null;
				}
			}
			
			// consegna la soluzione trovata
			if (debug) System.out.println("thread-"+_id+" consegna soluzione best_local al thread supervisore");
			synchronized(_solutions) {
				_solutions.add(_best_local_solution);
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
	
		// prendo un cliente a caso da una rotta a caso
		int src_route_idx = new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue(); // FIXME: POSSONO essere la stessa rotta, non so se sia un bene o un male
		VRPTWRoute src_route = newSolution.routes.get(src_route_idx);
		int indexOfCustomer = new Long(Math.round(Math.random()*( src_route.customers.size()-3 ))).intValue() + 1;
		VRPTWCustomer customer = src_route.customers.get(indexOfCustomer);
		
		// lo rimuovo dalla sua strada originale
		src_route.removeCustomer(customer);
		if (src_route.travelDistance() < 0.0001) { // se era un cliente solitario, rimuovo la ruote vuota
			newSolution.removeRoute(src_route);
			if (debug) System.out.println("Rimozione di rotta");	
		}
		
		// scelgo la route da allungare
		int dest_route_idx = new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue(); // FIXME: POSSONO essere la stessa rotta, non so se sia un bene o un male
		VRPTWRoute dest_route = newSolution.routes.get(dest_route_idx);
		
		// Calcola potenziali inserimenti del cliente casuale nella rotta casuale
		LinkedList<VRPTWCandidateCustomerInsertion> candidate_insertions = dest_route.candidate_insertions(customer);
		Collections.sort(candidate_insertions);
		
		// Prova a fare gli inserimenti con incremento di costo minore
		ListIterator<VRPTWCandidateCustomerInsertion> itr = candidate_insertions.listIterator();
		VRPTWCandidateCustomerInsertion insertion = null;
		boolean inserted = false;
		
		while (itr.hasNext() && !inserted) {
			insertion = itr.next();
			inserted = dest_route.addCustomer(insertion.customer, insertion.prev_customer_idx, insertion.next_customer_idx);
		}
		
		if (!inserted)
			newSolution = start_solution;
		else {
			// controllo se ho migliorato o meno		
			double cost_new = newSolution.cost();
			double cost_old = start_solution.cost();
			if (cost_new > cost_old) {
				if (debug) System.out.print("thread: soluzione peggiore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
				if (Math.random() < (temperature/(temperature + VRPTWParameters.delta))) {
					if (debug) System.out.println(" accettata comunque (T=" + Math.round(temperature) + ")");
				} else {
					// rifiuto la nuova soluzione, torno alla vecchia
					newSolution = start_solution;
					if (debug) System.out.println(" rifiutata");
				}
			} else
				if (debug) System.out.println("thread: soluzione migliore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
		}
		return newSolution;		
	}

	// Originale
	protected static VRPTWSolution annealing_step_orig(VRPTWSolution start_solution, double temperature) {
		VRPTWSolution newSolution = start_solution.clone();

		// scelgo la route da allungare
		int indexOfFirstRoute = new Long(Math.round(Math.random()*(newSolution.routes.size()-1))).intValue();
		VRPTWRoute r1 = newSolution.routes.get(indexOfFirstRoute);
		
		// prendo il cliente piu' vicino alla route selezionata
		LinkedList<VRPTWCustomer> externalCustomers = new LinkedList<VRPTWCustomer>();
		for (VRPTWRoute r : newSolution.routes) {
			if (r != r1) {
				for (VRPTWCustomer c : r.customers)
					if (!c.isWarehouse())
						externalCustomers.add(c);
				
			}
		}
		Collections.sort(externalCustomers, new VRPTWCustomerNearestToRouteComparator(r1));
		VRPTWCustomer nearestCustomer = externalCustomers.remove();;
		
		// lo rimuovo dalla sua strada originale
		for (VRPTWRoute r : newSolution.routes) {
			if (r.serve(nearestCustomer)) {
				r.removeCustomer(nearestCustomer);
				if (r.travelDistance() < 0.0001) { // se era un cliente solitario, rimuovo la ruote vuota
					newSolution.removeRoute(r);
					if (debug) System.out.println("Rimozione di rotta <------");
				}
				break;
			}
		}

		// Calcola potenziali inserimenti nella rotta corrente 
		LinkedList<VRPTWCandidateCustomerInsertion> candidate_insertions = r1.candidate_insertions(nearestCustomer);
		Collections.sort(candidate_insertions);
		
		// Prova a fare gli inserimenti con incremento di costo minore
		ListIterator<VRPTWCandidateCustomerInsertion> itr = candidate_insertions.listIterator();
		VRPTWCandidateCustomerInsertion insertion = null;
		boolean inserted = false;
		
		while (itr.hasNext() && !inserted) {
			insertion = itr.next();
			inserted = r1.addCustomer(insertion.customer, insertion.prev_customer_idx, insertion.next_customer_idx);
		}
		
		// Se tutti gli inserimenti non sono fattibili nella rotta corrente crea una nuova rotta
		// TODO: non e' un po' un controsenso? cerchi un custumer vicino alla rotta ma se non trovi posto per inserirlo ne crei una nuova (quando l'obiettivo Ã¨ minimizzare le rotte)
		if (!inserted) {
			VRPTWRoute route = new VRPTWRoute(r1._warehouse, r1._initial_capacity);
			route.addCustomer(nearestCustomer);
			newSolution.addRoute(route);
			if (debug) System.out.println("Generazione di una nuova rotta <------");
		}
		
		// controllo se ho migliorato o meno		
		double cost_new = newSolution.cost();
		double cost_old = start_solution.cost();
		if (cost_new > cost_old) {
			if (debug) System.out.print("thread: soluzione peggiore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
			if (Math.random() < (temperature/(temperature + VRPTWParameters.delta))) {
				if (debug) System.out.println(" accettata comunque (T=" + Math.round(temperature) + ")");
			} else {
				// rifiuto la nuova soluzione, torno alla vecchia
				newSolution = start_solution;
				if (debug) System.out.println(" rifiutata");
			}
		} else
			if (debug) System.out.println("thread: soluzione migliore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
		
		return newSolution;		
	}
	

	// Pairwise exchange, or Lin–Kernighan heuristics.
	// The pairwise exchange or '2-opt' technique involves iteratively removing two edges and replacing these with two different edges 
	protected static VRPTWSolution annealing_step_exchange(VRPTWSolution start_solution, double temperature) {
		VRPTWSolution newSolution = start_solution.clone();
		
		Random rnd_gen = new Random();
	
		int r_idx1 = rnd_gen.nextInt( newSolution.routes.size() );
		int c_idx1 = rnd_gen.nextInt( (newSolution.routes.get(r_idx1).customers.size() - 2) ) + 1;
		VRPTWCustomer c1 = newSolution.routes.get(r_idx1).customers.get(c_idx1);
		
		int r_idx2 = rnd_gen.nextInt( newSolution.routes.size() );
		int c_idx2 = rnd_gen.nextInt( (newSolution.routes.get(r_idx2).customers.size() - 2) ) + 1;
		VRPTWCustomer c2 = newSolution.routes.get(r_idx2).customers.get(c_idx2);
	
		newSolution.routes.get(r_idx1).removeCustomer(c1);
		newSolution.routes.get(r_idx2).removeCustomer(c2);
		
		boolean inserted = false;
		
		if ( (r_idx1 == r_idx2) && (c_idx1<c_idx2) ) {
			inserted = newSolution.routes.get(r_idx1).addCustomer(c2, c_idx1-1, c_idx1);
			if (inserted)
				inserted = newSolution.routes.get(r_idx2).addCustomer(c1, c_idx2-1, c_idx2);
		} else {
			inserted = newSolution.routes.get(r_idx2).addCustomer(c1, c_idx2-1, c_idx2);
			if (inserted)
				inserted = newSolution.routes.get(r_idx1).addCustomer(c2, c_idx1-1, c_idx1);
		}			
		
		// Se unfeasible, nessuna modifica
		if (!inserted)
			return start_solution;
		
		// controllo se ho migliorato o meno		
		double cost_new = newSolution.cost();
		double cost_old = start_solution.cost();
		if (cost_new > cost_old) {
			if (debug) System.out.print("thread: soluzione peggiore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
			if (Math.random() < (temperature/(temperature + VRPTWParameters.delta))) {
				if (debug) System.out.println(" accettata comunque (T=" + Math.round(temperature) + ")");
			} else {
				// rifiuto la nuova soluzione, torno alla vecchia
				if (debug) System.out.println(" rifiutata");
				return start_solution;
			}
		} else
			if (debug) System.out.println("thread: soluzione migliore di quella di partenza: costo " + Math.round(newSolution.cost()) + " con " + newSolution.routes.size() + " mezzi");
	
		return newSolution;
	}
	
}
