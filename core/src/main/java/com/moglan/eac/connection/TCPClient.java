package com.moglan.eac.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * Basic implementation of a TCP client that keeps socket connection open
 * until indicated otherwise.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPClient extends Client implements Runnable {

	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private volatile boolean isRunning = true;
	
	public TCPClient(String addr, int port) throws UnknownHostException, IOException {
		super(addr, port);
	}
	
	/**
	 * Runs the client which maintains the connection open until it is closed gracefully
	 * or interrupted.
	 */
	@Override
	public final void run() {
		Socket socket;
		
		try {
			socket = new Socket(getAddr(), getPort());
			
			onConnect(socket);
			
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				Message<?> request = null;
				
				do {
					request = createRequest();
					oos.writeUnshared(request);
				} while (keepAlive(ois, oos, request) && isRunning);
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			} finally {
				onDisconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		this.isRunning = false;
	}
	
	/**
	 * @param ois is the socket input stream
	 * @param oos is the socket output stream
	 * @param request is the request generated by the client
	 * @return true if the connection is to be maintained
	 * @throws IOException if a socket error occurs
	 * @throws ClassNotFoundException if Java cannot establish the class of the received object
	 */
	private final boolean keepAlive(ObjectInputStream ois, ObjectOutputStream oos, Message<?> request) 
			throws IOException, ClassNotFoundException {
		Object response = null;
		
		if (request.getProtocol() != Protocol.END_CONNECTION) {
			response = ois.readObject();
			
			if (response instanceof Message) {
				handleResponse((Message<?>) response);
			} else {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
	
}
