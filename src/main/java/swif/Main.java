package swif;

import java.io.IOException;

public class Main {

	public static void main(String... args) throws IOException {
		Gui g = new Gui();
		new BroadCastThread().start();
		new PeerListener().start();
		new FileListener().start();
	}

}