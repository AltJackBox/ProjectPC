package prodcons.v3;

import java.util.concurrent.Semaphore;

import utils.IProdConsBuffer;

import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer {

	private int bufferSz;
	private Message[] buffer;
	private Message[] _interruptedMessages;
	private int in, out;
	private int nmess;
	private int total;
	private Semaphore mput;
	private Semaphore wput;
	private Semaphore wget;
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
			mput.acquire();
			while (nmess >= bufferSz) {
				mput.release();
				wput.acquire();
				mput.acquire();
			}
			buffer[in] = m;
			in += 1;
			in %= bufferSz;
			total++;
			nmess++;
			wget.release();
		} finally {
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
	
	public Message[] _getInterruptedMessages() {
		return _interruptedMessages;
	}

	@Override
	public Message[] get(int k){
		int i = 0;
		Message[] tabMess = new Message[k];
		try {
			multipleGet.acquire();
			while (i < k) {
				while (nmess <= 0) {
					wget.acquire();
				}
				tabMess[i] = buffer[out];
				out += 1;
				out %= bufferSz;
				nmess--;
				i++;
				wput.release();
			}
		}catch (InterruptedException e) {
			_interruptedMessages = tabMess;
		} finally {
			multipleGet.release();
		}
		return tabMess;
	}

	@Override
	public void put(Message m, int n) throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

}
