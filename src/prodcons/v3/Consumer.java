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
			for (Message m : pcb._getInterruptedMessages()) {
				if (m != null) {
					System.out.print("Consumer id : " + getId() + " ");
					m.compute();
				}
			}
		}
	}
}
