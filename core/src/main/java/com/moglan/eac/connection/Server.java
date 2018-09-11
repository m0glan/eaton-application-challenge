package com.moglan.eac.connection;

import java.net.Socket;

/**
 * Abstract class providing the basis for standard server features.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class Server {
	
	private int port;
	
	public Server(int port) {
		this.port = port;
	}
	
	/**
	 * @return the port attributed for running the server
	 */
	public final int getPort() { return this.port; }

	/**
	 * Starts the server
	 */
	public abstract void start();
	
	/**
	 * Stops the server
	 */
	public abstract void stop();

	/**
	 * Method called when a client is connected.
	 * 
	 * @param socket is used for communication with the client
	 */
	protected abstract void onClientConnect(Socket socket);
	
	/**
	 * Method called when a client is disconnected.
	 * 
	 * @param socket is used for communication with the client
	 */
	protected abstract void onClientDisconnect(Socket socket);
	
	/**
	 * Provides a mechanism to perform operations when the server starts.
	 */
	protected abstract void onServerStart();
	
	/**
	 * Provides a mechanism to perform operations when the server shuts down.
	 */
	protected abstract void onServerShutdown();
	
	/**
	 * Method whose implementation defines how a request should be handled.
	 * 
	 * @param request is the object received from the client
	 * @param port is the port from which the request comes
	 * @return a response for the client
	 */
	protected abstract Message<?> handleRequest(Message<?> request, int port);
	
}
