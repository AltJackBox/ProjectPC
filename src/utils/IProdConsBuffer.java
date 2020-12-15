package utils;

public interface IProdConsBuffer {
	/**
	* Put the message m in the prodcons buffer
	**/
	public void put(Message m) throws InterruptedException;
	/**
	* Retrieve a message from the prodcons buffer,
	* following a FIFO order (if M1 was put before M2, M&
	* will be get before M2)
	**/
	public Message get() throws InterruptedException;
	/**
	* Returns the number of messages currently available in
	* the prod-cons buffer
	**/
	
	/**
	* Retrieve n consecutive messages from the prodcons buffer
	**/
	public Message[] get(int k) throws InterruptedException;
	
	public int nmsg();
	/**
	* Returns the total number of messages that have
	* been put in the buffer since its creation
	**/
	public int totmsg();
}
