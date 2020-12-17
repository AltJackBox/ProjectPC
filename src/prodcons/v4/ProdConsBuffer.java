package prodcons.v4;

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
	
	private int nMultiplePut; //Nombre de copy du message totale
	private int nMultipleGet; //Nombre de copy du message lue

	/*
	 * Semaphore qui met en attente les poducer si le buffer est plein
	 */
	private Semaphore wput;
	/*
	 * Semaphore qui met en attente les consumer si le buffer est vide
	 */
	private Semaphore wget;
	
	/*
	 * Lorsqu'un producter a produit les k messages,
	 * il attend dans wMultiplePut que tout les messages qu'il a produit
	 * soit lut
	 */
	private Semaphore wMultiplePut;
	
	/*
	 * Lorsqu'un consumer a consumer 1 message,
	 * il attend dans wMultipleGet que tout les autres messages 
	 * soit lues
	 */
	private Semaphore wMultipleGet;

	/*
	 * Semaphore pour gère l'accès aux buffer pour les consumers,
	 * 1 seul lit le buffer à la fois
	 * De plus, le consumer ne libère pas la ressource tant qu'il n'a pas lu 
	 * tout les messages consécutifs qu'il devait lire
	 */
	private Semaphore multipleGet;
	
	/*
	 * Semaphore pour gère l'accès aux buffer pour les producer,
	 * 1 seul lit le buffer à la fois
	 * De plus, le producer ne libère pas la ressource tant qu'il n'a pas produit 
	 * tout les messages consécutifs qu'il devait produire
	 */
	
	private Semaphore multiplePut;

	public ProdConsBuffer(int bufferSz) {
		this.bufferSz = bufferSz;
		buffer = new Message[bufferSz];
		in = 0;
		out = 0;
		nmess = 0;
		total = 0;
		nMultiplePut = 0;
		nMultipleGet = 0;
		wput = new Semaphore(0);
		wget = new Semaphore(0);
		wMultiplePut = new Semaphore(0);
		wMultipleGet = new Semaphore(0);
		multipleGet = new Semaphore(1);
		multiplePut = new Semaphore(1);
	}

	@Override
	public void put(Message m) throws InterruptedException {
		//
	}

	@Override
	public Message get() throws InterruptedException {
		Message mess;
		try {
			multipleGet.acquire();// le consumer acquire la ressource
			while (nmess <= 0) {
				/*
				 * Si il n'y as plus de message, le consummer attend, 
				 */
				multipleGet.release();
				wget.acquire();
				multipleGet.acquire();
			}
			mess = buffer[out];
			out += 1;
			out %= bufferSz;
			nmess--;
			nMultipleGet++;
			/*
			 * Après avoir lut un message, il release un producer qui a été mis en attente car le buffer était plein.
			 */
			wput.release();
		} finally {
			
			if (nMultipleGet < nMultiplePut) {
				/*
				 * Si les nMulptipleGet messages n'ont pas été tout lut,
				 * le consummer attend que ce soit le cas
				 */
				multipleGet.release();
				wMultipleGet.acquire();
			} else {
				/*
				 * Sinon, c'est que le consumer a lut le dernier message,
				 * donc il libère tout les consummers
				 * et libère le producer qui est en attente
				 */
				wMultipleGet.release(nMultiplePut - 1);
				wMultiplePut.release();
				multipleGet.release();
			}
		}
		return mess;
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
		return null;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		try {
			multiplePut.acquire(); // le producer acquire la ressource
			/*
			 * Initilisation des variables,
			 * n copy 
			 * 0 lues
			 */
			nMultiplePut = n; 
			nMultipleGet = 0;
			while (n > 0) {
				while (nmess >= bufferSz) {
					/*
					 * Si il n'y as plus de place, le producer attend, 
					 * mais sans liberer la ressources, car il ne doit pas
					 * se faire doubler par un autre producer.
					 * Il doit ecrire n copies consécutives
					 */
					wput.acquire();
				}
				buffer[in] = m;
				in += 1;
				in %= bufferSz;
				total++;
				nmess++;
				/*
				 * Après avoir ecrit un message, il release un consumer qui a été mis en attente car le buffer était vide.
				 */
				wget.release();
				n--;
			}
		} finally {
			/*
			 * Puis il se met en attente. Il attend que 
			 * toutes les copies écrites soit lues
			 * Il ne li_bère pas la ressources, car sinon un autre producer pourrait
			 * ecrire.
			 */
			wMultiplePut.acquire();
			multiplePut.release();
		}
	}

}
