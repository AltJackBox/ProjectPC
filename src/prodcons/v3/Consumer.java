package prodcons.v3;

import utils.Message;

public class Consumer extends Thread {

	ProdConsBuffer pcb;
	int consTime;
	int nGet;

	public Consumer(ProdConsBuffer pcb, int consTime, int nGet) {
		this.pcb = pcb;
		this.consTime = consTime;
		this.nGet = nGet;
	}

	public void run() {
		try {
			while (true) {
				for (Message m : pcb.get(nGet)) {
					if (m != null) {
						System.out.print("Consumer id : " + getId() + " ");
						m.compute();
						sleep(consTime);
					}
				}
			}
		} catch (InterruptedException e) {
		}
	}
}
