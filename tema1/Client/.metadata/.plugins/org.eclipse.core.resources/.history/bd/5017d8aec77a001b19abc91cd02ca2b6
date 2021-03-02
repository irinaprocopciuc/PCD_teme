package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

	private static Scanner scn;

	private static DataOutputStream output;
	private static DataInputStream input;
	private static Socket socket;

	private static String port = "";
	private static boolean chatreceived = false;

	public static void main(String args[]) {
		scn = new Scanner(System.in);
		System.out.print("Enter your name: ");
		String userName = scn.nextLine();

		try {
			socket = new Socket("localhost", 7711);
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			while (true) {
				if (port.isEmpty() || port.equals("")) {
					port = input.readUTF();
					if(chatreceived==false) {
						createReceiver();
						chatreceived = true;
					}
				}
				String msg = scn.nextLine();
				if (!msg.equals("exit")) {
					output.writeUTF(userName + ":" + msg);
				} else {
					output.writeUTF(msg);
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void createReceiver() {
		ReceiverFromServer server = new ReceiverFromServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	public static int getPort() {
		return Integer.valueOf(port);
	}
}
