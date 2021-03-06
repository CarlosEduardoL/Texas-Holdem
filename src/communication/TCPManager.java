package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TCPManager {

	private static TCPManager instance = null;

	private TCPManager() {
		listeners = new ArrayList<>();
		connections = new HashMap<>();
	}

	public synchronized static TCPManager getInstance() {
		if(instance == null) {
			instance = new TCPManager();
		}
		return instance;
	}


	//Global
	private ServerSocket server;
	private HashMap<String, Connection> connections;
	private List<ConnectionEvent> listeners;

	//Server methods
	public void waitForConnection(int port) {
		try {
			server = new ServerSocket(port);

			while(true) {
				System.out.println("Esperando cliente");
				Socket socket = server.accept();
				System.out.println("Cliente conectado!");
				Connection connection = new Connection(socket);
				connection.defineListeners(listeners);
				connection.init();
				connections.put(socket.getInetAddress().getHostAddress(), connection);
				for(int i=0 ; i<listeners.size() ; i++) listeners.get(i).onConnection(socket.getInetAddress().getHostAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBroadcast(String line) {
		for( String key : connections.keySet() ) {
			connections.get(key).sendMessage(line);
		}
	}


	// Make the class observable
	public interface ConnectionEvent{
		void onConnection(String uuid);
		void onMessage(String uuid, String msj);
	}



	public void addConnectionEvent(ConnectionEvent listener) {
		listeners.add(listener);
	}

	public int getClientsCount() {
		return connections.size();
	}

	public Connection getConnectionById(String uuid) {
		return connections.get(uuid);
	}

	public void sendDirectMessage(String remitente, String destinatario, String mensaje) {
		getConnectionById(destinatario).sendMessage(mensaje);
	}

}