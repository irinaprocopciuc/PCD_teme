package Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	private static ServerSocket serverSocket;

	public static void main(String args[]) {

		
		try {
			serverSocket = new ServerSocket(7711);
			while (true) {
				Socket clientSocket = null;
				try {
					clientSocket = serverSocket.accept();
					
					ClientHandler client = new ClientHandler(clientSocket);
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
}
