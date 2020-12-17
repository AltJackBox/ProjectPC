package prodcons.v1;

/*
 * Classe consumer
 */
public class Consumer extends Thread{
	
	ProdConsBuffer pcb;
	int consTime;
	
	public Consumer(ProdConsBuffer pcb, int consTime) {
		this.pcb= pcb;
		this.consTime = consTime;
	}


	/*
	 * Le consumer, jusqu'a son interuption, va lire un message dans le ProdConsBuffer 
	 * et va le lire lire (methode compute de message)
	 */
	public void run() {
		try {
			while (true) {
				pcb.get().compute();				
				sleep(consTime);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}
}
