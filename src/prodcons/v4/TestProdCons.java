package prodcons.v4;

import java.io.FileInputStream;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

import prodcons.v4.Consumer;
import prodcons.v4.ProdConsBuffer;
import prodcons.v4.Producer;

public class TestProdCons {

	public static void main(String[] args)
			throws InvalidPropertiesFormatException, FileNotFoundException, IOException, InterruptedException {
		/*
		 * on lit les options dans le fichier options.V4.xml
		 */
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("options.v4.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));
		
		/*
		 * Initialisation des producers et des consumers
		 */
		Consumer[] tabCons = new Consumer[nCons];
		Producer[] tabProd = new Producer[nProd];

		ProdConsBuffer pcb = new ProdConsBuffer(bufSz);
		for (int i = 0; i < nCons; i++) {
			tabCons[i] = new Consumer(pcb, consTime);
		}
		for (int i = 0; i < nProd; i++) {
			tabProd[i] = new Producer(pcb, minProd, maxProd, prodTime, i, nCons);
		}
		/*
		 * on lance les thread aléatoirement
		 */
		
		int startCons = 0; //Nombre de consumers lancés
		int startProd = 0; //Nombre de producers lancés
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
		/*
		 * on arrête les consummers
		 */
		for (int i = 0; i < nCons; i++) {
			tabCons[i].interrupt();
		}
		System.out.println("Tous les messages ont été transmis");
	}
}
