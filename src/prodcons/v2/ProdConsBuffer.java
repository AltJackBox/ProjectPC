package prodcons.v2;

import java.util.concurrent.Semaphore;

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

	/*
	 * Semaphore pour gère l'accès aux buffer pour les consumers,
	 * 1 seul lit le buffer à la fois
	 */
	private Semaphore mget;
	/*
	 * Semaphore pour gère l'accès aux buffer pour les producer,
	 * 1 seul ecrit dans le buffer à la fois
	 */
	private Semaphore mput;
	
	/*
	 * Semaphore qui met en attente les poducer si le buffer est plein
	 */
	private Semaphore wput;
	/*
	 * Semaphore qui met en attente les consumer si le buffer est vide
	 */
	private Semaphore wget;

	public ProdConsBuffer(int bufferSz) {
		this.bufferSz = bufferSz;
		buffer = new Message[bufferSz];
		in = 0;
		out = 0;
		nmess = 0;
		total = 0;
		wput = new Semaphore(0);
		wget = new Semaphore(0);
		mput = new Semaphore(1);
		mget = new Semaphore(1);
	}

	@Override
	public void put(Message m) throws InterruptedException {
		try {
			mput.acquire(); // le producer acquire la ressource
			while (nmess >= bufferSz) {
				/*
				 * Si le buffer est plein, il libère la ressources et se met en attente
				 */
				mput.release();
				wput.acquire();
				mput.acquire();
			}
			buffer[in] = m;
			in += 1;
			in %= bufferSz;
			total++;
			nmess++;
			/*
			 * Après avoir produit le message, il release un consumer qui a été mis en attente car le buffer était vide.
			 */
			wget.release();
		} finally {
			/*
			 * Puis le producer libère la ressource
			 */
			mput.release();
		}
	}

	@Override
	public Message get() throws InterruptedException {
		Message m;
		try {
			mget.acquire();// le consumer acquire la ressource
			while (nmess <= 0) {
				/*
				 * Si le buffer est vide, il libère la ressources et se met en attente
				 */
				mget.release();
				wget.acquire();
				mget.acquire();
			}
			m = buffer[out];
			out += 1;
			out %= bufferSz;
			nmess--;
			/*
			 * Après avoir lut un message, il release un pducer qui a été mis en attente car le buffer était plein.
			 */
			wput.release();
		} finally {
			/*
			 * Puis le consumer libère la ressource
			 */
			mget.release();
		}
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
