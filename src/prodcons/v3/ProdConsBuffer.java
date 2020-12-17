package prodcons.v3;

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
	
	/*
	 * tableau des messages lus par un consumer, mais qui n'ont pas été renvoyés car le consummer a 
	 * été intérrompu. Ce cas arrive si un consummer attend X messages, mais que les producer ne 
	 * vont produire que Y messages, avec Y < N.
	 * Au quel cas, le consummer va attendre le reste des messages, qui n'existent pas, et va se 
	 * faire interompre par le main
	 * Les Y messages lus vont pouvoir être récuperés et traités via ce buffer.
	 */
	private Message[] _interruptedMessages;

	/*
	 * Semaphore pour gère l'accès au buffer par les producer,
	 * 1 seul producer doit pouvoir écrire dans le buffer à la fois
	 */
	private Semaphore mput;
	
	/*
	 * Semaphore qui met en attente les producers si le buffer est plein
	 */
	private Semaphore wput;
	/*
	 * Semaphore qui met en attente les consumers si le buffer est vide
	 */
	private Semaphore wget;
	
	/*
	 * Semaphore pour gérer l'accès au buffer par les consumers,
	 * 1 seul consumer doit pouvoir lire le buffer à la fois
	 */
	private Semaphore multipleGet;

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
		multipleGet = new Semaphore(1);
	}

	@Override
	public void put(Message m) throws InterruptedException {
		try {
			mput.acquire(); // le producer acquiert la ressource
			while (nmess >= bufferSz) {
				/*
				 * Si le buffer est plein, il libère la ressource et se met en attente
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
			 * Après avoir produit le message, il libère un consumer qui a été mis en attente 
			 * car le buffer était vide.
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
		return get(1)[0];
	}

	@Override
	public int nmsg() {
		return nmess;
	}

	@Override
	public int totmsg() {
		return total;
	}
	/*
	 * Méthode qui permet de récupérer les messages lus lors de l'interruption d'un consumer
	 */
	public Message[] _getInterruptedMessages() {
		return _interruptedMessages;
	}

	@Override
	public Message[] get(int k){
		int i = 0;
		Message[] tabMess = new Message[k];
		try {
			multipleGet.acquire();// le consumer acquiert la ressource
			while (i < k) {
				while (nmess <= 0) {
					/*
					 * Si il n'y a plus de message, le consummer attend, 
					 * mais sans liberer la ressources, car il ne doit pas
					 * se faire doubler par un autre consummer.
					 * Il doit pouvoir lire k messages consécutifs
					 */
					wget.acquire();
				}
				tabMess[i] = buffer[out];
				out += 1;
				out %= bufferSz;
				nmess--;
				i++;
				/*
				 * Après avoir lu un message, il libère un producer qui a été mis en 
				 * attente car le buffer était plein.
				 */
				wput.release();
			}
		}catch (InterruptedException e) {
			/*
			 * Si le consumer est interompu, il stocke les messages qu'il a lu
			 * afin qu'ils puissent être traités sans être perdus
			 */
			_interruptedMessages = tabMess;
		} finally {
			/*
			 * Puis le consumer libère la ressource
			 */
			multipleGet.release();
		}
		return tabMess;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		
	}

}
