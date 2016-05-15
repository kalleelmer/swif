package swif;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * This thread listens for broadcast packets from other Peers within the same
 * network. Whenever a packet is received, it will be sent to the main data
 * structure for processing.
 */
public class PeerListener extends Thread {
	private static final int RETRY_TIMEOUT = 5000;

	private DataStructure ds;
	private DatagramSocket sock;

	public PeerListener(DataStructure ds) throws IOException {
		this.ds = ds;
		sock = new DatagramSocket(ds.getBroadcastPort());

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (sock != null) {
					sock.close();
					interrupt();
				}
			}
		});
	}

	public void run() {
		try {
			DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
			while (true) {
				try {
					sock.receive(p);
					Peer peer = new Peer(p);
					ds.addPeer(peer);
				} catch (IOException e) {
					if (isInterrupted()) {
						throw new InterruptedException();
					} else {
						System.out.println("Peer listener error: " + e.getMessage());
						System.out.println("Peer listener retry in " + RETRY_TIMEOUT + " ms");
						Thread.sleep(RETRY_TIMEOUT);
					}
				} catch (PeerException e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (InterruptedException e1) {
			System.out.println("Peer listener exiting.");
			return;
		}
	}
}