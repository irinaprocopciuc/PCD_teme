package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;

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
				String weight = msgReceived.substring(msgReceived.lastIndexOf(",") + 1);
				String heightWeight = msgReceived.substring(msgReceived.lastIndexOf(":") + 1);
				String height = heightWeight.substring(0, heightWeight.indexOf(","));
				double BMI = Double.parseDouble(weight) / (Double.parseDouble(height) * Double.parseDouble(height))
						* 10000;
				BMI = Double.parseDouble(new DecimalFormat("##.#").format(BMI));
				System.out.println(msgReceived);
				String username = msgReceived.substring(0, msgReceived.indexOf(":"));
				output.writeUTF(username + ", " + "your BMI is: " + BMI);

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
