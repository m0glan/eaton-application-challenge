package example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;

public class Simulation {

	private static final String LOCALHOST = "127.0.0.1";
	private static final int MAX_SENT_PER_DEVICE = 5;	// maximum number of messages sent per device
	private static final Logger LOGGER = Logger.getLogger("Simulation");
	
	public static void main(String[] args) {
		CentralMonitor centralMonitor = CentralMonitor.get();
		ExecutorService clientExecutionPool 
			= Executors.newFixedThreadPool(centralMonitor.getMaxNumberOfConnections());	// manages the running client instances
		List<Future<?>> futures = new ArrayList<>();	// used for preventing premature server stop
		
		centralMonitor.getLogger().setLevel(Arrays.asList(args).contains("--quiet-exchange") ? Level.OFF : Level.ALL);
		
		try {
			centralMonitor.start();
			
			/**
			 * Launching measuring devices
			 */
			
			for (int i = 0; i < centralMonitor.getMaxNumberOfConnections(); i++) {
				TCPClient measuringDevice = new MeasuringDevice(LOCALHOST, Config.PORT, MAX_SENT_PER_DEVICE);
				Future<?> future = clientExecutionPool.submit(measuringDevice);
				
				futures.add(future);
			}
			
			LOGGER.info("Connected " + centralMonitor.getMaxNumberOfConnections() + " clients, each of them sending "
					+ MAX_SENT_PER_DEVICE + " messages to the server. The total number of messages received by the server"
					+ " should be " + (MAX_SENT_PER_DEVICE * centralMonitor.getMaxNumberOfConnections()) + ".");
			
			for (Future<?> future : futures) {
				/**
				 * waiting for each client to be done
				 */
				
				future.get();	// blocking
			}
			
			LOGGER.fine("Total number of messages received by the server: " + centralMonitor.getNumRecvMessages() + ".");
			
			clientExecutionPool.shutdown();
			centralMonitor.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
