package com.moglan.eac.model.connection;

import java.io.Serializable;

/**
 * The unit of exchange between a client and the server.
 * 
 * @author Vlad-Adrian Moglan
 *
 * @param <T> is the type of data sent; must be {@code Serializable}
 */
public class Message<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = -499005692995480489L;
	
	private long senderID;
	private Protocol protocol;
	private T data;
	
	/**
	 * Used when the nature of the message is strictly determined by its {@code Protocol}.
	 * 
	 * @param senderID the identification number of the sender
	 * @param protocol defines the procedure to be followed by the client and the server
	 */
	public Message(long senderID, Protocol protocol) {
		this.senderID = senderID;
		this.protocol = protocol;
		this.data = null;
	}
	
	/**
	 * Used when the message is meant to carry data.
	 * 
	 * @param senderID the identification number of the sender
	 * @param protocol defines the procedure to be followed by the client and the server
	 * @param data is the {@code Serializable} object or structure to be exchanged between two nodes
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
