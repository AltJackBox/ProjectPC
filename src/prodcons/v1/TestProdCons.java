package prodcons.v1;

import java.io.FileInputStream;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

import prodcons.v1.Consumer;
import prodcons.v1.ProdConsBuffer;
import prodcons.v1.Producer;
import utils.AllocId;

public class TestProdCons {

	public static void main(String[] args)
			throws InvalidPropertiesFormatException, FileNotFoundException, IOException, InterruptedException {
		/*
		 * on lit les options dans le fichier options.xml
		 */
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("options.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

		/*
		 * Initialisation des producer et consumer
		 */
		Consumer[] tabCons = new Consumer[nCons];
		Producer[] tabProd = new Producer[nProd];

		ProdConsBuffer pcb = new ProdConsBuffer(bufSz);
		for (int i = 0; i < nCons; i++) {
			tabCons[i] = new Consumer(pcb, consTime);
		}
		for (int i = 0; i < nProd; i++) {
			tabProd[i] = new Producer(pcb, minProd, maxProd, prodTime, i);
		}
		/*
		 * on run les thread aléatoirement
		 */
		int startCons = 0; //nb producer run
		int startProd = 0; //nb consumer run
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
		/*
		 * on attend que les producer ont crée tout leur message
		 */
		for (int i = 0; i < nProd; i++) {
			tabProd[i].join();
		}
		/*
		 * tant qu'il reste des messages a traiter dans le buffer, on attent que les messages soient consommés
		 */
		while (pcb.nmsg() > 0) {
			Thread.sleep(10);
		}
		/*
		 * Lors que le dernier message est lu dans le buffer, on attend que le consummer qui l'a lu finisse le traitement
		 */
		Thread.sleep(consTime); 
		long endTime = System.nanoTime();
		/*
		 * on arrete les consummers
		 */
		for (int i = 0; i < nCons; i++) {
			tabCons[i].interrupt();
		}
		
		/*
		 * Affichage final avec le sperfommances
		 */
		System.out.println("Tous les messages ont été transmis");
		System.out.println("Nombre de messages traités = " + pcb.totmsg());
		System.out.println("Nombre d'id alloués traités = " + AllocId.nbAlloc());
		System.out.println("Temps d'éxecution = " + ((endTime - startTime)/1000000) + " ms");
		float a = (float) (pcb.totmsg());
		float b = (float)(endTime - startTime)/1000000000;
		System.out.println("Performances = " + (a /b) + " msg/s");

	}
}
