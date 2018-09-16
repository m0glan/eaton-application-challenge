package com.moglan.eac.model.connection;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Allows launching the server operations on a separate thread so that
 * they can be stopped asynchronously from the main thread.
 * 
 * @author Vlad-Adrian Moglan
 */
class TCPServerTask implements Runnable {

	private final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private TCPServer parent;
	private ServerSocket listener;
	private ThreadPoolExecutor connectionPool;
	private boolean isRunning;
	
	public TCPServerTask(TCPServer parent) {
		this.parent = parent;
		this.connectionPool = new ThreadPoolExecutor(
														parent.getMaximumNumberOfConnections(), 
														parent.getMaximumNumberOfConnections(), 
														0L,
														TimeUnit.MILLISECONDS, 
														new LinkedBlockingQueue<Runnable>()
													);
	}
	
	public final long getNumberOfActiveConnections() { return connectionPool.getActiveCount(); }
	
	public void run() {
		isRunning = true;
		
		try {
			this.listener = new ServerSocket(parent.getPort());
			
			while (isRunning) {
				accept();
			}
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	public void stop() throws IOException {
		isRunning = false;
		listener.close();
	}
	
	private void accept() {
		Socket socket;
		
		try {
			socket = listener.accept();
			
			connectionPool.submit(new TCPClientTask(parent, socket));
		} catch (IOException e) {
			LOGGER.info("Connection was closed.");
		}
	}
	
}
