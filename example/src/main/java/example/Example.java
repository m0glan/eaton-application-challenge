package example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;
import com.moglan.eac.connection.TCPServer;

public class Example {

	private static final String LOCALHOST = "127.0.0.1";
	
	private static final int MAX_SENT_PER_DEVICE = 5;	// maximum number of messages sent per device
	
	public static void main(String[] args) {
		TCPServer server = new CentralMonitor(Config.PORT);
		ExecutorService clientExecutionPool 
			= Executors.newFixedThreadPool(server.getMaxNumberOfConnections());	// manages the running client instances
		List<Future<?>> futures = new ArrayList<>();	// used for preventing premature server stop
		
		try {
			server.start();
			
			for (int i = 0; i < server.getMaxNumberOfConnections(); i++) {
				TCPClient client = new MeasuringDevice(LOCALHOST, Config.PORT, MAX_SENT_PER_DEVICE);
				Future<?> future = clientExecutionPool.submit(client);
				
				futures.add(future);
			}
			
			for (Future<?> future : futures) {
				/**
				 * waiting for each client to be done
				 */
				
				future.get();	// blocking
			}
			
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
