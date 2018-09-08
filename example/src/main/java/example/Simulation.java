package example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;
import com.moglan.eac.connection.TCPMultiServer;

public class Simulation {

	private static final String LOCALHOST = "127.0.0.1";
	private static final int MAX_SENT_PER_DEVICE = 5;	// maximum number of messages sent per device
	private static final Logger LOGGER = Logger.getLogger("Simulation");
	
	public static void main(String[] args) {
		TCPMultiServer server = new CentralMonitor(Config.PORT);
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
			
			LOGGER.info("Connected " + server.getMaxNumberOfConnections() + " clients, each of them sending "
					+ MAX_SENT_PER_DEVICE + " messages to the server. The total number of messages received by the server"
					+ " should be " + (MAX_SENT_PER_DEVICE * server.getMaxNumberOfConnections()) + ".");
			
			for (Future<?> future : futures) {
				/**
				 * waiting for each client to be done
				 */
				
				future.get();	// blocking
			}
			
			clientExecutionPool.shutdown();
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
