package example;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;

/**
 * Singleton that allows simulating an exchange between a number of clients and
 * a server.
 * 
 * @author Vlad-Adrian Moglan
 */
public class Simulation {
	
	private static final int PERIOD = 1500;	// the time between two requests of any client
	
	private static Simulation instance;
	
	private ThreadPoolExecutor clientExecutionPool;
	private List<TCPClient> clientTasks; 
	private int numActiveConnections;
	
	private Simulation() {
		clientExecutionPool = new ThreadPoolExecutor(
			CentralMonitor.get().getMaxNumberOfConnections(), 
			CentralMonitor.get().getMaxNumberOfConnections(), 
			0L, 
			TimeUnit.MILLISECONDS, 
			new LinkedBlockingQueue<>()
		);
		clientTasks = new ArrayList<>();
		numActiveConnections = 0;
	}
	
	public static Simulation get() {
		if (instance == null) {
			instance = new Simulation();
		}
		
		return instance;
	}
	
	public int getNumActiveConnections() { return numActiveConnections; }
	
	/**
	 * Starts the simulation.
	 */
	public void startSimulation() {
		clientTasks.clear();
		CentralMonitor.get().start();
	}
	
	/**
	 * Stops the simulation
	 */
	public void stopSimulation() {
		for (TCPClient clientTask : clientTasks) {
			clientTask.stop();
		}
		
		numActiveConnections = 0;
		
		CentralMonitor.get().stop();
		clientTasks.clear();
	}
	
	/**
	 * Runs a client task / connects a measuring device.
	 * 
	 * @throws UnknownHostException if host cannot be resolved
	 * @throws IOException if socket cannot be opened
	 */
	public void addClientTask() throws UnknownHostException, IOException {
		if (numActiveConnections < CentralMonitor.get().getMaxNumberOfConnections()) {
			MeasuringDevice clientTask = new MeasuringDevice("127.0.0.1", 
					Config.PORT, PERIOD);
			
			clientTasks.add(clientTask);
			clientExecutionPool.submit(clientTask);
			
			numActiveConnections++;
		}
	}
	
	/**
	 * Removes the last client from the task execution list.
	 */
	public void removeClientTask() {
		if (numActiveConnections > 0) {
			TCPClient clientTask = clientTasks.get(clientTasks.size() - 1);
			
			clientTask.stop();
			clientTasks.remove(clientTask);
			
			numActiveConnections--;
		}
	}
	
}
