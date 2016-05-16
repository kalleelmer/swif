package swif;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadCastThread extends Thread {
	private static final int BROADCAST_INTERVAL = 2000;
	private static final int RETRY_TIMEOUT = 5000;

	private PeerData ds;
	private DatagramSocket sock;

	public BroadCastThread() throws IOException {
		this.ds = PeerData.getInstance();
		sock = new DatagramSocket();
		sock.setBroadcast(true);

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
			while (true) {
				try {
					String message = "SERVICE_OFFER:" + ds.uuid + ":" + ds.getPeerName();
					byte[] data = message.getBytes();
					DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),
							ds.getBroadcastPort());
					sock.send(p);
				} catch (IOException e) {
					if (isInterrupted()) {
						throw new InterruptedException();
					} else {
						System.out.println("Broadcast thread error: " + e.getMessage());
						System.out.println("Broadcast thread retry in " + RETRY_TIMEOUT + " ms");
						Thread.sleep(RETRY_TIMEOUT);
					}
				}
				Thread.sleep(BROADCAST_INTERVAL);
			}
		} catch (InterruptedException e1) {
			System.out.println("Broadcast thread exiting.");
			return;
		}
	}
}