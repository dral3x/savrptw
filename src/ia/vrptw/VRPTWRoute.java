package ia.vrptw;

import java.util.LinkedList;
import java.util.List;

public class VRPTWRoute {
	List<Integer> route;
	
	public VRPTWRoute() {
		route = new LinkedList<Integer>();
	}
	
	public void addCustomer(int costumer) {
		route.add(costumer);
	}
	
	public int size() {
		return route.size();
	}
	
	public double travelDistance(VRPTWProblem problem) {
		double distance = 0.0;
		  if (route.size() > 1) {
			  for (int c=0; c<route.size()-1; c++) {
				  VRPTWCustomer customer1 = problem.getCustomer(c); 
				  double a_x = customer1.getXPosition();
				  double a_y = customer1.getYPosition();
				  
				  VRPTWCustomer customer2 = problem.getCustomer(c+1);
				  double b_x = customer2.getXPosition();
				  double b_y = customer2.getYPosition();
				  
				  distance += VRPTWUtils.distance(a_x, a_y, b_x, b_y);
			  }
		  }
		  return distance;
	}
}
