package prodcons.v1;

import utils.IProdConsBuffer;
import utils.Message;

public class ProdConsBuffer implements IProdConsBuffer{
	
	int bufferSz;
	Message buffer[];
	int in, out;
	int nmess;
	int total;
	
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
		if (nmess >= bufferSz) {
			wait();
		}
		buffer[in] = m;
		in += 1;
		in %= bufferSz;
		total++;
		nmess++;
		notify();
	}

	@Override
	public synchronized Message get() throws InterruptedException {
		if (nmess < 0) {
			wait();
		}
		Message m = buffer[out];
		out += 1;
		out %= bufferSz;
		nmess--;
		notify();
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
