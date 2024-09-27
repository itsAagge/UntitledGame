package Client.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ThreadIn extends Thread {
    Socket clientSocket;
    BufferedReader inFromServer;

    public ThreadIn(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void run() {
        while (true) {
            try {
                String message = inFromServer.readLine();
                Controller.updateGamestate(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
