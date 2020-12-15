package utils;

public class Message {
	
	int idProd;
	int idMess;
	
	public Message (int idProd) {
		this.idProd = idProd;
		this.idMess = AllocId.get();
	}
	
	public void compute() {
		System.out.println("Message " + idMess + " produced by Producer " + idProd);
	}
}
