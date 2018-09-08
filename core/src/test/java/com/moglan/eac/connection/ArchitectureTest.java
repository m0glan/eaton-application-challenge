package com.moglan.eac.connection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

public class ArchitectureTest {
	
	private static final String LOCALHOST = "127.0.0.1";
	
	private static final int MAX_SENT_PER_DEVICE = 3;
	private static final int MAX_CLIENTS = 3;

	/**
	 * Verifies whether a sub-class of TCPServer can successfully implement a global 
	 * counting system of all received messages without any concurrency issues such 
	 * as the simultaneous access of a critical by multiple threads (in this case, the counter).
	 */
	@Test
	void globalCountTest() {
		List<Future<?>> futures = new ArrayList<>();
		ExecutorService executionPool = Executors.newFixedThreadPool(MAX_CLIENTS);
		DummyServer server = null;
		
		try {
			/*
			 * Starting dummy server 
			 */
			
			server = new DummyServer(Config.PORT);
			
			server.start();
			
			/*
			 * Starting dummy clients
			 */
			
			for (int i = 0; i < MAX_CLIENTS; i++) {
				Runnable runnable = new DummyClient(i, LOCALHOST, Config.PORT);
				Future<?> future = executionPool.submit(runnable);
				
				futures.add(future);
			}
			
			/*
			 * Launching blocking operation '.get()' in order to waiting for each client to be done
			 */
			
			for (Future<?> future : futures) {
				try {
					future.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			/*
			 * Once all the futures are complete, if the number of messages received is equal to the number 
			 * of sending devices times the number of messages sent by device, the count is correct.
			 */
			
			boolean allDone = true;
			
			for(Future<?> future : futures){
			    allDone &= future.isDone();
			}
			
			if (allDone) {
				assertEquals(MAX_SENT_PER_DEVICE * MAX_CLIENTS, server.getNumRecvMessages());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			executionPool.shutdown();
			
			try {
				server.stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Tests the server shutdown mechanism
	 */
	@Test
	void serverShutdownTest() {
		TCPServer server = null;
		
		try {
			server = new DummyServer(Config.PORT);
			
			server.start();
			
			assertTrue(server.isRunning());
			
			try {
				server.stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			assertFalse(server.isRunning());
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (server != null && server.isRunning()) {
				try {
					server.stop();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Dummy server counting the total number of received messages.
	 */
	class DummyServer extends TCPServer {
		
		private long numRecvMessages;	// total number of received messages

		public DummyServer(int port) throws IOException {
			super(port);
			
			numRecvMessages = 0;
		}

		public long getNumRecvMessages() { return this.numRecvMessages; }

		@Override
		protected void onClientConnect(Socket socket) {
			return;
		}

		@Override
		protected void onClientDisconnect(Socket socket) {
			return;
		}

		/**
		 * Method that is synchronized to prevent simultaneous accent to the 
		 * {@code numRecvMessages} variable.
		 */
		@Override
		protected synchronized Message<?> handleRequest(Message<?> request, int port) {
			if ((request != null) && (request.getProtocol() != Protocol.END_CONNECTION)) {
				numRecvMessages++;
				
				return new Message<>(0, Protocol.DATA_TRANSFER, new String());
			}
			
			return null;
		}

		@Override
		protected void onServerShutdown() {
			return;
		}

		@Override
		protected void onServerStart() {
			LOGGER.info("Server running on port " + getPort() + ".");
		}

	}
	
	/**
	 * Dummy client class, sending a limited number of messages to the server.
	 */
	class DummyClient extends TCPClient {
		
		int id;
		private int numSentMessages;
		
		public DummyClient(int id, String addr, int port) throws UnknownHostException, IOException {
			super(addr, port);
			
			this.id = id;
			numSentMessages = 0;
		}

		@Override
		protected Message<?> createRequest() {
			if (numSentMessages < MAX_SENT_PER_DEVICE) {
				/**
				 * Sending {@code String} objects to the server until maximum number is attained.
				 */
				
				numSentMessages++;
				
				return new Message<>(id, Protocol.DATA_TRANSFER, new String());
			}
			
			else {
				/*
				 * Sending end-connection signal 
				 */
				
				return new Message<>(id, Protocol.END_CONNECTION);
			}
		}

		@Override
		protected void handleResponse(Message<?> reply) {
			try {
				Thread.sleep(1000);	// introducing a delay of 1 second between exchanges
			} catch (InterruptedException e) {
				LOGGER.severe(e.getMessage());
			}
		}

		@Override
		protected void onConnect(Socket socket) {
			return;
		}

		@Override
		protected void onDisconnect() {
			return;
		}
		
	}

}



