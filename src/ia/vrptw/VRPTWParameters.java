package ia.vrptw;

public class VRPTWParameters {

	// numero di thread che concorrono al processo di ottimizzazione
	static final int threads = 4; 
	
	// numero di cicli non migliorativi prima di terminare tutto
	static final int tau = 20; // 20 - 40 

	// costante per pesare il costo dovuto al numero di rotte rispetto al costo dovuto alla distanza percorsa

	// sigma*(c*n + e_min) >> d
	static final double sigma = 1; // 100

	// costante di proporzionalit� per regolare la temperatura con il costo della soluzione
	static final double gamma = 0.5; // 0.001 - 1.0

	// costante di proporzionalit� per abbassare la temperatura ad ogni step
	static final double beta = 0.75; // 0.1 - 0.99
	
	// variazione del costo della soluzione per calcolare la probabilit� di accettazione della soluzione peggiorativa
	// dovrebbe essere paragonabile alla temperatura per fare effetto... e la temperatura � gamma*costo_soluzione_iniziale
	//static final double delta = 2000; // 0.5 - 5
	
	// ora delta � un fattore moltiplicativo del costo della soluzione iniziale, usata per generare la probabilit�
	// di accettazione di una soluzione peggiorativa 
	// pi� piccolo � il delta, pi� alta � la probabilit� di accettare soluzioni peggiorative
	static final double delta = 1; // 0.5 - 2

}
