package prodcons.v4;

import utils.Message;

public class Consumer extends Thread {

	ProdConsBuffer pcb;
	int consTime;

	public Consumer(ProdConsBuffer pcb, int consTime) {
		this.pcb = pcb;
		this.consTime = consTime;
	}

	public void run() {
		try {
			while (true) {
				Message m = pcb.get();
				//System.out.println("Consumer id : " + getId());
				m.compute();
				sleep(consTime);
			}
		} catch (InterruptedException e) {
		}
	}
}
