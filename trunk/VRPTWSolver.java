public class VRPTWSolver {

  /*
  * Main per avviare il solver sul problema specificato.
  * Questa classe e' sostanzialmente P0.
  */
  public static void main(String[] args) {
    VRPTWProblem problem = new VRPTWProblem();
    VRPTWSolver solver = new VRPTWSolver(5); // processori
    VRPTWSolution solution = solver.resolve(problem);
    solution.show();
  }

  protected _processors;

  public VRPTWSolver(int processors) {
    _processors = processors;
  }

}
