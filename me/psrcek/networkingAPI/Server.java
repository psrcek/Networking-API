package me.psrcek.networkingAPI;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Server {
	
	private List<ServerMessageRecieveHandler> messageRecieveHandlers = new ArrayList<ServerMessageRecieveHandler>();
	private ServerSocket serverSocket;
	private boolean shouldRun;
	private boolean stopWait;

	public Server(ServerMessageRecieveHandler smrh, int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000);
		messageRecieveHandlers.add(smrh);
	}
	
	public Server(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000);
	}
	
	/**
	public Server(ServerMessageRecieveHandler smrh, int port, int socketTimeoutInMiliseconds) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(socketTimeoutInMiliseconds);
		messageRecieveHandlers.add(smrh);
	}
	
	public Server(int port, int socketTimeoutInMiliseconds) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(socketTimeoutInMiliseconds);
	}
	*/
	
	public void start() {
		shouldRun = true;
		System.out.println("[Server] Listening on port " + serverSocket.getLocalPort() + "...");
		while (shouldRun) {
			try {
				Socket server = serverSocket.accept();
				
				DataInputStream in = new DataInputStream(server.getInputStream());
				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				
				List<String> responses = recieveMessage(server, in.readUTF());
				
				/**
				if (!responses.isEmpty()) {
					for (String response : responses) {
						out.writeUTF(response);
					}
				}
				*/
				
				ByteArrayOutputStream out2 = new ByteArrayOutputStream();
			    new ObjectOutputStream(out2).writeObject(responses);
				
				out.writeUTF(Base64.getEncoder().encodeToString(out2.toByteArray()));
				
				server.close();
			} catch (SocketTimeoutException s) {
				//System.out.println("[Server] Socket timed out!");
				//break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		
		System.out.println("[Server] Stopped!");
		stopWait = false;
	}
	
	public void stop() {
		shouldRun = false;
		stopWait = true;
		while (stopWait) {};
	}
	
	private List<String> recieveMessage(Socket server, String message) {
		List<String> responses = new ArrayList<String>();
		String response;
		
		if (!messageRecieveHandlers.isEmpty()) {
			for (ServerMessageRecieveHandler h : messageRecieveHandlers) {
				response = h.messageRecieved(server, message);
				if (response != null) {
					responses.add(response);
				}
			}
		}
		return responses;
	}
	
	public void addRecieveHandler(ServerMessageRecieveHandler h) {
		messageRecieveHandlers.add(h);
	}
	
}
