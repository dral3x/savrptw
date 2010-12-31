package ia.vrptw;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class VRPTWRoute {

	LinkedList<VRPTWCustomer> customers;
	VRPTWCustomer _warehouse;
	double _initial_capacity;
	double _capacity;
//	double _travel_time;
	double _travel_distance;
	
	public VRPTWRoute(VRPTWCustomer warehouse, double initial_capacity) {
		customers = new LinkedList<VRPTWCustomer>();
		_warehouse = warehouse;
		_capacity = _initial_capacity = initial_capacity;
//		_travel_time = 0;
		_travel_distance = 0;
		
		customers.add(warehouse.clone());	// Le rotte hanno origine presso il Warehouse
		customers.add(warehouse.clone());	// e terminano presso il Warehouse
	}
	
	public boolean addCustomer(VRPTWCustomer customer, int prev_customer_idx, int next_customer_idx) {
		if (next_customer_idx - prev_customer_idx != 1)
			return false; // Inserimento fallito, dati non cooerenti
		
		if (_capacity < customer._demand)
			return false;
		
		VRPTWCustomer prev_customer = customers.get(prev_customer_idx);
		VRPTWCustomer next_customer = customers.get(next_customer_idx);
		
		double distance_prev_next = VRPTWUtils.distance(prev_customer, next_customer);
		double distance_prev_c = VRPTWUtils.distance(prev_customer, customer);
		double distance_c_next = VRPTWUtils.distance(customer, next_customer);		
		
		// Calcolo l'ora di arrivo effettiva dal cliente che sto inserendo
		double customer_arrival = prev_customer.getCompletedTime() + distance_prev_c;
		double customer_start = Math.max(customer.getStartTimeWindow(), customer_arrival);
		// Calcolo l'ora di arrivo effettiva dal cliente successivo se avvienisse l'inserimento
		double next_customer_new_arrival = customer_start + customer.getServiceTime() + distance_c_next;
		// Calcolo quando devo slittare la schedule per inserire il cliente 
		double push_forward_time = Math.max(0, next_customer_new_arrival - next_customer.getArrivalTime());
		
		if ( (customer_arrival <= customer.getEndTimeWindow()) && push_forward_keep_feasibility(next_customer_idx, push_forward_time)) {
			// Se l'inserimento e' fattibile aggiorna l'orario di arrivo effettivo ed eventualmente slitta i successivi
			customer.setArrivalTime( customer_arrival );
			customers.add(next_customer_idx, customer);

			// Slitta i successivi
			int i = next_customer_idx+1;
			while ( (i<customers.size()) && push_forward_time > 0) {
				customers.get(i).setArrivalTime( customers.get(i).getArrivalTime() + push_forward_time );
				if (i+1<customers.size())
					push_forward_time = Math.max( 0, push_forward_time-customers.get(i+1).getWaiting() );
				i++;
			}

			// Sottraggo la distanza che prima includeva il viaggio tra prev_customer e next_customer
			_travel_distance -= distance_prev_next;
			// Sommo la distanza derivata dalle due nuove tratte
			_travel_distance += distance_prev_c + distance_c_next;
			
			// Decremento la capacità del vettore
			_capacity -= customer._demand;
			return true;
		}
		return false;
	}
	

	public boolean addCustomer(VRPTWCustomer customer) {
		// Aggiunge il cliente alla fine, prima del ritorno in warehouse
		return addCustomer(customer, customers.size()-2, customers.size()-1);
	}
	
	
	public void removeCustomer(VRPTWCustomer customer) {
		// check che il cliente sia in questa strada
		int customer_idx = customers.indexOf(customer);
		if (customer_idx == -1)
			return;
		
		VRPTWCustomer prev_customer = customers.get(customer_idx-1);
		VRPTWCustomer next_customer = customers.get(customer_idx+1);
		
		double distance_prev_next = VRPTWUtils.distance(prev_customer, next_customer);
		double distance_prev_c = VRPTWUtils.distance(prev_customer, customer);
		double distance_c_next = VRPTWUtils.distance(customer, next_customer);		
		
		// Controllo se riesco a ridurre il tempo di inizio dei successivi
		double next_customer_arrival = Math.max(next_customer.getStartTimeWindow(), prev_customer.getCompletedTime() + distance_prev_next);
		double push_backward_time = next_customer.getArrivalTime() - next_customer_arrival;
		
		// Riesco ad ridurre il tempo di inizio delle attività successive di `push_backward_time` unità di tempo
		int i = customer_idx+1;
		while ( (i<customers.size()) && push_backward_time > 0) {
			double new_actual_arrival = Math.max(customers.get(i).getStartTimeWindow(), customers.get(i).getArrivalTime() - push_backward_time);
			push_backward_time -= customers.get(i).getArrivalTime() - new_actual_arrival; //TODO: da ricontrollare bene
			
			customers.get(i).setArrivalTime( new_actual_arrival );
			i++;
		}
		
		// Aggiorno _travel_distance, _capacity
		_travel_distance += distance_prev_next - distance_prev_c - distance_c_next;
		_capacity += customer._demand;
		
		// Rimuovo customer
		customers.remove(customer);
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
		if (customers.size() == 0)
			return 0;
		return customers.get(customers.size()-1).getCompletedTime();
	}
	

	// Controlla la fattibilità di slittare la schedulazione di `push_forward_time` unità di tempo dal cliente `customer_idx` in poi.
	// Ref. [Solomon - The Vehicle Routing and Scheduling Problem]
	public boolean push_forward_keep_feasibility(int customer_idx, double push_forward_time) {
		int i = customer_idx;
		while ( (i<customers.size()) && (customers.get(i).getArrivalTime() + push_forward_time < customers.get(i).getEndTimeWindow()) ) {
			i++;
			if (i != customers.size()) {
				// Aggiorno il push_forward_time sottraendolo ad eventuali tempi di attesa
				push_forward_time = Math.max( 0, push_forward_time-customers.get(i).getWaiting() );
				if (push_forward_time == 0)
					return true;	// Lo slittamente è compensato da tempi di attesa presso clienti successivi nella rotta
			}
		}
		if ( (i==customers.size()) && (customers.get(i-1).getArrivalTime() + push_forward_time < customers.get(i-1).getEndTimeWindow()) )
			return true;
		return false;
	}

		
	// Restituisce i customer migliori candidati all'inserimento nella rotta corrente.
	public LinkedList<VRPTWCustomer> candidate_customers(LinkedList<VRPTWCustomer> unallocated_pool) {
		// Customers are  selected for route  insertion  in a particular order. 
		// Three  ordering  rules  are  used  to  help  achieve  time  window  feasibility  during  the 
		// construction phase. *Rule one*  for selecting the next customer is based  on  the  smallest 
		// early  time  window  parameter  ej.  *Rule  two*  is  based  on  the  tightness  of  the  time 
		// window  as  calculated  by  100  (lj  -  ej)  -  d_0j,  where d_0j  denotes  the  distance  from  the 
		// depot  to  customer_j.  The  number  100  is  an  arbitrary  weight  used  to  emphasize  the 
		// tightness  of  the  time  window  relative  to  the  distance  from  the  depot.  Rule  three  is 
		// based  on  the  largest  value  of  d_0j.
		
		LinkedList<VRPTWCustomer> out = new LinkedList<VRPTWCustomer>();
		ListIterator<VRPTWCustomer> itr = null;
		
		// Rule one
		double min_cost = Double.MAX_VALUE;
		
		VRPTWCustomer smaller_start_time_customer = null;
		itr = unallocated_pool.listIterator();
		while(itr.hasNext()) {
			VRPTWCustomer c = itr.next();
			if (c.getStartTimeWindow() < min_cost) {
				smaller_start_time_customer = c;
				min_cost = c.getStartTimeWindow();
			}
		}
		if (smaller_start_time_customer != null)
			out.add(smaller_start_time_customer);
		

		// Rule two
		// DUBBIO: stando al paper a parità di finestra selezione quello più distante dal magazzino. Perchè? Perché più difficilmente schedulabile?
		min_cost = Double.MAX_VALUE;
		
		VRPTWCustomer tight_window_customer = null;
		itr = unallocated_pool.listIterator();
		while(itr.hasNext()) {
			VRPTWCustomer c = itr.next();
			double cost = 1000 * (c.getEndTimeWindow() - c.getStartTimeWindow()) - VRPTWUtils.distance(c, _warehouse);
			
			if ( (cost < min_cost) && (c != smaller_start_time_customer) ) {
				tight_window_customer = c;
				min_cost = cost;
			}
		}
		if (tight_window_customer != null)
			out.add(tight_window_customer);
		
		
		// Rule three
		double max_cost = Double.MIN_VALUE;
		
		VRPTWCustomer farthest_customer = null;
		itr = unallocated_pool.listIterator();
		while(itr.hasNext()) {
			VRPTWCustomer c = itr.next();
			double cost = VRPTWUtils.distance(c, _warehouse);
			
			if ( (cost > max_cost)  && (c != smaller_start_time_customer) && (c != tight_window_customer) ) {
				farthest_customer = c;
				max_cost = cost;
			}
		}
		if (farthest_customer != null)
			out.add(farthest_customer);
		
		return out;
	}
	
	
	// Calcola tutti i possibili inserimenti di un customer candidato calcolandone i relativo aumento di costo in seguito all'eventuale inserimento.
	public LinkedList<VRPTWCandidateCustomerInsertion> candidate_insertions(VRPTWCustomer customer) {
		LinkedList<VRPTWCandidateCustomerInsertion> out = new LinkedList<VRPTWCandidateCustomerInsertion>();
		
		if (_capacity < customer._demand)
			return out;	//FIXME: possibile deadlock se tutti e tre i clienti richiedono capacità maggiori?!
		
		VRPTWCustomer prev_customer = customers.get(0);
		double distance_c_next, distance_prev_next, distance_prev_c = VRPTWUtils.distance(prev_customer, customer);
		
		// prev_customer e next_customer appartengono alla rotta mentre customer è il candidato da inserire tra prev_customer e next_customer.
		// Ref. [Chiang, Russell - Simulated annealing metaheuristics for the vehicle routing problem with time windows]
		int i = 1;
		while (i < customers.size()) {
			VRPTWCustomer next_customer = customers.get(i);
				
			distance_prev_next = VRPTWUtils.distance(prev_customer, next_customer);
			//distance_prev_c = VRPTWUtils.distance(prev_customer, customer);
			distance_c_next = VRPTWUtils.distance(customer, next_customer);		
			
			// Calcolo l'ora di arrivo effettiva dal cliente che sto inserendo
			double customer_arrival = Math.max(customer.getStartTimeWindow(), prev_customer.getCompletedTime() + distance_prev_c);
			// Calcolo l'ora di arrivo effettiva dal cliente successivo se avvienisse l'inserimento
			double next_customer_new_arrival = Math.max(next_customer.getStartTimeWindow(), customer_arrival + customer.getServiceTime() + distance_c_next);
			// Calcolo quando devo slittare la schedule per inserire il cliente 
			double push_forward_time = Math.max(0, next_customer_new_arrival - next_customer.getArrivalTime());
			
			// Ref. [Chiang, Russell] (1)
			double distance_increase = distance_prev_c + distance_c_next - distance_prev_next;
			out.add( new VRPTWCandidateCustomerInsertion(customer, i-1, i, distance_increase) );
			
			// Ref. [Chiang, Russell] (2)
			double local_sched_time_increase = push_forward_time;
			out.add( new VRPTWCandidateCustomerInsertion(customer, i-1, i, local_sched_time_increase) );
			
			prev_customer = next_customer;
			distance_prev_c = distance_c_next;
			i++;
		}
		return out;		
	}

	
	public double getRemainCapacity() {
		return _capacity;
	}
	
	
	public int size() {
		return customers.size();
	}
	
	
	public void show() {
		// route 10: 15 48 16 12 10 88 60 75 87 58 53;
		for (int c = 0; c<customers.size()-1; c++) {
			System.out.print(customers.get(c).getID() + " ");
		}
		System.out.println(customers.get(customers.size()-1).getID() + ";");
	}
	
	public String toString() {
		String description = "";
		
		for (int c = 0; c<customers.size()-1; c++) {
			description += customers.get(c).getID() + " ";
		}
		description += customers.get(customers.size()-1).getID() + ";";
		
		return description;
	}
	
	
	public boolean serve(VRPTWCustomer customer) {
		return customers.contains(customer);
	}

}
