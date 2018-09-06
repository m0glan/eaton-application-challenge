package com.moglan.eac.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Basic implementation of a TCP client that keeps socket connection open
 * until indicated otherwise.
 * 
 * @author Vlad-Adrian Moglan
 */
public abstract class TCPClient implements Runnable {

	Socket socket;
	
	/**
	 * The constructor opens a socket with a given address and a given port.
	 * 
	 * @param addr is the address of the remote server
	 * @param port is the port through which to access the server
	 * @throws UnknownHostException if the host cannot be found
	 * @throws IOException if the socket fails to open
	 */
	public TCPClient(String addr, int port) throws UnknownHostException, IOException {
		socket = new Socket(addr, port);
	}
	
	/**
	 * Runs the client which maintains the connection open as long as the object received
	 * is not null. 
	 */
	@Override
	public final void run() {
		ObjectOutputStream oos;
		ObjectInputStream ois;
		Object reply = null;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
			do {
				oos.writeUnshared(createRequest());
				reply = ois.readObject();
				handleReply(reply);
			} while (reply != null);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Creates a request directed at the server and allows the implementation of a connection interruption 
	 * mechanism.
	 * 
	 * @return a server request
	 */
	protected abstract Object createRequest();
	
	/**
	 * Handles the reply received from the server after a request.
	 * 
	 * @param reply is the message received from the server
	 */
	protected abstract void handleReply(Object reply);
	
}
