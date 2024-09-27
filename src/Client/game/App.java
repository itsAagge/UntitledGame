package Client.game;

import java.net.*;
import java.io.*;
import javafx.application.Application;

public class App {
	private static String ip;
	private static int port;
	public static void main(String[] args) throws Exception{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast ip på server");
		ip = inFromUser.readLine().trim();
		System.out.println("Indtast port på server");
		port = Integer.parseInt(inFromUser.readLine().trim());

		Socket clientSocket = new Socket(ip, port);
		Controller.setClientSocket(clientSocket);
		Controller.setOutToServer(new DataOutputStream(clientSocket.getOutputStream()));

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		System.out.println("Indtast spillernavn");
		String navn = inFromUser.readLine();
		Controller.sendToServer(navn);
		//GameLogic.makePlayers(navn); -- Tilføj spiller
		Application.launch(Gui.class);
	}
}
;