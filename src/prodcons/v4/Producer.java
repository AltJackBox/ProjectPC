package prodcons.v4;

import utils.Message;

public class Producer extends Thread{	
	
	ProdConsBuffer pcb;
	int nbProd;
	int prodTime;
	int id;
	int maxExempl;
	
	public Producer(ProdConsBuffer pcb, int minProd, int maxProd, int prodTime, int id, int maxExempl) {
		this.pcb= pcb;
		nbProd = minProd + (int) (Math.random()*((maxProd - minProd) + 1));
		this.prodTime = prodTime;
		this.id = id;
		this.maxExempl = (int) (Math.random()*maxExempl + 1);
	}
	
	public void run() {
		try {
			while (nbProd > 0) {
				Message m = new Message(id);
				pcb.put(m, maxExempl);
				nbProd--;
				System.out.println("## ## Producer "+id+" produit message "+m.getId()+ " en "+maxExempl);
				sleep(prodTime*maxExempl);
				
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
