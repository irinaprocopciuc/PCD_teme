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


	public static void main(String args[]) {
		scn = new Scanner(System.in);
		System.out.print("Enter your name: ");
		String userName = scn.nextLine();
		System.out.print("Enter your height(cm): ");
		String height = scn.nextLine();
		System.out.print("Enter your weight(kg): ");
		String weight = scn.nextLine();

		try {
			socket = new Socket("fenrir.info.uaic.ro", 7711);
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());

			while (true) {
				String msg = scn.nextLine();
				if (!msg.equals("exit")) {
					output.writeUTF(userName + ":" + height + "," + weight);
					System.out.println(input.readUTF());
				} else {
					output.writeUTF("Exit");
					System.out.println("Disconnected from the server");
					break;
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	
}
