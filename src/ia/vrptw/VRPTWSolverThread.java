package ia.vrptw;

import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class VRPTWSolverThread implements Runnable {
	
	boolean go;
	VRPTWSolution _best_local_solution;
	VRPTWSolution _coworker_solution;
	VRPTWSolution _old_solution;
	VRPTWProblem _problem;
	
	LinkedList<VRPTWSolution> _solutions;
	CyclicBarrier _start_barrier;
	CyclicBarrier _done_barrier;
	CyclicBarrier _cooperate_barrier;
	
	VRPTWSolverThread _coworker;
	
	double temperature;
	double gamma = 0.001; // 0.001 - 1.0
	int customers;
	
	public VRPTWSolverThread(VRPTWProblem problem, VRPTWSolution solution, LinkedList<VRPTWSolution> solutions, CyclicBarrier start, CyclicBarrier done, CyclicBarrier cooperate) {
		_old_solution = solution;
		_best_local_solution = solution;
		_problem = problem;
		_solutions = solutions;
		
		_start_barrier = start;
		_done_barrier = done;
		_cooperate_barrier = cooperate;
		
		temperature = gamma * _old_solution.cost();
		customers = problem.getNumberOfCustomers();
		
		_coworker_solution = null;
		go = true;
	}
	
	public void cooperateWith(VRPTWSolverThread socio) {
		_coworker = socio;
	}
	
	public void setCoWorkerSolution(VRPTWSolution another_solution) {
		_coworker_solution = another_solution;
	}
	
	public VRPTWSolution getSolution() {
		return _best_local_solution;
	}
	
	public void stop() {
		go = false;
	}

	@Override
	public void run() {
		
		System.out.println("SolverThread "+Thread.currentThread().getId()+" avviato!");
		
		// attende partenza 
		try {
			_start_barrier.await();
		} catch (BrokenBarrierException e) {
			go = false;
		} catch (InterruptedException e) {
			go = false;
		}
		
		while (go) {
			
			// fa quel che deve
			
			for (int iteration=1; iteration < customers*customers; iteration++ ) {
				System.out.println("thread-"+Thread.currentThread().getId()+" iterazione "+iteration);
				_best_local_solution = annealing_step(_best_local_solution);
				
				// ogni n iterazioni (n = numero di clienti) coopero col "vicino"
				if (iteration % customers == 0) { // co-operate
					synchronized(_coworker) {
						_coworker.setCoWorkerSolution(_best_local_solution);
						_coworker.notify();
					}
					
					synchronized(this) {
						while (_coworker_solution == null) {
							try {
								System.out.println("thread-"+Thread.currentThread().getId()+" attendo gli altri");
								this.wait();
							} catch (InterruptedException e) {
								go = false;
								break;
							}
						}
					}
					
					// prendo la solutione del vicino se  migliore della  mia
					System.out.println("thread-"+Thread.currentThread().getId()+" controllo solutione arrivata dagli altri");
					if (_coworker_solution.cost() < _best_local_solution.cost()) {
						_best_local_solution = _coworker_solution;
					}
					_coworker_solution = null;
				}
			}
			
			// consegna la soluzione trovata
			System.out.println("thread-"+Thread.currentThread().getId()+" consegna soluzione best_local");
			synchronized(_solutions) {
				_solutions.add(_best_local_solution);
			}
			//System.out.println("aggiunta soluzione");

			// attendo una notify dal solver
			try {
				_done_barrier.await();
				// attendo una notify dal solver
				System.out.println("thread-"+Thread.currentThread().getId()+" in attesa di iniziare di nuovo");
				_start_barrier.await();
			} catch (BrokenBarrierException e) {
				go = false;
			} catch (InterruptedException e) {
				go = false;
			}
			//System.out.println("sono stato notificato!");
			
		}
		
		System.out.println("SolverThread "+Thread.currentThread().getId()+" termina");
	}
	
	private VRPTWSolution annealing_step(VRPTWSolution start_solution) {
		// TODO da implementare seriamente
		VRPTWSolution newSolution = start_solution;
		
		try {
			Thread.sleep(Math.round(Math.random()*500));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return newSolution;
	}
}
