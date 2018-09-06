package com.moglan.eac.connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
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
	
	List<Future<?>> futures = new ArrayList<>();
	ExecutorService clientExecutionPool = Executors.newFixedThreadPool(3);

	/**
	 * Verifies whether a sub-class of TCPServer can successfully implement a global 
	 * counting system of all received messages without any concurrency issues such 
	 * as the simultaneous access of a critical by multiple threads (in this case, the counter).
	 */
	@Test
	void globalCountTest() {
		try {
			/*
			 * Starting dummy server 
			 */
			
			CounterServer server = new CounterServer(Config.PORT);
			
			server.start();
			
			/*
			 * Starting dummy clients
			 */
			
			for (int i = 0; i < MAX_CLIENTS; i++) {
				Runnable runnable = new MeasuringDevice(LOCALHOST, Config.PORT);
				Future<?> future = clientExecutionPool.submit(runnable);
				
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
				assertEquals(server.getNumRecvMessages(), MAX_SENT_PER_DEVICE * MAX_CLIENTS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Dummy server counting the total number of received messages.
	 */
	class CounterServer extends TCPServer {
		
		private long numRecvMessages;	// total number of received messages

		public CounterServer(int port) throws IOException {
			super(port);
			
			numRecvMessages = 0;
		}

		public long getNumRecvMessages() { return this.numRecvMessages; }
		
		/**
		 * Method that is synchronized to prevent simultaneous accent to the 
		 * {@code numRecvMessages} variable.
		 */
		@Override
		protected synchronized Object handleRequest(Object request) {
			if (request != null) {
				numRecvMessages++;
				
				return new String();
			} else {
				return null;
			}
		}

	}
	
	/**
	 * Dummy client class, sending a limited number of messages to the server.
	 */
	class MeasuringDevice extends TCPClient {
		
		private int numSentMessages;
		
		public MeasuringDevice(String addr, int port) throws UnknownHostException, IOException {
			super(addr, port);
			
			numSentMessages = 0;
		}

		@Override
		protected Object createRequest() {
			if (numSentMessages < MAX_SENT_PER_DEVICE) {
				numSentMessages++;
				
				return new String();
			}
			
			else
				return null;
		}

		@Override
		protected void handleReply(Object reply) {
			try {
				Thread.sleep(1000);	// introducing a delay of 1 second between exchanges
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}



