package Client.game;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThreadIn extends Thread {
    Socket clientSocket;
    BufferedReader inFromServer;

    public ClientThreadIn(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        try {
            Thread.sleep(3000);
            System.out.println("Waiting for id");
            String JSONPlayerIdString = inFromServer.readLine();
            System.out.println(JSONPlayerIdString);

            JSONObject jsonObjectPlayerId = new JSONObject(JSONPlayerIdString);
            int id = jsonObjectPlayerId.getInt("PlayerId");
            System.out.println("Player id: " + id);
            Controller.setPlayerId(id);

            while (true) {
                String update = inFromServer.readLine();
                Controller.updateGamestate(update);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
