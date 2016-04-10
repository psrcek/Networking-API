package me.psrcek.networkingAPI;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;

public class EncryptedClient {

	private Socket client;

	private String IP;
	private int port;
	
	private KeyPair pair;

	public EncryptedClient(String IP, int port, int keysize) throws NoSuchAlgorithmException {
		this.IP = IP;
		this.port = port;
		
		//Generate KeyPair
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
		SecureRandom rand = new SecureRandom();
		gen.initialize(keysize, rand);

		pair = gen.generateKeyPair();
	}

	public List<String> request(String message) throws UnknownHostException, IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		client = new Socket(IP, port);

		DataOutputStream out = new DataOutputStream(client.getOutputStream());
		DataInputStream in = new DataInputStream(client.getInputStream());

		// Send client's public key to server
		out.writeUTF(EncryptionHandler.encode(pair.getPublic()));

		// Recieve server's public key
		PublicKey serverPubKey = (PublicKey) EncryptionHandler.decode(in.readUTF());

		// Send request to server
		out.writeUTF(EncryptionHandler.encryptAndEncode((Serializable) message, serverPubKey));

		// Recieve response from server
		@SuppressWarnings("unchecked")
		List<String> recieved = (List<String>) EncryptionHandler.decrypt((SealedObject) EncryptionHandler.decode(in.readUTF()), pair.getPrivate());

		client.close();

		return recieved;
	}
}
