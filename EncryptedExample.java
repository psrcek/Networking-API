package me.psrcek.networkingAPIExample;

import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import me.psrcek.networkingAPI.EncryptedClient;
import me.psrcek.networkingAPI.EncryptedServer;
import me.psrcek.networkingAPI.ServerMessageRecieveHandler;

public class EncryptedExample {
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		
		final EncryptedServer server = new EncryptedServer(6066, 2048);
		
		server.addRecieveHandler(new ServerMessageRecieveHandler() {
			public String messageRecieved(Socket server, String message) {
				return "got yer message: \"" + message + "\" from: " + server.getRemoteSocketAddress();
			}});
		
		server.addRecieveHandler(new ServerMessageRecieveHandler() {
			public String messageRecieved(Socket server, String message) {
				return "got yer message #2: \"" + message + "\" from: " + server.getRemoteSocketAddress();
			}});
		
		Thread serverThread = new Thread(new Runnable() {
			public void run() {
				try {
					server.start();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		serverThread.start();
		
		while (!server.running) {
			Thread.sleep(1);
		}
		
		System.out.println("Generating client 1");
		EncryptedClient client1 = new EncryptedClient("localhost", 6066, 2048);
		System.out.println("Generating client 2");
		EncryptedClient client2 = new EncryptedClient("localhost", 6066, 2048);
		System.out.println("Generating client 3");
		EncryptedClient client3 = new EncryptedClient("localhost", 6066, 2048);
		
		System.out.println("Done, making requests");

		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		
		System.out.println("Done, stopping server");
		
		server.stop();
		
	}

}
