package ia.vrptw;

public class VRPTWParameters {

	// numero di cicli non migliorativi prima di terminare tutto
	static final int tau = 10; // 20 - 40 

	// costante di proporzionalitˆ per regolare la temperatura con il costo della soluzione
	static final double gamma = 0.001; // 0.001 - 1.0

	// costante di proporzionalitˆ per abbassare la temperatura ad ogni step
	static final double beta = 0.92; // 0.1 - 0.99
	
	// variazione del costo della soluzione per calcolare la probabilitˆ di accettazione della soluzione peggiorativa
	// Since the basic criterion of optimization is the number of routes, the constant should be large enough, so that
	// delta*(c*n + e_min) >> d
	static final double delta = 1.0; // 0.5 - 5
	
}
