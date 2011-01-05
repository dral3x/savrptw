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
		_max_vehicles = problem.getMaxVehicles();
		routes = new LinkedList<VRPTWRoute>();
	}

	public double totalTravelDistance() {
		double _cost = 0;
		for(int r=0; r<routes.size(); r++) {
			//_cost += routes.get(r).travelDistance(_problem);
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
	}
	
	public void show() { 
		// Problem instance: RC104. Distance: 1135.48. Vehicles: 10.
		// route 1: 86 64 90 77 85 52 65 84 67;
		// route 2: 23 21 50 20 24 22 49 19 26 25;

		double km = 0.0;
		for (VRPTWRoute r : routes)
			km += r._travel_distance;

		System.out.println("Problem instance: "+_instance_name + ". Distance: "+km+ ". Vehicles: "+routes.size()+".");

		for (int i=0; i<routes.size(); i++) {
			System.out.print("route "+(i+1)+": ");
			routes.get(i).show();
		}
	}

	public void addRoute(VRPTWRoute route) {
		routes.add(route);
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

}