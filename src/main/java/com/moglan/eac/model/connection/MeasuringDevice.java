package com.moglan.eac.model.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.moglan.eac.model.connection.Message;
import com.moglan.eac.model.connection.Protocol;
import com.moglan.eac.model.connection.TCPClient;

public class MeasuringDevice extends TCPClient {
	
	private long id;
    private boolean isRequestedToStop;
    private int period;
    
    /**
     * @param addr is the host address
     * @param port is the port on which the hosts accepts connections
     * @param period is the time in milliseconds between two requests sent by the client
     * @throws UnknownHostException if the host address cannot be resolved
     * @throws IOException if the socket cannot be opened
     */
	public MeasuringDevice(String addr, int port, int period) throws UnknownHostException, IOException {
		super(addr, port);
		
		id = -1;
        isRequestedToStop = false;
        this.period = period;
	}
        
    public void stop() { isRequestedToStop = true; }

	@Override
	protected void onConnect(Socket socket) {
		/**
		 * When a client connects to the server, it does not have a valid id and thus,
		 * it requests one.
		 */
		
		try {
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			Message<?> idRequest = new Message<>(id, Protocol.ID_ASSIGNMENT);
			
			oos.writeObject(idRequest);
			
			Object reply = ois.readObject();
			
			if (!(reply instanceof Message)) {
				throw new IllegalArgumentException("Invalid reply type.");
			}
			
			Object data = ((Message<?>) reply).getData();
			
			if (!(data instanceof Long)) {
				throw new IllegalArgumentException("Invalid reply type.");
			}
			
			this.id = (Long) data;
		} catch (Exception e) {
			try {
				socket.close();
			} catch (IOException e1) {
				LOGGER.severe(e1.getMessage());
			}
			
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
		
		if (!isRequestedToStop) {
			return new Message<String>(this.id, Protocol.DATA_TRANSFER, "Hello.");
		}
		
		return new Message<>(this.id, Protocol.END_CONNECTION);
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
