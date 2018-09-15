package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * A TCP client that uses a handshake protocol for exchanging data with a server and ending
 * its connection with it.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPClient implements Runnable {

	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private Socket socket;
	private boolean isRunning;
	
	public TCPClient(String addr, int port) throws UnknownHostException, IOException {
		socket = new Socket(addr, port);
		isRunning = false;
	}
	
	/**
	 * @return true if client is running
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	protected Socket getSocket() {
		return socket;
	}
	
	/**
	 * Provides the connection loop.
	 */
	@Override
	public final void run() {
		isRunning = true;
		
		try {
			onConnect();
			
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Message<?> request = null;
			
			do {
				request = createRequest();
				oos.writeUnshared(request);
				
				if (request.getProtocol() != Protocol.END_CONNECTION) {
					Object response = ois.readObject();
					
					if (response instanceof Message) {
						handleResponse((Message<?>) response);
					} else {
						throw new IllegalArgumentException("Invalid server response type.");
					}
				}
			} while (isRunning && request.getProtocol() != Protocol.END_CONNECTION);
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
		} finally {
			onDisconnect();
		}
	}
	
	/**
	 * Requests client to exit the connection loop; this ends the connection abruptly.
	 */
	public void stop() {
		isRunning = false;
		
		try {
			socket.close();
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	/**
	 * Determines behavior upon connection.
	 * 
	 * @param socket used for communicating with the server.
	 */
	protected abstract void onConnect();
	
	/**
	 * Determines behavior upon end of connection.
	 */
	protected abstract void onDisconnect();
	
	/**
	 * @return a server request; {@see com.moglan.eac.model.connection.Protocol}
	 */
	protected abstract Message<?> createRequest();
	
	/**
	 * Handles the response received from the server.
	 * 
	 * @param response the message received from the server
	 */
	protected abstract void handleResponse(Message<?> response);
	
}
