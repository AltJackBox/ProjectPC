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
	 * indices de position de lecture et d'écriture dans le buffer
	 */
	private int in, out;
	/*
	 * nombre de messages présents dans le buffer
	 */
	private int nmess;
	/*
	 * nombre de messages total écrits dans le buffer depuis sa création
	 */
	private int total;
	
	private int nMultiplePut; //Nombre total de copies du message produites par un producer
	private int nMultipleGet; //Nombre total de lectures du message par un consumer

	/*
	 * Semaphore qui met en attente les poducers si le buffer est plein
	 */
	private Semaphore wput;
	/*
	 * Semaphore qui met en attente les consumers si le buffer est vide
	 */
	private Semaphore wget;
	
	/*
	 * Lorsqu'un producer a produit les k messages,
	 * il attend dans wMultiplePut que tous les messages qu'il a produit
	 * soient lus
	 */
	private Semaphore wMultiplePut;
	
	/*
	 * Lorsqu'un consumer a lu 1 message,
	 * il attend dans wMultipleGet que toutes les autres copies du message
	 * soient lues par d'autres consumers
	 */
	private Semaphore wMultipleGet;

	/*
	 * Semaphore pour gérer l'accès au buffer par les consumers,
	 * 1 seul consumer doit pouvoir lire le buffer à la fois
	 */
	private Semaphore multipleGet;
	
	/*
	 * Semaphore pour gère l'accès aux buffer pour les producer,
	 * 1 seul lit le buffer à la fois
	 * De plus, le producer ne libère pas la ressource tant qu'il n'a pas produit 
	 * toutes les copies du message qu'il doit produire
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
	}

	@Override
	public Message get() throws InterruptedException {
		Message mess;
		try {
			multipleGet.acquire();// le consumer acquiert la ressource
			while (nmess <= 0) {
				/*
				 * Si il n'y a plus de message dans le buffer, le consummer attend
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
			 * Après avoir lu un message, il libère un producer qui a été mis en attente car 
			 * le buffer était plein.
			 */
			wput.release();
		} finally {
			
			if (nMultipleGet < nMultiplePut) {
				/*
				 * Si les nMulptipleGet messages n'ont pas été tous lus,
				 * le consummer attend que ce soit le cas
				 */
				multipleGet.release();
				wMultipleGet.acquire();
			} else {
				/*
				 * Sinon, c'est que le consumer a lu le dernier message,
				 * donc il libère tout les consummers bloqués
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
			multiplePut.acquire(); // le producer acquiert la ressource
			/*
			 * Initilisation des variables,
			 * n copies du message à produire
			 * 0 actuellement lues
			 */
			nMultiplePut = n; 
			nMultipleGet = 0;
			while (n > 0) {
				while (nmess >= bufferSz) {
					/*
					 * Si il n'y as plus de place dans le buffer, le producer attend, 
					 * mais sans liberer la ressource, car il ne doit pas
					 * se faire doubler par un autre producer.
					 * Il doit écrire n copies consécutives du message
					 */
					wput.acquire();
				}
				buffer[in] = m;
				in += 1;
				in %= bufferSz;
				total++;
				nmess++;
				/*
				 * Après avoir écrit un message, il release un consumer qui a été mis 
				 * en attente car le buffer était vide.
				 */
				wget.release();
				n--;
			}
		} finally {
			/*
			 * Puis il se met en attente. Il attend que 
			 * toutes les copies écrites soit lues
			 * Il ne libère pas la ressource, car sinon un autre producer pourrait
			 * écrire avant que tous les messages produits n'aient été consommés
			 */
			wMultiplePut.acquire();
			multiplePut.release();
		}
	}

}
