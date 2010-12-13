package ia.vrptw;

import java.util.LinkedList;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWSolution {
/*
Problem instance: RC104. Distance: 1135.48. Vehicles: 10.
route 1: 86 64 90 77 85 52 65 84 67;
route 2: 23 21 50 20 24 22 49 19 26 25;
route 3: 81 92 57 96 63 68 95 94 72 73 55 97 82;
route 4: 62 71 2 4 6 46 9 47 5 101 69;
route 5: 91 66 100 98 76 59 78;
route 6: 43 45 44 39 38 36 37 41 40 42;
route 7: 89 61 79 74 80 8 7 3 56;
route 8: 70 99 54 13 18 17 14 11 83;
route 9: 93 51 34 33 31 29 27 28 30 32 35;
route 10: 15 48 16 12 10 88 60 75 87 58 53;
*/

  String _instance_name;
  VRPTWProblem _problem;
  double distance;
  int _max_vehicles;
  boolean completed;
  LinkedList<VRPTWRoute> routes;
  
  // Since the basic criterion of optimization is the num- ber of routes, the constant	should be large enough, so that
  // delta*(c*n + e_min) >> d
  double delta = 0.5; // 0.5 ... 5
  

  public VRPTWSolution(VRPTWProblem problem) {
	  if (problem == null)
		  throw new IllegalArgumentException("Invalid problem instance");
	  
	  _problem = problem;
	  _instance_name = problem.getInstanceName();
	  completed = false;
	  _max_vehicles = problem.getMaxVehicles();
	  routes = new LinkedList<VRPTWRoute>();
  }
  
  public double totalTravelDistance() {
	  double _cost = 0;
	  for(int r=0; r<routes.size(); r++) {
		  _cost += routes.get(r).travelDistance(_problem);
	  }
	  return _cost;
  }

  public double cost() {
	  // cost(S) = d + delta*(c*n + e_min)
	  double d = totalTravelDistance();
	  double c = routes.size();
	  double n = _problem.getNumberOfCustomers();
	  double e_min = 0.0;
	  
	  return d + delta*(c*n + e_min);
  }
  
  public void show() {
    if (completed) {
      System.out.println("Soluzione completa a "+_instance_name);
    } else {
      System.out.println("Soluzione parziale a "+_instance_name);
    }
  }
  
  public void addRoute(VRPTWRoute route) {
	  routes.add(route);
  }

}