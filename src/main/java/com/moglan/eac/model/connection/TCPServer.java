package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.moglan.eac.application.Config;

/**
 * A TCP server capable of handling requests from multiple clients at the same time.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPServer {
	
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private ServerTask serverTask;
	private Thread serverThread;
	private ThreadPoolExecutor connectionPool;
	private boolean isRunning;
	private int port;
	private int maximumNumberOfConnections;	// simultaneous connections
	
	/**
	 * @param port is the port on which the server listens for new connections
	 */
	public TCPServer(int port) {
		setPort(port);
		
		maximumNumberOfConnections = ManagementFactory.getThreadMXBean().getThreadCount() + 1;	// number of available threads + 1
		connectionPool = new ThreadPoolExecutor(maximumNumberOfConnections, maximumNumberOfConnections, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * @return the port on which the server is to be run
	 */
	public final int getPort() { return this.port; }
	
	/**
	 * Changes the port on which the server is to be run.
	 * It resets the server.
	 * 
	 * @param port the port on which the server is to be run
	 */
	public final void setPort(int port) {
		this.port = port;
		
		if (isRunning) {
			reset();
		}
	}
	
	/**
	 * @return true if the server is running
	 */
	public final boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * @return the maximum number of simultaneous connections
	 */
	public final int getMaximumNumberOfConnections() { return maximumNumberOfConnections; }
	
	/**
	 * @return the number of active connections
	 */
	public final long getNumberOfActiveConnections() { return connectionPool.getActiveCount(); }
	
	/**
	 * Starts the server.
	 */
	public final void start()  {
		if (!isRunning()) {
			isRunning = true;
			serverTask = new ServerTask(connectionPool);
			serverThread = new Thread(serverTask);
			
			serverThread.start();
			onServerStart();
		}
	}
	
	/**
	 * Stops the server.
	 */
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
	
	/**
	 * Resets the server.
	 */
	public final void reset() {
		stop();
		start();
	}
	
	/**
	 * Determines behavior upon client connection.
	 * 
	 * @param socket used for communicating with the client
	 */
	protected abstract void onClientConnect(Socket socket);
	
	/**
	 * Determines behavior upon end of connection with a client.
	 * 
	 * @param socket used for communicating with the client
	 */
	protected abstract void onClientDisconnect(Socket socket);
	
	/**
	 * Determines behavior at server start.
	 */
	protected abstract void onServerStart();
	
	/**
	 * Determines behavior at server shutdown.
	 */
	protected abstract void onServerShutdown();
	
	/**
	 * Handles the request of a client.
	 * 
	 * @param request message received from the client
	 * @param port the port from which the request comes
	 * @return a response for the client
	 */
	protected abstract Message<?> handleRequest(Message<?> request, int port);
	
	/**
	 * Runs main server operations in a separate thread so that it can be interrupted
	 * asynchronously. 
	 */
	private final class ServerTask implements Runnable {
		
		private ThreadPoolExecutor connectionPool;
		private ServerSocket listener;
		
		public ServerTask(ThreadPoolExecutor connectionPool) {
			this.connectionPool = connectionPool;
		}
		
		/**
		 * Provides the loop used for accepting new connections.
		 */
		public void run() {
			try {
				listener = new ServerSocket(getPort());
				
				while (isRunning()) {
					accept();
				}
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}
		
		/**
		 * Closes the listener and all of the active connections, preventing it
		 * from accepting new ones.
		 * 
		 * @throws IOException if something goes wrong with closing the socket
		 */
		public void stop() throws IOException {
			listener.close();
		}
		
		/**
		 * Handles incoming connections.
		 */
		private void accept() {
			Socket socket;
			
			try {
				socket = listener.accept();
				connectionPool.submit(new ClientTask(socket));
			} catch (IOException e) {
				LOGGER.info("Connection was closed.");
			}
		}
		
	}
	
	/**
	 * Manages the exchanges between the server and one client.
	 */
	private final class ClientTask implements Runnable {
		
		private Socket socket;
		
		public ClientTask(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Provides the exchange loop between the server and one client.
		 */
		public void run() {
			onClientConnect(socket);
			
			try {
				socket.setSoTimeout(Config.SO_TIMEOUT);
				
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message<?> request = null;
				
				do {
					request = (Message<?>) in.readObject();
					
					if (request.getProtocol() != Protocol.END_CONNECTION) {
						Message<?> response = handleRequest(request, socket.getPort());
						out.writeObject(response);
					}
				} while (request.getProtocol() != Protocol.END_CONNECTION);
			} catch (Exception e) {
				LOGGER.info("Connection interrupted abruptly on port " + socket.getPort() + ".");
			} finally {
				onClientDisconnect(socket);
				
				try {
					socket.close();
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
			}
		}
		
	}
	
}
