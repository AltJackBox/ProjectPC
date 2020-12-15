package prodcons.v3;

import java.io.FileInputStream;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

import prodcons.v3.Consumer;
import prodcons.v3.ProdConsBuffer;
import prodcons.v3.Producer;
import utils.AllocId;

public class TestProdCons {

	public static void main(String[] args)
			throws InvalidPropertiesFormatException, FileNotFoundException, IOException, InterruptedException {
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("options.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));
		int nGet = Integer.parseInt(properties.getProperty("nGet"));
		
		Consumer[] tabCons = new Consumer[nCons];
		Producer[] tabProd = new Producer[nProd];

		ProdConsBuffer pcb = new ProdConsBuffer(bufSz);
		for (int i = 0; i < nCons; i++) {
			tabCons[i] = new Consumer(pcb, consTime, nGet);
		}
		for (int i = 0; i < nProd; i++) {
			tabProd[i] = new Producer(pcb, minProd, maxProd, prodTime, i);
		}
		int startCons = 0;
		int startProd = 0;
		long startTime = System.nanoTime();
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
			tabProd[i].join();
		}
		while (pcb.nmsg() > 0) {
			Thread.sleep(10);
		}
		Thread.sleep(nGet*consTime); // Laisse le temps au consommateur de consommer le message
		long endTime = System.nanoTime();
		for (int i = 0; i < nCons; i++) {
			tabCons[i].interrupt();
		}
		System.out.println("Tous les messages ont été transmis");
		System.out.println("Nombre de messages traités = " + pcb.totmsg());
		System.out.println("Nombre d'id alloués traités = " + AllocId.nbAlloc());
		System.out.println("Temps d'éxecution = " + ((endTime - startTime)/1000000) + " ms");
		float a = (float) (pcb.totmsg());
		float b = (float)(endTime - startTime)/1000000000;
		System.out.println("Performances = " + (a /b) + " msg/s");

	}
}
