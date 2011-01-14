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

    		new VRPTWProblem("C101", 200),
    		new VRPTWProblem("C102", 200),
    		new VRPTWProblem("C103", 200),
    		new VRPTWProblem("C104", 200),
    		new VRPTWProblem("C105", 200),
    		new VRPTWProblem("C106", 200),
    		new VRPTWProblem("C107", 200),
    		new VRPTWProblem("C108", 200),
    		new VRPTWProblem("C109", 200)
    };
    
    VRPTWProblem r1_problems[] = {
    		new VRPTWProblem("R101", 200),
    		new VRPTWProblem("R102", 200),
    		new VRPTWProblem("R103", 200),
    		new VRPTWProblem("R104", 200),
    		new VRPTWProblem("R105", 200),
    		new VRPTWProblem("R106", 200),
    		new VRPTWProblem("R107", 200),
    		new VRPTWProblem("R108", 200),
    		new VRPTWProblem("R109", 200),
    		new VRPTWProblem("R110", 200),
    		new VRPTWProblem("R111", 200),
    		new VRPTWProblem("R112", 200)
    };
    
    VRPTWProblem rc1_problems[] = {
    		new VRPTWProblem("RC101", 200),
    		new VRPTWProblem("RC102", 200),
    		new VRPTWProblem("RC103", 200),
    		new VRPTWProblem("RC104", 200),
    		new VRPTWProblem("RC105", 200),
    		new VRPTWProblem("RC106", 200),
    		new VRPTWProblem("RC107", 200),
    		new VRPTWProblem("RC108", 200)
    };
    
    for (VRPTWProblem p : rc1_problems) {
    	VRPTWSolver solver = new VRPTWSolver(VRPTWParameters.threads); // processori
    	System.out.println("<< INIZIO OTTIMIZZAZIONE >> ");
    	VRPTWSolution solution = solver.resolve(p);
    	System.out.print("<< OTT.COMPLETATA >> ");
    	solution.show();
    }
  }

}
