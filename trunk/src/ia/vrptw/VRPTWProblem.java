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
	double bestDistance;
	int bestVehicles;

	LinkedList<VRPTWCustomer> customers;
	
	/**
	 * Costruttore di un problema VRPTW
	 * 
	 * @param name nome del file con i dati
	 * @param vehicles numero di veicoli disponibili (max)
	 * @param capacity capacit� dei veicoli (uguale per tutti i veicoli)
	 */
	public VRPTWProblem(String name, int vehicles, double capacity) {
		instance_name = name;
		maxVehicles = vehicles;
		vehicleCapacity = capacity;
		bestDistance = 0;
		bestVehicles = 0;
		
		// init customers from file "instance_name"
		customers = new LinkedList<VRPTWCustomer>();
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("problems/"+instance_name);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
	
			while ((strLine = br.readLine()) != null)   { // read file line by line
				String[] data = strLine.split("\t");
				if (data[0].equals("Best")){
					bestDistance = Double.parseDouble(data[1]);
					bestVehicles = Integer.parseInt(data[2]);
				} else 
				{
					VRPTWCustomer customer = new VRPTWCustomer(Integer.parseInt(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]), Double.parseDouble(data[3]), Double.parseDouble(data[4]), Double.parseDouble(data[5]), Double.parseDouble(data[6]));
					customers.add(customer);
				}
			}
			in.close(); //Close the input stream
		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Esegue una stampa a video della "mappa" del problema
	 */
	public void show() {
		System.out.println("Problema "+instance_name);
		for (VRPTWCustomer c : customers) {
			c.show();
		}
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
	
	public double getVehicleCapacity() {
		return vehicleCapacity;
	}
	
	public int getCurrentBestVehicles() {
		return bestVehicles;
	}
	
	public double getCurrentBestDistance() {
		return bestDistance;
	}
	
	public VRPTWCustomer getWarehouse() {
		return customers.element();
	}
}