package Client.game;

import java.net.*;
import java.io.*;
import javafx.application.Application;

public class App {
	private static String playerName;
	public static void main(String[] args) throws Exception {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Indtast ip på server");
		String ip = inFromUser.readLine().trim();
		System.out.println("Indtast port på server");
		int port = Integer.parseInt(inFromUser.readLine().trim());

		// Connects to the server
		Socket clientSocket = new Socket(ip, port);

		// Sends the socket to the controller for communication with the server
		Controller.startController(clientSocket);

		System.out.println("Indtast spillernavn");
		playerName = inFromUser.readLine();
		Controller.requestPlayerAddToGame(playerName);
		//GameLogic.makePlayers(navn); -- Tilføj spiller
		Application.launch(Gui.class);
	}

	public static String getPlayerName() {
		return playerName;
	}
}