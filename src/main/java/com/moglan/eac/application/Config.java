package com.moglan.eac.application;

/**
 * Contains different configuration elements, such as the port used for the application,
 * the inactivity time out and so on.
 * 
 * @author Vlad-Adrian Moglan
 */
public class Config {
	
	public static final int APP_PORT = 9091;	// used for ensuring that only one instance of the app runs at a time
	public static final int SERVER_PORT = 9090;
	public static final int SO_TIMEOUT = 50000;
	
	private Config() { }
	
}
