package ia.vrptw;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * Classe che definisce un problema VRPTW con tutti i suoi parametri
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWProblem {

	String instance_name;
	double distance;
	int maxVehicles;
	double vehicleCapacity; // Q

	LinkedList<VRPTWCustomer> customers;
	
	/**
	 * Costruttore di un problema VRPTW
	 * 
	 * @param name nome del file con i dati
	 * @param vehicles numero di veicoli disponibili (max)
	 * @param capacity capacitˆ dei veicoli (uguale per tutti i veicoli)
	 */
	public VRPTWProblem(String name, int vehicles, double capacity) {
		instance_name = name;
		maxVehicles = vehicles;
		vehicleCapacity = capacity;
		
		// init customers from file "instance_name"
		customers = new LinkedList<VRPTWCustomer>();
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("problems/test1");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   {
				// Print the content on the console
				String[] data = strLine.split("\t");
				VRPTWCustomer customer = new VRPTWCustomer(Integer.parseInt(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5]), Double.parseDouble(data[6]));
				customers.add(customer);
			}
			//Close the input stream
			in.close();
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Esegue una stampa a video della "mappa" del problema
	 */
	public void show() {
		System.out.println("Problema "+instance_name);
	}
	
	public String getInstanceName() {
		return instance_name;
	}
	
	public VRPTWCustomer getCustomer(int customer) {
		if (customer >= 0 && customer <customers.size()) {
			return customers.get(customer);
		} else {
			return null;
		}
	}
	public int getNumberOfCustomers() {
		return customers.size();
	}
	
	public int getMaxVehicles() {
		return maxVehicles;
	}
}