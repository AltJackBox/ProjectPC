package prodcons.v1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {

	public static void main(String[] args) throws InvalidPropertiesFormatException, FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("options.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

		Consumer[] tabCons = new Consumer[nCons];
		Producer[] tabProd = new Producer[nProd];

		ProdConsBuffer pcb = new ProdConsBuffer(bufSz);
		for (int i = 0; i < nCons; i++) {
			tabCons[i] = new Consumer(pcb, consTime);
		}
		for (int i = 0; i < nProd; i++) {
			tabProd[i] = new Producer(pcb, minProd, maxProd, prodTime, i);
		}
		int startCons = 0;
		int startProd = 0;
		while (true) {
			if ((startCons < nCons) && (startProd < nProd)) {
				if (new Random().nextBoolean()) {
					tabCons[startCons++].start();
				} else {
					tabProd[startProd++].start();
				}
			} else if (startCons < nCons) {
				tabCons[startCons++].start();
			} else if (startProd < nProd) {
				tabProd[startProd++].start();
			} else {
				break;
			}
		}
		for (int i = 0; i < nProd; i++) {
			try {
				tabProd[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (pcb.nmsg() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0; i < nCons; i++) {
			tabCons[i].interrupt();
		}
		System.out.println("Tous les messages ont été transmis");
	}
}
