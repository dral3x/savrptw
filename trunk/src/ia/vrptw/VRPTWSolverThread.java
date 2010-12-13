package ia.vrptw;

import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class VRPTWSolverThread implements Runnable {
	
	boolean go;
	VRPTWSolution _solution;
	VRPTWProblem _problem;
	
	LinkedList<VRPTWSolution> _solutions;
	CyclicBarrier _start_barrier;
	CyclicBarrier _done_barrier;
		
	public VRPTWSolverThread(VRPTWProblem problem, VRPTWSolution solution, LinkedList<VRPTWSolution> solutions, CyclicBarrier start, CyclicBarrier done) {
		_solution = solution;
		_problem = problem;
		_solutions = solutions;
		_start_barrier = start;
		_done_barrier = done;
		
		go = true;
	}
	
	public VRPTWSolution getSolution() {
		return _solution;
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
			
			// fa cose
			
			
			// consegna la soluzione trovata
			_solutions.add(_solution);
			//System.out.println("aggiunta soluzione");

			// attendo una notify dal solver
			//System.out.println("thread in attesa di notifica");
			try {
				_done_barrier.await();
				// attendo una notify dal solver
				//System.out.println("thread in attesa di notifica");
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
}
