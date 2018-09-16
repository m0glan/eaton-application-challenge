package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;

import com.moglan.eac.model.connection.Message;
import com.moglan.eac.model.connection.Protocol;
import com.moglan.eac.model.connection.TCPClient;

/**
 * Simulates a measuring device that constantly sends data to a server.
 * 
 * @author Vlad-Adrian Moglan
 */
public class MeasuringDevice extends TCPClient {
	
	private long id;
    private volatile int period;	// accessed by multiple threads
    
	public MeasuringDevice(String addr, int port, int period) 
			throws UnknownHostException, IOException {
		super(addr, port);
		
		this.id = -1;
        this.period = period;
	}
        
	public void setPeriod(int period) {
		this.period = period;
	}

	@Override
	protected void onConnect() {
		/**
		 * When a client connects to the server, it does not have a valid id and thus,
		 * it requests one.
		 */
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(getSocket().getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(getSocket().getInputStream());
			Message<?> idRequest = new Message<>(id, Protocol.ID_ASSIGNMENT);
			
			oos.writeObject(idRequest);
			
			Object reply = ois.readObject();
			Object data = ((Message<?>) reply).getData();
			
			this.id = (Long) data;
		} catch (Exception e) {
			stop();
			
			LOGGER.severe(e.getMessage());
		}
	}

	@Override
	protected void onDisconnect() {
		return;
	}

	@Override
	protected Message<?> createRequest() {
		/**
		 * When the maximum number of messages to be sent is attained, the 
		 * client sends an end connection request using the handshake method.
		 */
		
		return new Message<String>(this.id, Protocol.DATA_TRANSFER, "Hello.");
	}

	@Override
	protected void handleResponse(Message<?> reply) {
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {
			LOGGER.severe(e.getMessage());
		}
	}
        
    @Override
    public String toString() {
        return Long.toString(id);
    }

}
