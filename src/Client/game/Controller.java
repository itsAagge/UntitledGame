package Client.game;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Controller {
    private static Socket clientSocket;
    private static DataOutputStream outToServer;

    public static void setClientSocket(Socket clientSocket) {
        Controller.clientSocket = clientSocket;
    }

    public static void setOutToServer(DataOutputStream outToServer) {
        Controller.outToServer = outToServer;
    }

    public static void sendToServer(String message) throws IOException {
        outToServer.writeBytes(message + "\n");
    }

    public static void updateGamestate(String message) {

    }
}
