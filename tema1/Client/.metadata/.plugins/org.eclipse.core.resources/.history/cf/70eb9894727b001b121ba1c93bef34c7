package Controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverFromServer implements Runnable {

	private ServerSocket serverSocket;
	private DataInputStream input;

	public ReceiverFromServer() {
		try {
			serverSocket = new ServerSocket(Main.getPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Socket client = serverSocket.accept();
			String msgReceived;

			try {
				input = new DataInputStream(client.getInputStream());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			while (true) {
				try {
					msgReceived = input.readUTF();
					if (msgReceived.equals("end")) {
						break;
					}else {
						System.out.println(msgReceived);
					}
				} catch (IOException e) {
					break;
				}
			}

			while (true) {
				client = serverSocket.accept();

				try {
					input = new DataInputStream(client.getInputStream());
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				msgReceived = input.readUTF();
				System.out.println(msgReceived);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
