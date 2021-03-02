package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

	private static Scanner scn;

	private static DataInputStream input;
	private static DataOutputStream output;
	private static Socket socket;


	public static void main(String args[]) {
		scn = new Scanner(System.in);
		System.out.print("Enter your name: ");
		String userName = scn.nextLine();

		try {
			socket = new Socket("fenrir.info.uaic.ro", 7711);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			String msgReceived;
			while (true) {
				String msg = scn.nextLine();
				if (!msg.equals("exit")) {
					output.writeUTF(userName + ":" + msg);
				} else {
					output.writeUTF("Exit");
					System.out.println("Disconnected from the server");
					break;
				}

				msgReceived = input.readUTF();
				System.out.println("Server: \""+msgReceived+"\"");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
