package Client.game;

import Server.NPC;
import Server.PowerUp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Controller {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;
    private static ClientThreadIn threadInFromServer;
    private static int playerId;
    private static ArrayList<pair> playersOnScreen = new ArrayList<>();
    private static ArrayList<String> playerPoints = new ArrayList<>();
    public static ArrayList<pair> tempShotPairs = new ArrayList<>();
    public static pair powerUp = new pair(1,1);
    public static pair npcOnGui;

    public static void startController(Socket clientSocket) throws Exception {
        Controller.clientSocket = clientSocket;

        // For output to the server
        Controller.outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // For input from the server
        createThreadForIncomingInformation();
    }

    public static void createThreadForIncomingInformation() throws Exception {
        threadInFromServer = new ClientThreadIn(clientSocket);
        threadInFromServer.start();
    }

    public static void updateGamestate(String gamestate) {
        System.out.println(gamestate);
        JSONObject jsonObject = new JSONObject(gamestate);
        if (npcOnGui != null) {
            Gui.removePlayerOnScreen(npcOnGui);
        }
        Gui.removePowerUp(powerUp);
        playerPoints.clear();
        Gui.removeAllShots();
        pair shooterPair = null;
        boolean starShootingActive = false;
        String shooterDirection = "";
        for (pair pair : playersOnScreen) {
            Gui.removePlayerOnScreen(pair);
        }
        playersOnScreen.clear();
        JSONArray jsonPlayerArray = jsonObject.getJSONArray("PlayerArray");
        for (int i = 0; i < jsonPlayerArray.length(); i++) {
            JSONObject jsonPlayer = jsonPlayerArray.getJSONObject(i);
            pair pos = new pair(0, 0);
            pos.x = jsonPlayer.getInt("PlayerXPos");
            pos.y = jsonPlayer.getInt("PlayerYPos");
            String direction = jsonPlayer.getString("PlayerDirection");
            if (jsonPlayer.getBoolean("PlayerShooting")) {
                shooterPair = pos;
                shooterDirection = direction;
                starShootingActive = jsonPlayer.getBoolean("StarShootingActive");
            }
            playersOnScreen.add(pos);
            Gui.placePlayerOnScreen(pos, direction);
        }
        JSONArray jsonPointArray = jsonObject.getJSONArray("PointArray");
        for (int i = 0; i < jsonPointArray.length(); i++) {
            JSONObject jsonPoint = jsonPointArray.getJSONObject(i);
            String playerName = jsonPoint.getString("PlayerName");
            int playerPoint = jsonPoint.getInt("PlayerPoints");
            playerPoints.add(playerName + ": " + playerPoint + " points");
        }
        Gui.updateScoreTable();
        if (shooterPair != null) {
            Gui.shoot(shooterPair, shooterDirection, starShootingActive);
        }
        JSONObject jsonPU = jsonObject.getJSONObject("PowerUp");
        if (jsonPU.getBoolean("Active")) {
            JSONObject jsonPowerUpPosition = jsonPU.getJSONObject("PowerUpPosition");
            int x = jsonPowerUpPosition.getInt("X");
            int y = jsonPowerUpPosition.getInt("Y");
            pair p = new pair(x, y);
            powerUp = p;
            Gui.placePowerUp(p);
        }
        JSONObject jsonNPC = jsonObject.getJSONObject("NPC");
        if (jsonNPC.getBoolean("isAlive")) {
            int x = jsonNPC.getInt("NPCXPos");
            int y = jsonNPC.getInt("NPCYPos");
            String direction = jsonNPC.getString("NPCDirection");
            npcOnGui = new pair(x,y);
            Gui.placePlayerOnScreen(npcOnGui, direction);
        }
    }

    public static void requestPlayerAddToGame(String name) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Request");
        jsonObject.put("RequestType", "AddPlayer");
        jsonObject.put("PlayerName", name);

        outToServer.writeBytes(jsonObject.toString() + "\n");
    }

    public static void requestPlayerMove(int id, int deltaX, int deltaY, String direction) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Request");
        jsonObject.put("RequestType", "MovePlayer");
        jsonObject.put("PlayerId", id);
        jsonObject.put("PlayerDeltaX", deltaX);
        jsonObject.put("PlayerDeltaY", deltaY);
        jsonObject.put("PlayerDirection", direction);

        outToServer.writeBytes(jsonObject.toString() + "\n");
    }

    public static void requestPlayerShoot(int id) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Request");
        jsonObject.put("RequestType", "PlayerShoot");
        jsonObject.put("PlayerId", id);

        outToServer.writeBytes(jsonObject.toString() + "\n");
    }

    public static void setPlayerId(int id) {
        Controller.playerId = id;
    }

    public static int getPlayerId() {
        return Controller.playerId;
    }

    public static ArrayList<String> getPlayerPoints() {
        return new ArrayList<>(playerPoints);
    }
}
