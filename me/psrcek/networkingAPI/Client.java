package me.psrcek.networkingAPI;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Base64;

public class Client {
	
	private Socket client;
	
	private DataOutputStream outStream;
	private DataInputStream inStream;
	
	private String IP;
	private int port;
	
	public Client(String IP, int port) {
		this.IP = IP;
		this.port = port;
	}
	
	public ArrayList<String> request(String message) throws UnknownHostException, IOException, ClassNotFoundException {
		client = new Socket(IP, port);
		
		outStream = new DataOutputStream(client.getOutputStream());
		inStream = new DataInputStream(client.getInputStream());
		
		outStream.writeUTF(message);
		
		ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(inStream.readUTF().getBytes()));
		
		@SuppressWarnings("unchecked")
		ArrayList<String> recieved = (ArrayList<String>) new ObjectInputStream(in).readObject();
		
		client.close();
		
		return recieved;
	}
}
