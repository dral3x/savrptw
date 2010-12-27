package ia.vrptw;

public class VRPTWParameters {

	// numero di cicli non migliorativi prima di terminare tutto
	static final int tau = 40; // 20 - 40 

	// costante per pesare il costo dovuto al numero di rotte rispetto al costo dovuto alla distanza percorsa
	// gamma*(c*n + e_min) >> d
	static final double sigma = 100; // FIXME: costante messa a caso! dipenderà dall'ordine di grandezza di d nei problemi trattati
	
	// costante di proporzionalit� per regolare la temperatura con il costo della soluzione
	static final double gamma = 1; // 0.001 - 1.0

	// costante di proporzionalit� per abbassare la temperatura ad ogni step
	static final double beta = 0.92; // 0.1 - 0.99
	
	// variazione del costo della soluzione per calcolare la probabilit� di accettazione della soluzione peggiorativa
	static final double delta = 5.0; // 0.5 - 5
	
	
	static final int insertion_attempt = 10;
}
