package prodcons.v4;

public class Consumer extends Thread{
	
	ProdConsBuffer pcb;
	int consTime;
	
	public Consumer(ProdConsBuffer pcb, int consTime) {
		this.pcb= pcb;
		this.consTime = consTime;
	}


	/*
	 * Le consumer, jusqu'à son interruption, va lire des messages dans le buffer
	 * et va les afficher
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
