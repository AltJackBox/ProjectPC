package prodcons.v4;

import java.util.concurrent.Semaphore;

import utils.IProdConsBuffer;

import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer {

	private int bufferSz;
	private Message[] buffer;
	private int nMultiplePut;
	private int nMultipleGet;
	private int in, out;
	private int nmess;
	private int total;
	private Semaphore wput;
	private Semaphore wget;
	private Semaphore wMultiplePut;
	private Semaphore wMultipleGet;
	private Semaphore multipleGet;
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
		put(m, 1);
	}

	@Override
	public Message get() throws InterruptedException {
		Message mess;
		try {
			multipleGet.acquire();
			while (nmess <= 0) {
				multipleGet.release();
				wget.acquire();
				multipleGet.acquire();
			}
			mess = buffer[out];
			out += 1;
			out %= bufferSz;
			nmess--;
			nMultipleGet++;
			wput.release();
		} finally {
			if (nMultipleGet < nMultiplePut) {
				multipleGet.release();
				wMultipleGet.acquire();
			} else {
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
			multiplePut.acquire();
			nMultiplePut = n;
			nMultipleGet = 0;
			while (n > 0) {
				while (nmess >= bufferSz) {
					wput.acquire();
				}
				buffer[in] = m;
				in += 1;
				in %= bufferSz;
				total++;
				nmess++;
				wget.release();
				n--;
			}
		} finally {

			wMultiplePut.acquire();
			multiplePut.release();
		}
	}

}
