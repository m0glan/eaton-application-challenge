package com.moglan.eac.application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.moglan.eac.model.connection.Config;
import com.moglan.eac.model.connection.Monitor;
import com.moglan.eac.model.connection.MeasuringDevice;
import com.moglan.eac.model.connection.TCPClient;

/**
 * Singleton that allows monitoring the exchange between a number of clients and
 * a server. It acts as a controller for the application, notifying the view whenever
 * the model changes.
 * 
 * @author Vlad-Adrian Moglan
 */
public class Simulation extends Observable {
	
	private static final int PERIOD = 1500;	// the time between two requests of any client
	
	private static Simulation instance;
	
	private ThreadPoolExecutor clientExecutionPool;
	private List<TCPClient> clientTasks; 
	private long messageCount;
	private boolean isRunning;
	
	private Simulation() {
		clientExecutionPool = new ThreadPoolExecutor(
			getServerMaximumNumberOfConnections(),
			getServerMaximumNumberOfConnections(),
			0L, 
			TimeUnit.MILLISECONDS, 
			new LinkedBlockingQueue<>()
		);
		
		clientTasks = new ArrayList<>();
		messageCount = 0;
		isRunning = false;
	}
	
	public static Simulation get() {
		if (instance == null) {
			instance = new Simulation();
		}
		
		return instance;
	}
	
	public int getServerPort() { return Monitor.get().getPort(); }
	
	public int getServerActiveCount() { return clientTasks.size(); }
	
	public int getServerMaximumNumberOfConnections() { return Monitor.get().getMaxNumberOfConnections(); }
	
	public long getServerMessageCount() { return messageCount; }
	
	public boolean isRunning() { return isRunning; }
	
	public void messageCountIncrement() {
		messageCount++;
		
		onChange();
	}
	
	/**
	 * Starts the simulation.
	 */
	public void startSimulation() {
		if (!isRunning) {
			isRunning = true;
			
			Monitor.get().start();
			onChange();
		}
	}
	
	/**
	 * Stops the simulation
	 */
	public void stopSimulation() {
		if (isRunning) {
			isRunning = false;
			messageCount = 0;
			
			for (TCPClient clientTask : clientTasks) {
				clientTask.stop();
			}
			
			Monitor.get().stop();
			clientTasks.clear();
			onChange();
		}
	}
	
	/**
	 * Runs a client task / connects a measuring device.
	 * 
	 * @throws UnknownHostException if host cannot be resolved
	 * @throws IOException if socket cannot be opened
	 */
	public void addClientTask() throws UnknownHostException, IOException {
		if (clientTasks.size() < Monitor.get().getMaxNumberOfConnections()) {
			MeasuringDevice clientTask = new MeasuringDevice("127.0.0.1", 
					Config.PORT, PERIOD);
			
			clientTasks.add(clientTask);
			clientExecutionPool.submit(clientTask);
			onChange();
		}
	}
	
	/**
	 * Removes the last client from the task execution list.
	 */
	public void removeClientTask() {
		if (clientTasks.size() > 0) {
			TCPClient clientTask = clientTasks.get(clientTasks.size() - 1);
			
			clientTask.stop();
			clientTasks.remove(clientTask);
			
			onChange();
		}
	}
	
	/**
	 * Sets the {@code hasChanged} variable of the {@code Observable} class to true and
	 * notifies observers;
	 */
	private void onChange() {
		setChanged();
		notifyObservers();
	}
	
}
