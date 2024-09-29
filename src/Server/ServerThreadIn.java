package Server;

import org.json.JSONObject;

import java.net.*;
import java.io.*;

public class ServerThreadIn extends Thread{
	Socket connSocket;
	BufferedReader inFromClient;
	
	public ServerThreadIn(Socket connSocket) throws Exception {
		this.connSocket = connSocket;
		this.inFromClient = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
	}
	public void run() {
		try {
			// Recieves the players intent to play along with their username
			String JSONAddPlayerString = inFromClient.readLine();
			System.out.println(JSONAddPlayerString);
			JSONObject jsonObjectAddPlayerRequest = new JSONObject(JSONAddPlayerString);
			String playerName = jsonObjectAddPlayerRequest.getString("PlayerName");

			// Adds the player
			ServerPlayer player = GameLogic.addPlayer(playerName);

			JSONObject jsonObjectAddPlayerResponse = new JSONObject();
			jsonObjectAddPlayerResponse.put("MessageType", "Request");
			jsonObjectAddPlayerResponse.put("RequestType", "AddPlayer");
			jsonObjectAddPlayerResponse.put("PlayerName", playerName);
			jsonObjectAddPlayerResponse.put("PlayerId", player.getId());
			DataOutputStream dataOutputStream = new DataOutputStream(connSocket.getOutputStream());
			System.out.println("Sending response: " + jsonObjectAddPlayerResponse.toString());
			dataOutputStream.writeBytes(jsonObjectAddPlayerResponse.toString() + "\n");

			//Adds the new player to everyone's gui
			pair playerPos = new pair(player.getXpos(), player.getYpos());
			System.out.println("Adding player to x: " + player.getXpos() + ", y: " + player.getYpos());
			//Server.sendUpdateToClients(GameLogic.createPlayerAddedJSON(playerPos, player.getDirection())); // Send update
			Server.sendUpdateToClients(GameLogic.createGamestateJSON()); // Send gamestate

			while (true) {
				String JSONRequest = inFromClient.readLine();
				System.out.println(JSONRequest);

				JSONObject jsonObject = new JSONObject(JSONRequest);
				String messageType = jsonObject.getString("MessageType");
				if (messageType.equals("Request")) {
					String requestType = jsonObject.getString("RequestType");
					switch (requestType) {
						case ("MovePlayer") -> {
							int id = jsonObject.getInt("PlayerId");
							int deltaX = jsonObject.getInt("PlayerDeltaX");
							int deltaY = jsonObject.getInt("PlayerDeltaY");
							String direction = jsonObject.getString("PlayerDirection");

							System.out.println("Player " + id + " wants to move " + deltaX + " to horizontal and " + deltaY + " vertical, facing " + direction);
							GameLogic.updatePlayer(id, deltaX, deltaY, direction);
						}
					}

				}


			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		// do the work here
	}
}
