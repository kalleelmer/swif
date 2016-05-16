package swif;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileListener extends Thread {
	private final PeerData ds;

	public FileListener() {
		ds = PeerData.getInstance();
	}

	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(PeerData.FILE_PORT);
			while (!isInterrupted()) {
				Socket socket = server.accept();
				FileReceiver receiver = new FileReceiver(socket);
				ds.addReceiver(receiver);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO better error handling with user-friendly message
			System.exit(1);
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
