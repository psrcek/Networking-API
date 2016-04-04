package me.psrcek.networkingAPIExample;

import java.io.IOException;
import java.net.Socket;

import me.psrcek.networkingAPI.Client;
import me.psrcek.networkingAPI.Server;
import me.psrcek.networkingAPI.ServerMessageRecieveHandler;

public class Example {
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		
		final Server server = new Server(6066);
		
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
				server.start();
			}
		});
		
		serverThread.start();
		
		Thread.sleep(3000);

		Client client1 = new Client("localhost", 6066);
		Client client2 = new Client("localhost", 6066);
		Client client3 = new Client("localhost", 6066);

		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		System.out.println("[Client1] Response from server: \"" + client1.request("client1").get(0) + "\"");
		System.out.println("[Client2] Response from server: \"" + client2.request("client2").get(1) + "\"");
		System.out.println("[Client3] Response from server: \"" + client3.request("client3") + "\"");
		
		//Thread.sleep(3000);
		
		server.stop();
	}
}
