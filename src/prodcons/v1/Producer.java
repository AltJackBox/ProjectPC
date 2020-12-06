package prodcons.v1;

import utils.Message;

public class Producer extends Thread{	
	
	ProdConsBuffer pcb;
	Message mess;
	
	public Producer(ProdConsBuffer pcb, Message m) {
		this.pcb= pcb;
		mess = m;
	}

	public void produce() throws InterruptedException {
		pcb.put(mess);
	}
	
	public void run() {
		try {
			produce();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
