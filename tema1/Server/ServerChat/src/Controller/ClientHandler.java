package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientHandler implements Runnable {

	private DataInputStream input;
	private DataOutputStream output;

	private String userName = "";

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

				if (portSent == false) {

					output.writeUTF(String.valueOf(7711 + Main.getClients().size() + 2));
					Main.addPort(7711 + Main.getClients().size() + 2);
					sendChat();
					portSent = true;
				}

				msgReceived = input.readUTF();
				if (msgReceived.equals("Exit")) {
					System.out.println("Client closed");
					this.client.close();
					break;
				}

				if (userName.isEmpty() || userName.equals("")) {
					userName = msgReceived.substring(0, msgReceived.indexOf(":"));
				}
				if (!msgReceived.equals("end")) {
					Main.addMessages(msgReceived);
					System.out.println(msgReceived);
				}
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

	private boolean portSent = false;

	private void sendChat() {
		Socket socket;
		try {
			socket = new Socket("localhost", 7711 + Main.getClients().size() + 2);

			DataOutputStream output = new DataOutputStream(socket.getOutputStream());

			for (String i : Main.getMessages()) {
				if (!i.substring(0, i.indexOf(":")).equals(userName)) {
					output.writeUTF(i);
				}
			}
			output.writeUTF("end");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendNewMsg(String msg, int port) {
		Socket socket;
		try {
			socket = new Socket("localhost", port);

			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF(msg);

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUName() {
		return userName;
	}
}
