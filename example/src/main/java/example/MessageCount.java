package example;

import java.util.Observable;

/**
 * Observable element used for notifying GUI components
 * when the message count is updated.
 * 
 * @author Vlad-Adrian Moglan
 */
public class MessageCount extends Observable {
	
	private static MessageCount instance = null;
	
	private volatile int count;
	
	private MessageCount() { count = 0; }
	
	public static MessageCount get() {
		if (instance == null) {
			instance = new MessageCount();
		}
		
		return instance;
	}
	
	public int getCount() { return count; }
	
	/**
	 * Increments the count variable and notifies all observers.
	 */
	public synchronized void increment() {
		count++;
		setChanged();
		notifyObservers();
	}
	
}
