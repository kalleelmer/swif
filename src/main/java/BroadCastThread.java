
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BroadCastThread{
	private DataStructure ds;
	private MulticastSocket sock;

	public BroadCastThread(DataStructure ds) throws IOException{
		this.ds = ds;
		sock = new MulticastSocket();
		sock.joinGroup(InetAddress.getByName("256.0.9.5"));
		

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (sock != null) {
					sock.close();
				}
			}
		});
	}






}