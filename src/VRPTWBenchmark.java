public class VRPTWBenchmark {

  public static void main(String[] args) {
    VRPTWProblem problem = new VRPTWProblem();
    VRPTWSolver solver = new VRPTWSolver(5); // processori
    VRPTWSolution solution = solver.resolve(problem);
    solution.show();
  }

}