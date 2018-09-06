package com.moglan.eac.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A TCP server capable of handling multiple clients at the same time.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPServer {
	
	private ExecutorService clientTasksPool;
	private int port;
	
	/**
	 * The constructor initializes the port on which the server listens for new connections as well as the
	 * thread pool used for handling multiple connections at the same time.
	 * 
	 * @param port is the port on which the server listens for new connections
	 */
	public TCPServer(int port) {
		int numberOfConcurrentConnections = ManagementFactory.getThreadMXBean().getThreadCount() + 1;
		
		clientTasksPool = Executors.newFixedThreadPool(numberOfConcurrentConnections);
		this.port = port;
	}
	
	/**
	 * Starts the server
	 */
	public final void start()  {
		Runnable serverTask = new Runnable() {	
			public void run() {
				ServerSocket listener;
				
				try {
					listener = new ServerSocket(port);
					
					while (true) {
						Socket socket = listener.accept();
						
						clientTasksPool.submit(new ClientTask(socket));
					}
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		};
		
		Thread serverThread = new Thread(serverTask);
		
		serverThread.start();
	}
	
	/**
	 * Method whose implementation defines how a request should be handled.
	 * 
	 * @param request is the object received from the client
	 * @return a reply for the client
	 */
	protected abstract Object handleRequest(Object request);
	
	/**
	 * A runnable allowing to manage a connection with a client.
	 */
	private class ClientTask implements Runnable {
		
		private Socket socket;
		
		public ClientTask(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				socket.setSoTimeout(Config.SO_TIMEOUT);
				
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Object message;
				
				do {
					/*
					 * If the received message is null, the connection ends gracefully
					 */
					
					message = in.readObject();
					
					Object reply = handleRequest(message);
					
					out.writeObject(reply);
				} while (message != null);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		}

	}
	
}
