package swif;
import java.io.IOException;


public class Main {

	public static void main(String... args) throws IOException{
		DataStructure ds = new DataStructure();
		OfferThreads ot = new OfferThreads();
		Gui g = new Gui(ds, ot);
		ds.addGuiListener(g);
		new BroadCastThread(ds);
	}

}