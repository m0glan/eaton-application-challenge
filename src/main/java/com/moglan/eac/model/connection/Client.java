package com.moglan.eac.model.connection;

import java.net.Socket;

/**
 * Abstract class providing the basis for standard client features.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class Client {
	
	private String addr;
	private int port;
	
	/**
	 * @param addr is the address of the remote server
	 * @param port is the port through which to access the server
	 */
	public Client(String addr, int port) {
		this.addr = addr;
		this.port = port;
	}
	
	public String getAddr() { return addr; }
	
	public int getPort() { return port; }
	
	/**
	 * Method called when the connection is established.
	 * 
	 * @param socket is used for communicating with the server.
	 */
	protected abstract void onConnect(Socket socket);
	
	/**
	 * Method called when the connection is ended. 
	 */
	protected abstract void onDisconnect();
	
	/**
	 * Creates a request directed at the server and allows the implementation of a connection interruption 
	 * mechanism.
	 * 
	 * @return a server request
	 */
	protected abstract Message<?> createRequest();
	
	/**
	 * Handles the response received from the server after a request.
	 * 
	 * @param response is the message received from the server
	 */
	protected abstract void handleResponse(Message<?> response);
	
}
