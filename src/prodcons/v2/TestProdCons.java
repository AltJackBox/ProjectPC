package prodcons.v2;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

import prodcons.v2.Consumer;
import prodcons.v2.ProdConsBuffer;
import prodcons.v2.Producer;
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
		 * Initialisation des producers et consumers
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
		 * on lance les thread aléatoirement
		 */
		int startCons = 0; //Nombre de consumers lancés
		int startProd = 0; //Nombre de producers lancés
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
		 * on attend que les producers aient créé tous les messages
		 */
		for (int i = 0; i < nProd; i++) {
			tabProd[i].join();
		}
		/*
		 * tant qu'il reste des messages à traiter dans le buffer, on attend que les messages 
		 * soient consommés		 
		 */
		while (pcb.nmsg() > 0) {
			Thread.sleep(10);
		}
		/*
		 * Lorsque le dernier message est lu dans le buffer, on attend que le dernier consummer qui 
		 * l'ai lu finisse le traitement du message			 
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
		 * Affichage final avec les perfomances
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
