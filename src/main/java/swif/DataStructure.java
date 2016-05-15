package swif;
import java.util.ArrayList;


public class DataStructure {
	private Gui g;
	private ArrayList<Peer> peers;

	public DataStructure(){
		peers = new ArrayList<>();
	}

	public void addGuiListener(Gui g){
		this.g = g;
	}

	public synchronized void addPeer(Peer p){
		//Ändra så att det inte blir dupliceringar
		peers.add(p);
		g.notify();
	}

}