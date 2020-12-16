package utils;

/* 
 * AllocId permet d'allouer un identifiant unique à chaque message créé
 * Ses champs et ses méthodes sont statiques
 */

public class AllocId {
	/*
	 * VAL = dernier identifiant alloué
	 */
	private static int VAL = 0;
	
	public synchronized static int get() {
		return VAL++;
	}
	
	public static int nbAlloc() {
		return VAL;
	}
}
