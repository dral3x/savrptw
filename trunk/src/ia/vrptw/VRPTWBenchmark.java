package ia.vrptw;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWBenchmark {

  public static void main(String[] args) {
    VRPTWProblem problem = new VRPTWProblem("C101", 100, 200);
    VRPTWSolver solver = new VRPTWSolver(5); // processori
    VRPTWSolution solution = solver.resolve(problem);
    solution.show();
  }

}