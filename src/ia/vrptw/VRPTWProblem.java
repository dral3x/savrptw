package ia.vrptw;

/**
 * Classe che definisce un problema VRPTW con tutti i suoi parametri
 * @author Alessandro Calzavara
 * @author Simone Pozzobon
 * @author Michele Redolfi
 * @author Lorenzo Valerio
 *
 */
public class VRPTWProblem {

	String instanceName;
	double distance;
	int maxVehicles;
	double vehicleCapacity; // Q

	/**
	 * Costruttore di un problema VRPTW
	 * 
	 * @param name nome del file con i dati
	 * @param vehicles numero di veicoli disponibili (max)
	 * @param capacity capacitˆ dei veicoli (uguale per tutti i veicoli)
	 */
	public VRPTWProblem(String name, int vehicles, double capacity) {
		instanceName = name;
		maxVehicles = vehicles;
		vehicleCapacity = capacity;
	}

	/**
	 * Esegue una stampa a video della "mappa" del problema
	 */
	public void show() {
		System.out.println("Problema");
	}
}