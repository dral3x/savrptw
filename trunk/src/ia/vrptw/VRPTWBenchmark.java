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
    VRPTWProblem problems[] = {
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
    
    for (VRPTWProblem p : problems) {
    	VRPTWSolver solver = new VRPTWSolver(4); // processori
    	System.out.println("<< INIZIO OTTIMIZZAZIONE >> ");
    	VRPTWSolution solution = solver.resolve(p);
    	System.out.print("<< OTT.COMPLETATA >> ");
    	solution.show();
    }
  }

}