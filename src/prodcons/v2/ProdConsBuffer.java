package prodcons.v2;

import java.util.concurrent.Semaphore;


import utils.IProdConsBuffer;

import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer {

	private int bufferSz;
	private Message buffer[];
	private int in, out;
	private int nmess;
	private int total;
	private Semaphore mget;
	private Semaphore mput;
	private Semaphore wput;
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
		Message m;
		try {
			mget.acquire();
			while (nmess <= 0) {
				mget.release();
				wget.acquire();
				mget.acquire();
			}
			m = buffer[out];
			out += 1;
			out %= bufferSz;
			nmess--;
			wput.release();
		} finally {
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
