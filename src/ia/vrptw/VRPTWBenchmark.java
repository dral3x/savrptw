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
    		new VRPTWProblem("C101", 50, 200),
    		new VRPTWProblem("C102", 50, 200),
    		new VRPTWProblem("C103", 50, 200),
    		new VRPTWProblem("C104", 50, 200),
    		new VRPTWProblem("C105", 50, 200),
    		new VRPTWProblem("C106", 50, 200),
    		new VRPTWProblem("C107", 50, 200),
    		new VRPTWProblem("C108", 50, 200),
    		new VRPTWProblem("C109", 50, 200)
    };
    
    VRPTWProblem r1_problems[] = {
    		new VRPTWProblem("R101", 50, 200),
    		new VRPTWProblem("R102", 50, 200),
    		new VRPTWProblem("R103", 50, 200),
    		new VRPTWProblem("R104", 50, 200),
    		new VRPTWProblem("R105", 50, 200),
    		new VRPTWProblem("R106", 50, 200),
    		new VRPTWProblem("R107", 50, 200),
    		new VRPTWProblem("R108", 50, 200),
    		new VRPTWProblem("R109", 50, 200),
    		new VRPTWProblem("R110", 50, 200),
    		new VRPTWProblem("R111", 50, 200),
    		new VRPTWProblem("R112", 50, 200)
    };
    
    VRPTWProblem rc1_problems[] = {
    		new VRPTWProblem("RC101", 50, 200),
    		new VRPTWProblem("RC102", 50, 200),
    		new VRPTWProblem("RC103", 50, 200),
    		new VRPTWProblem("RC104", 50, 200),
    		new VRPTWProblem("RC105", 50, 200),
    		new VRPTWProblem("RC106", 50, 200),
    		new VRPTWProblem("RC107", 50, 200),
    		new VRPTWProblem("RC108", 50, 200)
    };
    
    for (VRPTWProblem p : rc1_problems) {
    	VRPTWSolver solver = new VRPTWSolver(4); // processori
    	System.out.println("<< INIZIO OTTIMIZZAZIONE >> ");
    	VRPTWSolution solution = solver.resolve(p);
    	System.out.print("<< OTT.COMPLETATA >> ");
    	solution.show();
    }
  }

}