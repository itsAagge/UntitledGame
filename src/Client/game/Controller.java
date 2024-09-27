package Client.game;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;
    private static ThreadIn threadInFromServer;

    public static void startController(Socket clientSocket) throws Exception {
        Controller.clientSocket = clientSocket;

        // For output to the server
        Controller.outToServer = new DataOutputStream(clientSocket.getOutputStream());

        // For input from the server
        createThreadForIncomingInformation();
    }
    public static void createThreadForIncomingInformation() throws Exception {
        threadInFromServer = new ThreadIn(clientSocket);
    }

    public static void setDataOutputStream(DataOutputStream outToServer) {
        Controller.outToServer = outToServer;
    }

    public static void updateGamestate(String message) {

    }

    public static void requestPlayerAddToGame(String name) throws IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("MessageType", "Request");
        jsonObject.put("RequestType", "AddPlayer");
        jsonObject.put("Player", name);

        outToServer.writeBytes(jsonObject + "\n");
    }

    public static void requestPlayerMove(int deltaX, int deltaY, String direction) {

    }
}
