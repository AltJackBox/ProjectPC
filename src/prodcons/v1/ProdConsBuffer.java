package prodcons.v1;

import utils.IProdConsBuffer;
import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer {

	/*
	 * taille du buffer
	 */
	private int bufferSz;
	private Message buffer[];
	/*
	 * indice de position de lecture et d'écriture dans le buffer
	 */
	private int in, out; 
	/*
	 * nombre de message actuel dans le buffer
	 */
	private int nmess;
	/*
	 * nombre de message totale écrit dans le buffer
	 */
	private int total;

	public ProdConsBuffer(int bufferSz) {
		this.bufferSz = bufferSz;
		buffer = new Message[bufferSz];
		in = 0;
		out = 0;
		nmess = 0;
		total = 0;
	}

	@Override
	public synchronized void put(Message m) throws InterruptedException {
		while (nmess >= bufferSz) {
			/*
			 * si le buffer est plein, on attend
			 */
			wait();
		}
		buffer[in] = m;
		in += 1;
		in %= bufferSz;
		total++;
		nmess++;
		/*
		 * on notify tout le monde, producer et consummer, qu'un message a été écrit
		 */
		notifyAll();
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		while (nmess <= 0) {
			/*
			 * si le buffer est vide, on attend
			 */
			wait();
		}
		Message m = buffer[out];
		out += 1;
		out %= bufferSz;
		nmess--;
		/*
		 * on notify tout le monde, producer et consummer, qu'un message a été lu
		 */
		notifyAll();
		return m;
	}

	@Override
	public int nmsg() {
		return nmess;
	}

	@Override
	public int totmsg() {
		return total;
	}

	@Override
	public Message[] get(int k) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

}
