package com.moglan.eac.application;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
	
	private static Simulation instance;
	
	private static final int BASE_PERIOD = 6000;	// in ms
	private final int MIN_SENDING_FREQ = 1;
	private final int MAX_SENDING_FREQ = 4;
	
	private ThreadPoolExecutor clientExecutionPool;
	private List<MeasuringDevice> clientTasks; 
	private long messageCount;
	private boolean isRunning;
	
	private Simulation() {
		this.clientExecutionPool = new ThreadPoolExecutor(
			getServerMaximumNumberOfConnections(),
			getServerMaximumNumberOfConnections(),
			0L, 
			TimeUnit.MILLISECONDS, 
			new LinkedBlockingQueue<>()
		);
		
		this.clientTasks = new ArrayList<>();
		this.messageCount = 0;
		this.isRunning = false;
	}
	
	public static Simulation get() {
		if (instance == null) {
			instance = new Simulation();
		}
		
		return instance;
	}
	
	public boolean isRunning() { return isRunning; }
	
	public int getServerPort() { return Monitor.get().getPort(); }
	
	public int getServerActiveCount() { return clientTasks.size(); }
	
	public int getServerMaximumNumberOfConnections() { return Monitor.get().getMaximumNumberOfConnections(); }
	
	public long getServerMessageCount() { return messageCount; }
	
	public void messageCountIncrement() {
		messageCount++;
		
		onChange();
	}
	
	public int getMinimumSendingFrequency() { return MIN_SENDING_FREQ; }
	
	public int getMaximumSendingFrequency() { return MAX_SENDING_FREQ; }
	
	public void setMessageSendingFrequency(int frequency) {
		for (MeasuringDevice clientTask : clientTasks) {
			clientTask.setPeriod(BASE_PERIOD/frequency);
		}
		
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
	 * @param sendingFrequency is the frequency at which the clients send messages to the server
	 * @throws UnknownHostException if host cannot be resolved
	 * @throws IOException if socket cannot be opened
	 */
	public void addClientTask(int sendingFrequency) throws UnknownHostException, IOException {
		if (clientTasks.size() < Monitor.get().getMaximumNumberOfConnections()) {
			MeasuringDevice clientTask = new MeasuringDevice("127.0.0.1", 
					Config.PORT, BASE_PERIOD/sendingFrequency);
			
			clientTasks.add(clientTask);
			clientExecutionPool.submit(clientTask);
			onChange();
		}
	}
	
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
	 * notifies observers.
	 */
	private void onChange() {
		setChanged();
		notifyObservers();
	}
	
}
