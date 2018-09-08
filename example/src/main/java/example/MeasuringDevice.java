package example;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import com.moglan.eac.connection.Message;
import com.moglan.eac.connection.Protocol;
import com.moglan.eac.connection.TCPClient;

public class MeasuringDevice extends TCPClient {
	
	private long id;
	private int numMessagesSent;
	private int maxNumMessagesToSend;	// the maximum number of message that the client should send

	public MeasuringDevice(String addr, int port, int maxNumMessagesToSend) throws UnknownHostException, IOException {
		super(addr, port);
		
		id = -1;
		numMessagesSent = 0;
		this.maxNumMessagesToSend = maxNumMessagesToSend;
	}

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
		
		if (numMessagesSent < maxNumMessagesToSend) {
			numMessagesSent++;
			
			return new Message<String>(this.id, Protocol.DATA_TRANSFER, new String("Message no. " + numMessagesSent + "."));
		}
		
		return new Message<>(this.id, Protocol.END_CONNECTION);
	}

	@Override
	protected void handleResponse(Message<?> reply) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.severe(e.getMessage());
		}
	}

}
