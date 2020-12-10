package prodcons.v1;

import utils.IProdConsBuffer;
import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer {

	private int bufferSz;
	private Message buffer[];
	private int in, out;
	private int nmess;
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
			wait();
		}
		buffer[in] = m;
		in += 1;
		in %= bufferSz;
		total++;
		nmess++;
		notifyAll();
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		while (nmess <= 0) {
			wait();
		}
		Message m = buffer[out];
		out += 1;
		out %= bufferSz;
		nmess--;
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

}
