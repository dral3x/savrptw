package ia.vrptw;

import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWSolution {

	String _instance_name;
	VRPTWProblem _problem;
	double distance;
	int _max_vehicles;
	LinkedList<VRPTWRoute> routes;

	public VRPTWSolution(VRPTWProblem problem) {
		if (problem == null)
			throw new IllegalArgumentException("Invalid problem instance");

		_problem = problem;
		_instance_name = problem.getInstanceName();
		routes = new LinkedList<VRPTWRoute>();
	}

	public double totalTravelDistance() {
		double _cost = 0;
		for(int r=0; r<routes.size(); r++) {
			_cost += routes.get(r).travelDistance();
		}
		return _cost;
	}

	public double cost() {
		// cost(S) = d + sigma*(c*n + e_min)
		double d = totalTravelDistance();
		double c = routes.size();
		double n = _problem.getNumberOfCustomers();
		int e_min = Integer.MAX_VALUE;
		for (VRPTWRoute r : routes) {
			if (r.size() < e_min) e_min = r.size();
		}
		return d + VRPTWParameters.sigma*(c*n + e_min);
//		return d + VRPTWParameters.sigma*(c*n);
	}
	
	public void show() { 
		// Problem instance: RC104. Distance: 1135.48. Vehicles: 10.

		double km = getDistance();

		System.out.println("Problem instance: "+_instance_name + ". Distance: "+km+ ". Vehicles: "+routes.size()+".");
	}

	public void showAll() { 
		// Problem instance: RC104. Distance: 1135.48. Vehicles: 10.
		// route 1: 86 64 90 77 85 52 65 84 67;
		// route 2: 23 21 50 20 24 22 49 19 26 25;

		double km = getDistance();
	
		System.out.println("Problem instance: "+_instance_name + ". Distance: "+km+ ". Vehicles: "+routes.size()+".");

		for (int i=0; i<routes.size(); i++) {
			System.out.print("route "+(i+1)+": ");
			routes.get(i).show();
		}
	}
	
	public void addRoute(VRPTWRoute route) {
		routes.add(route);
	}
	
	// Importa la descrizione testuale della route in output da toString per ricostruirla
	// (funzione con finalità di test)
	public void addRoute(String route_description) {
		VRPTWRoute newroute = new VRPTWRoute(_problem.getWarehouse(), _problem.getVehicleCapacity());
		
		Scanner scanner = new Scanner(route_description);
		scanner.useDelimiter(" ");
		
		while ( scanner.hasNext() ) {
		int customerid = Integer.parseInt(scanner.next());
			VRPTWCustomer customer = _problem.getCustomer(customerid);
			if (!customer.isWarehouse()) {
				if (customer.getArrivalTime() != 0) {
					System.err.println("Cliente già inserito");
					System.exit(1);
				}
				newroute.addCustomer(customer);
			}
	    }		
		routes.add(newroute);
	}

	public void removeRoute(VRPTWRoute route) {
		routes.remove(route);
	}
	
	public VRPTWSolution clone() {

		VRPTWSolution clone = new VRPTWSolution(_problem);
		for (VRPTWRoute route : routes) {
			VRPTWRoute newRoute = new VRPTWRoute(_problem.getWarehouse(), _problem.getVehicleCapacity());
			for (VRPTWCustomer customer : route.customers) {
				if (!customer.isWarehouse())
					newRoute.addCustomer(customer.clone());
			}
			clone.addRoute(newRoute);
		}

		return clone;
	}
	
	// (funzione con finalità di test)
	public int customers_size() {
		int tot_cust = 0;
		for (VRPTWRoute r : routes) {
			if (r.customers.size() > 2)
				tot_cust += r.customers.size() - 2;
		}
		return tot_cust;
	}
	
	public double getDistance() {
		double km = 0;
		for (VRPTWRoute r : routes)
			km += r._travel_distance;
		return km;
	}
	
	public int getVehicles(){
		return routes.size();
	}
	
	public String toString() { 
		// Problem instance: RC104. Distance: 1135.48. Vehicles: 10.
		// route 1: 86 64 90 77 85 52 65 84 67;
		// route 2: 23 21 50 20 24 22 49 19 26 25
		
		double km = getDistance();
	
		String result = "Problem instance: "+_instance_name + ". Distance: "+km+ ". Vehicles: "+routes.size()+"."+'\n';

		for (int i=0; i<routes.size(); i++) {
			result += "route "+(i+1)+": "+ routes.get(i)+'\n';
		}
		return result;
	}

}