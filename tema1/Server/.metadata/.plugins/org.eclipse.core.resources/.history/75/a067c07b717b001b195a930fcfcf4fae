package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Main {

	private static ServerSocket serverSocket;
	
	private static List<String> messages;
	private static List<ClientHandler> clientList;
	private static List<Integer> ports;

	public static void main(String args[]) {

		messages = new ArrayList<String>();
		clientList = new ArrayList<ClientHandler>();
		ports = new ArrayList<Integer>();
		
		try {
			serverSocket = new ServerSocket(7711);
			while (true) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					
					ClientHandler client = new ClientHandler(clientSocket); 
					clientList.add(client);
					Thread clientThread = new Thread(client);
					clientThread.start();

				} catch (Exception e) {
					clientSocket.close();
					e.printStackTrace();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<String> getMessages() {
		return messages;
	}
	
	public static void addMessages(String msg) {
		messages.add(msg);
		
		for(ClientHandler i: clientList) {
			if(!msg.substring(0,msg.indexOf(":")).equals(i.getUName())) {
				i.sendNewMsg(msg,ports.get(clientList.indexOf(i)));
			}
		}
		
	}
	
	public static void addPort(int port) {
		ports.add(port);
	}
	
	public static List<ClientHandler> getClients() {
		return clientList;
	}
}
