package prodcons.v3;

import utils.Message;

/*
 * Classe consumer
 */
public class Consumer extends Thread {

	ProdConsBuffer pcb;
	int consTime;
	/*
	 * Nombre de message a get
	 */
	int nGet;

	public Consumer(ProdConsBuffer pcb, int consTime, int nGet) {
		this.pcb = pcb;
		this.consTime = consTime;
		this.nGet = nGet;
	}

	/*
	 * Le consumer, jusqu'a son interuption, va lire nGet messages dans le ProdConsBuffer 
	 * et va les lire (methode compute de message)
	 * Cependant, il est possible que le consummer s'attendent a lire nGet message mais 
	 * seul n messages vont être produit (n < nGet). 
	 * Au quel cas, ce consummer pourrait être interronpu lors de la lecture de nGet messages (get)
	 * Auquel cas, il va, après être intépondu, traiter les n messages lus
	 */
	public void run() {
		Message[] getRes;
		try {
			while (true) {
				getRes = pcb.get(nGet);
				for (Message m : getRes) {
					if (m != null) {
						System.out.print("Consumer id : " + getId() + " ");
						m.compute();
						sleep(consTime);
					}
				}
			}
		} catch (InterruptedException e) {
			/*
			 * traitement des derniers messages
			 */
			for (Message m : pcb._getInterruptedMessages()) {
				if (m != null) {
					System.out.print("Consumer id : " + getId() + " ");
					m.compute();
				}
			}
		}
	}
}
