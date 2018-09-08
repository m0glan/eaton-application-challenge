package com.moglan.eac.connection;

import java.io.Serializable;

/**
 * Class symbolizing the unit of exchange between a client and the server
 * 
 * @author Vlad-Adrian Moglan
 *
 * @param <T> is the type of data sent - must be serializable
 */
public class Message<T extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -499005692995480489L;
	
	private long senderID;
	private Protocol protocol;
	private T data;
	
	/**
	 * Initializes the sender ID and the protocol of the message.
	 * 
	 * @param senderID is the identification number of the sender
	 * @param protocol defines the procedure to be followed by the client or the server
	 */
	public Message(long senderID, Protocol protocol) {
		this.senderID = senderID;
		this.protocol = protocol;
		this.data = null;
	}
	
	/**
	 * Initializes the sender ID, the protocol and the data to be exchanged.
	 * 
	 * @param senderID is the identification number of the sender
	 * @param protocol defines the procedure to be followed by the client or the server
	 * @param data is the serializable object or the structure to be exchanged between two nodes
	 */
	public Message(long senderID, Protocol protocol, T data) {
		this.senderID = senderID;
		this.protocol = protocol;
		this.data = data;
	}
	
	public long getSenderID() { return senderID; }
	
	public Protocol getProtocol() { return protocol; }
	
	public T getData() { return data; }

}
