package com.moglan.eac.model.connection;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * A TCP server capable of handling requests from multiple clients at the same time.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPServer {
	
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private TCPServerTask serverTask;
	private Thread serverThread;
	private int port;
	private boolean isRunning;
	private int maximumNumberOfConnections;
	
	public TCPServer(int port) {
		this.port = port;
		this.maximumNumberOfConnections = ManagementFactory.getThreadMXBean()
				.getThreadCount() + 1;	// number of available threads + 1
	}
	
	public final int getPort() { return port; }
	
	/**
	 * Changes the port and resets the server.
	 * 
	 * @param port the port on which the server is to be run
	 */
	public final void setPort(int port) {
		this.port = port;
		
		if (isRunning) {
			reset();
		}
	}
	
	public final boolean isRunning() {
		return isRunning;
	}
	
	public final int getMaximumNumberOfConnections() { return maximumNumberOfConnections; }
	
	public final long getNumberOfActiveConnections() { return serverTask.getNumberOfActiveConnections(); }
	
	public final void start()  {
		if (!isRunning()) {
			isRunning = true;
			serverTask = new TCPServerTask(this);
			serverThread = new Thread(serverTask);
			
			serverThread.start();
			onServerStart();
		}
	}
	
	public final void stop() {
		if (isRunning()) {
			isRunning = false;
			
			try {
				serverTask.stop();
				
				try {
					serverThread.join();
				} catch (InterruptedException e) {
					LOGGER.severe(e.getMessage());
				}
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
			
			onServerShutdown();
		}
	}
	
	public final void reset() {
		stop();
		start();
	}

	protected abstract void onClientConnect(Socket socket);
	
	protected abstract void onClientDisconnect(Socket socket);
	
	protected abstract void onServerStart();
	
	protected abstract void onServerShutdown();
	
	/**
	 * @param request the message received from the client
	 * @param port the port from which the request comes
	 * @return a response for the client
	 */
	protected abstract Message<?> handleRequest(Message<?> request, int port);
	
}
