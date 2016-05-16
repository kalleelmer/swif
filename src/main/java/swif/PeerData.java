package swif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

public class PeerData extends Observable {
	private static volatile PeerData instance;

	public static PeerData getInstance() {
		return instance != null ? instance : (instance = new PeerData());
	}

	public static final int FILE_PORT = 36837;
	private Map<String, Peer> peers;
	private List<FileReceiver> incoming = new ArrayList<FileReceiver>();
	/**
	 * This a universally unique identifier used to distinguish each client,
	 * even if multiple clients share the same name.
	 */
	public final String uuid;

	private PeerData() {
		peers = new HashMap<String, Peer>();
		uuid = UUID.randomUUID().toString();
		System.out.println("Data structure initialized. UUID is " + uuid);
	}

	/**
	 * Adds a newly found peer to the list of available peers, or replaces it if
	 * there has been a change in any property of that peer. The last seen
	 * timestamp is always updated to indicate that the peer is alive. It will
	 * not add itself, as determined by the UUID property.
	 */
	public synchronized void addPeer(Peer peer) {
		// TODO clean out clients that are no longer active
		if (peer.uuid.equals(uuid)) {
			// Don't add self as peer
			System.out.println("Ignoring self");
			return;
		}
		Peer existing = peers.get(peer.uuid);
		if (existing == null || !peers.get(peer.uuid).equals(peer)) {
			peers.put(peer.uuid, peer);
			System.out.println("Added new peer: " + peer.uuid);
			setChanged();
			notifyObservers();
		} else {
			existing.refresh();
			System.out.println("Peer no change: " + peer.uuid);
		}
	}

	/**
	 * Returns the client name specified by the user. This name makes it
	 * possible to find the right computer in the network.
	 */
	public String getPeerName() {
		// TODO Allow the user to select a name on startup
		return "TEST_CLIENT";
	}

	/**
	 * Returns the port number used to discover other clients in the local
	 * network.
	 */
	public int getBroadcastPort() {
		return 37635;
	}

	public void addReceiver(FileReceiver receiver) {
		incoming.add(receiver);
		setChanged();
		notifyObservers();
	}

}