package example;

import java.util.Observable;

/**
 * Observable element used for notifying GUI components
 * when the message count is updated.
 * 
 * @author Vlad-Adrian Moglan
 */
public class Controller extends Observable {
	
	private static Controller instance = null;
	
	private Controller() { }
	
	public static Controller get() {
		if (instance == null) {
			instance = new Controller();
		}
		
		return instance;
	}
	
	/**
	 * Called when model changed.
	 */
	public void modelChanged() {
		setChanged();
		notifyObservers();
	}
	
}
