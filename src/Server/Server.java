package Server;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.*;
import java.util.HashMap;

public class Server {
	private static final HashMap<Socket, DataOutputStream> connections = new HashMap<>();
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			connections.put(connectionSocket, new DataOutputStream(connectionSocket.getOutputStream()));
			(new ServerThreadIn(connectionSocket)).start();
		}
	}

	public static void sendUpdateToClients(JSONObject update) {
		try {
			for (DataOutputStream dataOutputStream : connections.values()) {
				dataOutputStream.writeBytes(update + "\n");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
