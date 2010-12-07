package ia.vrptw;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWSolver {

  /*
  * Main per avviare il solver sul problema specificato.
  * Questa classe e' sostanzialmente P0.
  */
  public static void main(String[] args) {
    VRPTWProblem problem = new VRPTWProblem("C101", 100, 200);
    VRPTWSolver solver = new VRPTWSolver(5); // processori
    VRPTWSolution solution = solver.resolve(problem);
    solution.show();
  }

  protected int _processors;

  public VRPTWSolver(int processors) {
    _processors = processors;
  }
  
  public VRPTWSolution resolve(VRPTWProblem problem) {
	  return null;
  }

}
