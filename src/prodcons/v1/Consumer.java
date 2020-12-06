package prodcons.v1;

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
				sleep(consTime);
				consume().compute();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
