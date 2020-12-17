package prodcons.v3;

import utils.Message;

public class Consumer extends Thread {

	ProdConsBuffer pcb;
	int consTime;
	/*
	 * Nombre de messages à consommer
	 */
	int nGet;

	public Consumer(ProdConsBuffer pcb, int consTime, int nGet) {
		this.pcb = pcb;
		this.consTime = consTime;
		this.nGet = nGet;
	}

	/*
	 * Le consumer, jusqu'à son interuption, va lire nGet messages dans le ProdConsBuffer 
	 * et va les afficher
	 * Cependant, il est possible que les consummers s'attendent a lire nGet message mais 
	 * seul n messages vont être produits (n < nGet), cela arrive nottament lorsque tous les 
	 * messages ont été lus, le programme de test interromp alors tous les consumers
	 * Lorsque cela arrive, un consumer peut être interrompu alors qu'il n'a pas pu encore lire nGet
	 * messages
	 * Auquel cas, il va, après être interrompu, traiter les n messages qui ont déjà été lus
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
