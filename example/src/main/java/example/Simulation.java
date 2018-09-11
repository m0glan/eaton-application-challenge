package example;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi.Attribute;
import com.diogonunes.jcdp.color.api.Ansi.BColor;
import com.diogonunes.jcdp.color.api.Ansi.FColor;
import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;

public class Simulation {
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		boolean quietExchange = Arrays.asList(args).contains("--quiet-exchange");
		int numMessagesPerDevice;
		
		ColoredPrinter printer = new ColoredPrinter.Builder(1, false).build();
        
		try {
			printer.println(AppProperties.TITLE + System.lineSeparator(), Attribute.BOLD, FColor.CYAN, BColor.NONE);
			printer.println(AppProperties.BRIEF + System.lineSeparator(), Attribute.NONE, FColor.CYAN, BColor.NONE);
			printer.println("Enter the number of messages to be sent per measuring device: ", Attribute.NONE, FColor.MAGENTA, BColor.NONE);
			
			numMessagesPerDevice = scanner.nextInt();
			scanner.close();
			
			printer.println("Starting simulation with " + CentralMonitor.get().getMaxNumberOfConnections() + " measuring devices "
					+ "and " + numMessagesPerDevice + " number of messages sent per device...", Attribute.NONE, FColor.CYAN, BColor.NONE);
			
			printer.clear();
			
			int messageCount = simulate(numMessagesPerDevice, quietExchange);
			
			printer.println("Simulation has ended. Total number of messages received from the measuring devices: " +  
					messageCount + ".", Attribute.NONE, FColor.GREEN, BColor.NONE);
		} catch (Exception e) {
			printer.errorPrintln(e.getMessage());
		}
	}
	
	/**
	 * Simulates a server that counts the total number of messages that it receives.
	 * 
	 * @param numMessagesPerDevice is the total number of messages each device will send during the simulation
	 * @param quietExchange is true if server logs are not to be shown
	 * @return the total number of messages received by the server
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static int simulate(int numMessagesPerDevice, boolean quietExchange) 
			throws InterruptedException, ExecutionException, UnknownHostException, IOException {
		CentralMonitor centralMonitor = CentralMonitor.get();
		ExecutorService clientExecutionPool 
			= Executors.newFixedThreadPool(centralMonitor.getMaxNumberOfConnections());	// manages the running client instances
		List<Future<?>> futures = new ArrayList<>();	// used for preventing premature server stop
		
		centralMonitor.getLogger().setLevel(quietExchange ? Level.OFF : Level.ALL);
		
		centralMonitor.start();
		
		/**
		 * Launching measuring devices
		 */
		
		for (int i = 0; i < centralMonitor.getMaxNumberOfConnections(); i++) {
			TCPClient measuringDevice = new MeasuringDevice("127.0.0.1", Config.PORT, numMessagesPerDevice);
			Future<?> future = clientExecutionPool.submit(measuringDevice);
			
			futures.add(future);
		}
		
		for (Future<?> future : futures) {
			/**
			 * waiting for each client to be done
			 */
			
			future.get();	// blocking
		}
		
		clientExecutionPool.shutdown();
		centralMonitor.stop();
		
		return centralMonitor.getNumRecvMessages();
	}

}
