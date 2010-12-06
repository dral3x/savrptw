public class VRPTWProblem {
  
  String instanceName;
  double distance;
  int maxVehicles;
  double vehicleCapacity; // Q
  
  public VRPTWProblem(String name, int vehicles, double capacity) {
    instanceName = name;
    macVehicles = vehicles;
    vehicleCapacity = capacity;
    completed = false;
  }

  public show() {
    if (completed) {
    } else {
      System.out.println("Soluzione parziale");
    }
  }
}