package swif;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Peer {
	private static final long TIMEOUT = 10000;

	public final String uuid;
	private String name;
	private InetAddress address;
	private ArrayList<String> files;
	private long lastSeen = System.currentTimeMillis();

	/**
	 * Constructs a new peer from a broadcast packet. This packet should contain
	 * the UUID and name of the peer.
	 */
	public Peer(DatagramPacket packet) throws PeerException {
		byte[] data = packet.getData();
		String content = new String(data, 0, packet.getLength());
		String[] field = content.split(":");
		if (field.length < 3) {
			throw new PeerException("Malformed peer broadcast packet: " + content);

		}
		this.uuid = field[1];
		this.name = field[2];
		address = packet.getAddress();
		files = new ArrayList<>();
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Peer) {
			Peer other = (Peer) object;
			return other.address == address && other.name == name;
		}
		return false;
	}

	public synchronized void addFile(String file) {
		files.add(file);
	}

	/**
	 * Updates the last seen time for this peer, to indicate that it is alive
	 * and shouldn't be removed.
	 */
	public void refresh() {
		lastSeen = System.currentTimeMillis();
	}

	/**
	 * Checks if this Peer has sent a broadcast packet within the timeout
	 * threshold. If this method returns false, the peer has disappeared and
	 * should be removed.
	 */
	public boolean isAlive() {
		return System.currentTimeMillis() - lastSeen < TIMEOUT;
	}
}