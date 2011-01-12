package ia.vrptw;

public class VRPTWParameters {

	// numero di cicli non migliorativi prima di terminare tutto
	static final int tau = 20; // 20 - 40 

	// costante per pesare il costo dovuto al numero di rotte rispetto al costo dovuto alla distanza percorsa

	// sigma*(c*n + e_min) >> d
	static final double sigma = 1; // 100

	// costante di proporzionalitÔøΩ per regolare la temperatura con il costo della soluzione
	static final double gamma = 0.5; // 0.001 - 1.0

	// costante di proporzionalitÔøΩ per abbassare la temperatura ad ogni step
	static final double beta = 0.75; // 0.1 - 0.99
	
	// variazione del costo della soluzione per calcolare la probabilitÔøΩ di accettazione della soluzione peggiorativa
	// dovrebbe essere paragonabile alla temperatura per fare effetto... e la temperatura è gamma*costo_soluzione_iniziale
	//static final double delta = 2000; // 0.5 - 5
	
	// ora delta è un fattore moltiplicativo del costo della soluzione iniziale, usata per generare la probabilità
	// di accettazione di una soluzione peggiorativa 
	// più piccolo è il delta, più alta è la probabilità di accettare soluzioni peggiorative
	static final double delta = 1; // 0.5 - 2

}
