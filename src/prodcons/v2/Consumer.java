package prodcons.v2;

import utils.Message;

public class Consumer extends Thread{
	
	ProdConsBuffer pcb;
	int consTime;
	
	public Consumer(ProdConsBuffer pcb, int consTime) {
		this.pcb= pcb;
		this.consTime = consTime;
	}

	public Message consume() throws InterruptedException {
		return pcb.get();
	}
	
	public void run() {
		try {
			while (true) {
				consume().compute();
				sleep(consTime);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
	}
}
