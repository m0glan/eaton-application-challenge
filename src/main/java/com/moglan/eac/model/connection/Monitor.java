package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import com.moglan.eac.application.Simulation;

import java.util.logging.Level;

/**
 * Singleton that monitors multiple measuring devices and counts the total
 * number of messages received.
 * 
 * @author Vlad-Adrian Moglan
 */
public class Monitor extends TCPMultiServer {
	
	private static Monitor instance = null;
        
	private final Map<Integer, Long> portAllocation; 	// stores what client is connected on what port
        
	private volatile int messageCount;	// the total number of received messages throughout the server's lifespan

	private Monitor() {
		super(Config.PORT);
		
		messageCount = 0;
		portAllocation = new HashMap<>();
                
        LOGGER.setLevel(Level.ALL);
	}
	
	/**
	 * @return the unique instance of the class
	 */
	public static Monitor get() {
		if (instance == null) {
			instance = new Monitor();
		}
		
		return instance;
	}
	
	public Logger getLogger() { return LOGGER; }
	
	public int getMessageCount() { return messageCount; }

	@Override
	protected void onClientConnect(Socket socket) {
		/**
		 * When the client connects, it does not have an ID so it requests
		 * the server to provide it with one. This method handles that first,
		 * special request.
		 */
		
		LOGGER.log(Level.INFO, "New connection on port {0}.", socket.getPort());
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Object request = ois.readObject();
			
			if (!(request instanceof Message)) {
				throw new IllegalArgumentException("Request is not of type message.");
			}
			
			if (((Message<?>) request).getProtocol() == Protocol.ID_ASSIGNMENT) {
				/**
				 * Generating and sending the client a message containing a unique id that 
				 * comes under the form of a long integer; the bit operation {@code &} ensures 
				 * that the generated number is not negative, since {@code Long.MAX_VALUE} has 
				 * its strongest bit set at 0.
				 */
				
				Long id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
				Message<Long> reply = new Message<>(0, Protocol.ID_ASSIGNMENT, id);
				
				oos.writeObject(reply);
				portAllocation.put(socket.getPort(), id);
				
				LOGGER.info("Attributed id " + id + " to the client on port " + socket.getPort() + ".");
			}
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				LOGGER.severe(e1.getMessage());
			}
			
			LOGGER.severe(e.getMessage());
		}
	}

	@Override
	protected void onClientDisconnect(Socket socket) {
		int localPort = socket.getPort();
		
		LOGGER.info("Client " + portAllocation.get(localPort) + " on port " + localPort + " has disconnected.");
		
		portAllocation.remove(localPort);
	}
	
	@Override
	protected void onServerStart() {
		LOGGER.info("Server running on port " + getPort() + ".");
	}

	@Override
	protected synchronized void onServerShutdown() {
		messageCount = 0;
		
		LOGGER.info("Server is shutting down...");
	}

	@Override
	protected synchronized Message<?> handleRequest(Message<?> request, int port) {
		/**
		 * Incrementing the number of messages upon receive. The method is synchronized so that
		 * it can be called only once at a time; this prevents the {@code numRecvMessages} variable from being accessed
		 * simultaneously.
		 */
		
		messageCount++;
		
		Simulation.get().messageCountIncrement();
		
		LOGGER.info("Received \"" + request.getData() + "\" from client " 
                        + request.getSenderID() + " on port " + port + ".");
		
		return new Message<>(0, Protocol.DATA_TRANSFER, "Well received.");
	}

}
