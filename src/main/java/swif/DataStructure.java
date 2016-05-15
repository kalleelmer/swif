package swif;

import java.util.ArrayList;
import java.util.UUID;

public class DataStructure {
	private Gui g;
	private ArrayList<Peer> peers;
	/**
	 * This a universally unique identifier used to distinguish each client,
	 * even if multiple clients share the same name.
	 */
	public final String uuid;

	public DataStructure() {
		peers = new ArrayList<>();
		uuid = UUID.randomUUID().toString();
		System.out.println("Data structure initialized. UUID is " + uuid);
	}

	public void addGuiListener(Gui g) {
		this.g = g;
	}

	public synchronized void addPeer(Peer p) {
		// TODO prevent duplication
		// TODO clean out clients that are no longer active
		peers.add(p);
		g.notify();
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

}