package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private DataInputStream input;
	private DataOutputStream output;

	private Socket client;

	public ClientHandler(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {

		try {
			input = new DataInputStream(client.getInputStream());
			output = new DataOutputStream(client.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String msgReceived;
		while (true) {
			try {
				msgReceived = input.readUTF();
				if (msgReceived.equals("Exit")) {
					System.out.println("Client closed");
					this.client.close();
					break;
				}
				System.out.println(msgReceived);
				output.writeUTF("Message received by the server: "+ msgReceived);

			} catch (IOException e) {
				break;
			}
		}

		try {
			this.input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	
}
