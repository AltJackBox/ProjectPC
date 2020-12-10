package utils;

public class Message {
	
	int idProd;
	int idMess;
	
	public Message (int idProd, int idMess) {
		this.idProd = idProd;
		this.idMess = idMess;
	}
	
	public void compute() {
		System.out.println("Message " + idMess + " produced by Producer " + idProd);
	}
}
