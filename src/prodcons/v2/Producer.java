package prodcons.v2;

import utils.Message;

public class Producer extends Thread{	
	
	ProdConsBuffer pcb;
	int nbProd;
	int prodTime;
	int id;
	
	public Producer(ProdConsBuffer pcb, int minProd, int maxProd, int prodTime, int id) {
		this.pcb= pcb;
		nbProd = minProd + (int) (Math.random()*((maxProd - minProd) + 1));
		this.prodTime = prodTime;
		this.id = id;
	}

	public void produce() throws InterruptedException {
		pcb.put(new Message(id));
	}
	
	public void run() {
		try {
			while (nbProd > 0) {
				produce();
				nbProd--;
				sleep(prodTime);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
