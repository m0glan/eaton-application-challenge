package com.moglan.eac.connection;

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

/**
 * A TCP server capable of handling multiple clients at the same time.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPServer {
	
	protected final Logger LOGGER = Logger.getLogger(getClass().getName());
	
	private ServerTask serverTask;
	private Thread serverThread;
	private ThreadPoolExecutor connectionPool;
	
	private int port;
	private int maxNumberOfConnections;	// simultaneous connections
	
	/**
	 * The constructor initializes the port on which the server listens for new connections as well as the
	 * thread pool used for handling multiple connections at the same time.
	 * 
	 * @param port is the port on which the server listens for new connections
	 */
	public TCPServer(int port) {
		maxNumberOfConnections = ManagementFactory.getThreadMXBean().getThreadCount() + 1;
		
		connectionPool = new ThreadPoolExecutor(maxNumberOfConnections, maxNumberOfConnections, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		
		this.port = port;
	}
	
	/**
	 * @return the port attributed for running the server
	 */
	public final int getPort() { return this.port; }
	
	/**
	 * @return the maximum number of simultaneous connections
	 */
	public final int getMaxNumberOfConnections() { return maxNumberOfConnections; }
	
	/**
	 * @return the number of active connections
	 */
	public final long getNumberOfConnections() { return connectionPool.getPoolSize(); }
	
	/**
	 * @return true if the server is running
	 */
	public final boolean isRunning() {
		return serverThread != null && serverThread.isAlive() && serverTask != null && serverTask.isRunning();
	}
	
	/**
	 * Starts the server
	 */
	public final void start()  {
		if (!isRunning()) {
			serverTask = new ServerTask(connectionPool);
			serverThread = new Thread(serverTask);
			
			serverThread.start();
		}
	}
	
	/**
	 * Stops the server
	 * 
	 * @throws InterruptedException if joining the thread goes wrong
	 */
	public final void stop() throws InterruptedException {
		if (isRunning()) {
			connectionPool.shutdown();
			serverTask.stop();
			serverThread.join();
		}
	}
	
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
	
	/**
	 * A runnable allowing to manage a connection with a client.
	 */
	private final class ClientTask implements Runnable {
		
		private Socket socket;
		
		public ClientTask(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				socket.setSoTimeout(Config.SO_TIMEOUT);
				
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object request;
				
				do {
					request = in.readObject();
				} while (keepAlive(request, out));
			} catch (Exception e) {
				LOGGER.severe(e.getMessage());
			} finally {
				onClientDisconnect(socket);
				
				try {
					socket.close();
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
				}
			}
		}
		
		/**
		 * @param request is the message received from the client
		 * @param out is the socket's output stream
		 * @return true as long as the connection is to be kept alive
		 */
		private boolean keepAlive(Object request, ObjectOutputStream out) {
			if (request instanceof Message) {
				Message<?> response; 
				
				if (((Message<?>) request).getProtocol() == Protocol.END_CONNECTION) {
					return false;
				} else {
					response = handleRequest((Message<?>) request, socket.getPort());
					
					try {
						out.writeObject(response);
					} catch (IOException e) {
						LOGGER.severe(e.getMessage());
					}
				}
			} else {
				/*
				 * Invalid object type
				 */
				
				return false;
			}
			
			return true;
		}

	}
	
	/**
	 * Runs main server operations
	 */
	private final class ServerTask implements Runnable {
		
		private ThreadPoolExecutor connectionPool;
		private ServerSocket listener;
		private boolean running;
		
		
		public ServerTask(ThreadPoolExecutor connectionPool) {
			running = true;
			this.connectionPool = connectionPool;
		}
		
		public boolean isRunning() { return running; }
		
		/**
		 * Main server task loop.
		 */
		public void run() {
			try {
				listener = new ServerSocket(port);
				
				onServerStart();
				
				while (running) {
					accept();
				}
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
		}
		
		/**
		 * Stops the server task.
		 */
		public void stop() {
			try {
				listener.close();
			} catch (IOException e) {
				LOGGER.severe(e.getMessage());
			}
			
			running = false;
			onServerShutdown();
		}
		
		/**
		 * Handles incoming connections.
		 */
		private void accept() {
			Socket socket;
			
			try {
				socket = listener.accept();
				onClientConnect(socket);
				connectionPool.submit(new ClientTask(socket));
			} catch (IOException e) {
				LOGGER.info("Connection was closed.");
			}
		}
		
	}
	
}
