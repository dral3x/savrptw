package ia.vrptw;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWBenchmark {

	public static void main(String[] args) throws InterruptedException {

		
		VRPTWProblem c1_problems[] = {
									new VRPTWProblem("C101", 200), new VRPTWProblem("C102", 200),
									new VRPTWProblem("C103", 200), new VRPTWProblem("C104", 200),
									new VRPTWProblem("C105", 200), new VRPTWProblem("C106", 200),
									new VRPTWProblem("C107", 200), new VRPTWProblem("C108", 200),
									new VRPTWProblem("C109", 200)
									};
		
		VRPTWProblem c2_problems[] = {
									new VRPTWProblem("C201", 700), new VRPTWProblem("C202", 700),
									new VRPTWProblem("C203", 700), new VRPTWProblem("C204", 700),
									new VRPTWProblem("C205", 700), new VRPTWProblem("C206", 700),
									new VRPTWProblem("C207", 700), new VRPTWProblem("C208", 700)
									};

		VRPTWProblem r1_problems[] = {
									new VRPTWProblem("R101", 200), new VRPTWProblem("R102", 200),
									new VRPTWProblem("R103", 200), new VRPTWProblem("R104", 200), 
									new VRPTWProblem("R105", 200), new VRPTWProblem("R106", 200), 
									new VRPTWProblem("R107", 200), new VRPTWProblem("R108", 200), 
									new VRPTWProblem("R109", 200), new VRPTWProblem("R110", 200),
									new VRPTWProblem("R111", 200), new VRPTWProblem("R112", 200)
									};

		VRPTWProblem r2_problems[] = {
									new VRPTWProblem("R201", 1000)
									};
		
		VRPTWProblem rc1_problems[] = {
									new VRPTWProblem("RC101", 200), new VRPTWProblem("RC102", 200), 
									new VRPTWProblem("RC103", 200), new VRPTWProblem("RC104", 200), 
									new VRPTWProblem("RC105", 200), new VRPTWProblem("RC106", 200), 
									new VRPTWProblem("RC107", 200), new VRPTWProblem("RC108", 200)
									};

		VRPTWProblem rc2_problems[] = {
									new VRPTWProblem("RC201", 1000), new VRPTWProblem("RC202", 1000),
									new VRPTWProblem("RC205", 1000)
									};

		for (VRPTWProblem p : rc1_problems) {
			VRPTWSolver solver = new VRPTWSolver();
			//solver.activateDrawingSolutionsMode();
			System.out.println("<< INIZIO OTTIMIZZAZIONE "+p.getInstanceName()+" >> ");
			VRPTWSolution solution = solver.resolve(p);
			System.out.println("<< OTT.COMPLETATA >> ");
			solution.show();
			solution.checkBestKnownSolutionImproved();
		}

	}

}
