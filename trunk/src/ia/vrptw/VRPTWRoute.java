package ia.vrptw;

import java.util.LinkedList;
import java.util.List;

public class VRPTWRoute {

	List<VRPTWCustomer> route;
	VRPTWCustomer _warehouse;
	double capacity;
	double _travel_time;
	double _travel_distance;
	
	public VRPTWRoute(VRPTWCustomer warehouse, double initial_capacity) {
		route = new LinkedList<VRPTWCustomer>();
		_warehouse = warehouse;
		capacity = initial_capacity;
		_travel_time = 0;
		_travel_distance = 0;
	}
	
	public void addCustomer(VRPTWCustomer customer) {
		
		// aggiorno capacity e travel time
		VRPTWCustomer last_customer = _warehouse;
		if (route.size()>0)
			last_customer = route.get(route.size()-1);
		
		double distance = VRPTWUtils.distance(last_customer.getXPosition(), last_customer.getYPosition(), customer.getXPosition(), customer.getYPosition()); 
		_travel_time += distance+customer._service_time;
		_travel_distance += distance;
		capacity -= customer._demand;
			
		// aggiungo il cliente alla rotta
		route.add(customer);
	}
	
	public int size() {
		return route.size();
	}
	
//	@Deprecated
//	public double travelDistance(VRPTWProblem problem) {
//		double distance = 0.0;
//		  if (route.size() > 1) {
//			  for (int c=0; c<route.size()-1; c++) {
//				  VRPTWCustomer customer1 = problem.getCustomer(c); 
//				  double a_x = customer1.getXPosition();
//				  double a_y = customer1.getYPosition();
//				  
//				  VRPTWCustomer customer2 = problem.getCustomer(c+1);
//				  double b_x = customer2.getXPosition();
//				  double b_y = customer2.getYPosition();
//				  
//				  distance += VRPTWUtils.distance(a_x, a_y, b_x, b_y);
//			  }
//		  }
//		  return distance;
//	}

	public double travelDistance() {
		  return _travel_distance;
	}
	
	public double travelTime() {
		return _travel_time;
	}
	
	public void show() {
		// route 10: 15 48 16 12 10 88 60 75 87 58 53;
		for (int c = 0; c<route.size()-1; c++) {
			System.out.print(route.get(c).getID() + " ");
		}
		System.out.println(route.get(route.size()-1).getID() + ";");
	}
}
