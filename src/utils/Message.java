package utils;

public class Message {
	/*
	 * idProd = id du Producer du message initialisé lors de la création du message
	 * idMess = id du message créé à l'aide de AllocId lors de sa création
	 */
	int idProd;
	int idMess;
	
	public Message (int idProd) {
		this.idProd = idProd;
		this.idMess = AllocId.get();
	}
	
	/*
	 * Traitement du message
	 */
	public void compute() {
		System.out.println("Message " + idMess + " produced by Producer " + idProd);
	}
	
	public int getId() {
		return idMess;
	}
}
