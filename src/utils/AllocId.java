package utils;

public class AllocId {
	
	private static int VAL = 0;
	
	public synchronized static int get() {
		return VAL++;
	}
	
	public static int nbAlloc() {
		return VAL;
	}
}
