package ia.vrptw;

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

	VRPTWSolverThread[] threads;

	/*
	 * Main per avviare il solver sul problema specificato.
	 * Questa classe e' sostanzialmente P0.
	 */
	public static void main(String[] args) throws InterruptedException {
		VRPTWProblem problem = new VRPTWProblem("test1", 4, 40);
		VRPTWSolver solver = new VRPTWSolver(2); // processori
		VRPTWSolution solution = solver.resolve(problem);
		solution.show();
	}

	protected int _processors;

	public VRPTWSolver(int processors) {
		_processors = processors;
		threads = new VRPTWSolverThread[_processors];
	}

	public VRPTWSolution resolve(VRPTWProblem problem) throws InterruptedException {
		// scelgo una soluzione da cui partire (a caso?)
		VRPTWSolution finalSolution = generateFirstSolution(problem);
		LinkedList<VRPTWSolution> solutions = new LinkedList<VRPTWSolution>();
		
		// istanzio i thread paralleli e li faccio partire dalla soluzione generata
		threads = new VRPTWSolverThread[_processors];
		CyclicBarrier _start_barrier = new CyclicBarrier(_processors+1);
		CyclicBarrier _done_barrier = new CyclicBarrier(_processors+1);
	     
		for (int i=0; i<_processors; i++) {
			threads[i] = new VRPTWSolverThread(problem, finalSolution, solutions, _start_barrier, _done_barrier);
			new Thread(threads[i]).start();
		}
		
		int tau = 100;
		int equilibrium = 0;
		while (equilibrium < tau) {
			System.out.println("giro "+equilibrium);
			
			try {
				_start_barrier.await();
				_start_barrier.reset();
				//System.out.println("attendo che tutti i risolutori abbiano consegnato qualcosa");
				_done_barrier.await();
				_done_barrier.reset();
			} catch (InterruptedException e) {
				break;
			} catch (BrokenBarrierException e) {
				break;
			}

			if (solutions.size() != _processors) {
				// tutti i thread hanno consegnato qualcosa
				System.err.println("QUALCUNO HA MANCATO LA CONSEGNA!");
			}
			//System.out.println("ho ricevuto tutte le soluzioni, sceglio la migliore e vado avanti");
			
			// scelgo la soluzione migliore tra quelle trovate finora
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
			} else {
				equilibrium ++;
			}
			
			// fermo i thread se non ho fatto progressi
			if (equilibrium >= tau) {
				for (int i=0; i<_processors; i++) {
					threads[i].stop();
				}
				try {
					_start_barrier.await();
				} catch (BrokenBarrierException e) {
					break;
				}
			}
			
			
			
		}
	
		// ritorno la meglio soluzione trovata finora
		return finalSolution;
	}
	
	private VRPTWSolution generateFirstSolution(VRPTWProblem problem)  {
		VRPTWSolution solution = new VRPTWSolution(problem);
		
		//for (int c=0; c<problem.getNumberOfCustomers(); c++) {
			
		//}
		
		return solution;
	}

}
