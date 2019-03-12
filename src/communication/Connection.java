package communication;

import communication.TCPManager.ConnectionEvent;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

public class Connection {

	private String uuid;
	private Socket socket;
	private Emisor emisor;
	private List<ConnectionEvent> listeners;

	public Connection(Socket socket) {
		uuid = UUID.randomUUID().toString();
		this.socket = socket;
	}

	public void defineListeners(List<ConnectionEvent> listeners) {
		this.listeners = listeners;
	}


	public void init() {
		try {
			Receptor receptor = new Receptor(socket.getInputStream());
			receptor.start();
			emisor = new Emisor(socket.getOutputStream());
		}catch(IOException ex) {
			ex.printStackTrace();
		}

	}

	public void sendMessage(String msj) {
		emisor.enviarMensaje(msj);
	}

	public void closeConnection() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUuid() {
		return uuid;
	}

	// Emisor Thread
	public class Emisor {
		private OutputStream out;

		public Emisor(OutputStream out) {
			this.out = out;
		}

		public void enviarMensaje(String msj) {
			new Thread( () -> {
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(out)));
				writer.println(msj);
				writer.flush();
			}).start();
		}

	}

	// Recepto Thread
	public class Receptor extends Thread{
		private InputStream input;
		private boolean isAlive = true;

		public Receptor(InputStream input) {
			this.input = input;
		}

		@Override
		public void run() {
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));
				while( isAlive ) {
					String line = reader.readLine();
					//Detectamos la desconexion
					if(line ==  null) {
						input.close();
						isAlive = false;
					}
					System.out.println(">>Receptor: " + line);
					for(int i=0 ; i < listeners.size() && isAlive ; i++) listeners.get(i).onMessage(uuid, line);
				}
			}catch(IOException ex) {

			}
		}

	}

}