package me.psrcek.networkingAPI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class EncryptedServer {

	private List<ServerMessageRecieveHandler> messageRecieveHandlers = new ArrayList<ServerMessageRecieveHandler>();
	private ServerSocket serverSocket;
	private boolean shouldRun;
	private boolean stopWait;
	private int keysize;
	
	public boolean running = false;

	public EncryptedServer(ServerMessageRecieveHandler smrh, int port, int keysize) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000);
		messageRecieveHandlers.add(smrh);
		
		this.keysize = keysize;
	}

	public EncryptedServer(int port, int keysize) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(1000);
		
		this.keysize = keysize;
	}

	public void start() throws NoSuchAlgorithmException, IOException, ClassNotFoundException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		shouldRun = true;

		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		SecureRandom rand = new SecureRandom();
		gen.initialize(keysize, rand);

		System.out.println("[Server] Generating public/private keypair...");
		KeyPair pair = gen.generateKeyPair();

		System.out.println("[Server] Listening on port " + serverSocket.getLocalPort() + "...");
		running = true;
		while (shouldRun) {
			try {
			Socket server = serverSocket.accept();

			DataInputStream in = new DataInputStream(server.getInputStream());
			DataOutputStream out = new DataOutputStream(server.getOutputStream());

			// Recieve client's public key
			PublicKey clientPubKey = (PublicKey) EncryptionHandler.decode(in.readUTF());

			// Send server's public key to client
			out.writeUTF(EncryptionHandler.encode(pair.getPublic()));

			// Recieve request from client
			String recievedMsg = (String) EncryptionHandler.decrypt((SealedObject) EncryptionHandler.decode(in.readUTF()), pair.getPrivate());
			List<String> responses = recieveMessage(server, recievedMsg);

			// Send response to client
			out.writeUTF(EncryptionHandler.encryptAndEncode((Serializable) responses, clientPubKey));

			// Close connection with client
			server.close();
			} catch(SocketTimeoutException e) {}
		}
		
		running = false;
		System.out.println("[Server] Stopped!");
		stopWait = false;
	}

	public void stop() {
		shouldRun = false;
		stopWait = true;
		while (stopWait) {
		}
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
