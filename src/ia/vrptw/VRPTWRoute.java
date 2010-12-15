package ia.vrptw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VRPTWRoute {

	List<VRPTWCustomer> customers;
	VRPTWCustomer _warehouse;
	double _initial_capacity;
	double _capacity;
	double _travel_time;
	double _travel_distance;
	
	public VRPTWRoute(VRPTWCustomer warehouse, double initial_capacity) {
		customers = new LinkedList<VRPTWCustomer>();
		_warehouse = warehouse;
		_capacity = _initial_capacity = initial_capacity;
		_travel_time = 0;
		_travel_distance = 0;
	}
	
	public void addCustomer(VRPTWCustomer customer) {
		
		// aggiorno capacity e travel time
		VRPTWCustomer last_customer = _warehouse;
		if (customers.size()>0)
			last_customer = customers.get(customers.size()-1);
		
		double distance = VRPTWUtils.distance(last_customer.getXPosition(), last_customer.getYPosition(), customer.getXPosition(), customer.getYPosition()); 
		_travel_time += distance+customer._service_time;
		_travel_distance += distance;
		_capacity -= customer._demand;
			
		// aggiungo il cliente alla rotta
		customers.add(customer);
	}
	
	public boolean addCustomerIfPossible(VRPTWCustomer customer) {
		
		// creo il nuovo percorso basato sulla finestra temporale
		LinkedList<VRPTWCustomer> newRoute = new LinkedList<VRPTWCustomer>();
		for (VRPTWCustomer c : customers)
			newRoute.add(c);
		newRoute.add(customer);
		Collections.sort(newRoute, new VRPTWCustomerEndTimeWindowComparator());

		// controllo se effettivamente ce la fa
		double time = 0;
		double travel_distance = 0;
		double capacity = _initial_capacity;
		VRPTWCustomer last_customer = _warehouse;
		for (VRPTWCustomer c : newRoute) {
			double distance = VRPTWUtils.distance(last_customer.getXPosition(), last_customer.getYPosition(), customer.getXPosition(), customer.getYPosition());
			time += distance;
			if (time > c._due_date) {
				// non ce la fa a servirlo in tempo!
				return false;
			}
			if (capacity < customer._demand) {
				// non ha spazio per la merce del cliente
				return false;
			}
			time += c._service_time;
			travel_distance += distance;
			capacity -= customer._demand;
		}
		// ce la fa ancora
		
		// aggiorno la rotta, capacity e travel time
		customers = newRoute;
		_travel_distance = travel_distance;
		_capacity = capacity;
		
		return true;
	}
	
	public double getRemainCapacity() {
		return _capacity;
	}
	
	
	public int size() {
		return customers.size();
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
		for (int c = 0; c<customers.size()-1; c++) {
			System.out.print(customers.get(c).getID() + " ");
		}
		System.out.println(customers.get(customers.size()-1).getID() + ";");
	}
	

}
