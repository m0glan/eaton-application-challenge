package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.moglan.eac.application.Config;

/**
 * Used for handling exchanges on one connection in a separate thread.
 * 
 * @author Vlad-Adrian Moglan
 */
class TCPClientTask implements Runnable {

	private final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private Socket socket;
	private TCPServer parent;
	
	public TCPClientTask(TCPServer parent, Socket socket) {
		this.socket = socket;
		this.parent = parent;
	}

	public void run() {
		parent.onClientConnect(socket);
		
		try {
			socket.setSoTimeout(Config.SO_TIMEOUT);
			
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			Message<?> request = null;
			
			do {
				request = (Message<?>) in.readObject();
				
				if (request.getProtocol() != Protocol.END_CONNECTION) {
					Message<?> response = parent.handleRequest(request, socket.getPort());
					out.writeObject(response);
				}
			} while (request.getProtocol() != Protocol.END_CONNECTION);
		} catch (Exception e) {
			LOGGER.info("Connection interrupted abruptly on port " + socket.getPort() + ".");
		} finally {
			parent.onClientDisconnect(socket);
			
			try {
				socket.close();
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}
	}
	
}
