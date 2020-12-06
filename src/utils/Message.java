package utils;

public class Message {
	
	int id;
	
	public Message (int id) {
		this.id = id;
	}
	
	public void compute() {
		System.out.println("Message : id = " + id);
	}
}
