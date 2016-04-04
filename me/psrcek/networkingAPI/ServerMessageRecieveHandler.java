package me.psrcek.networkingAPI;

import java.net.Socket;

public interface ServerMessageRecieveHandler {

	String messageRecieved(Socket server, String message);

}
