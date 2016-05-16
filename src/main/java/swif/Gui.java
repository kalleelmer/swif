package swif;

public class Gui {
	private PeerData ds;

	public Gui() {
		this.ds = PeerData.getInstance();

		// Faktiskt gui

		startListening();
	}

	private void startListening() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// while(true){
				// try{
				// this.wait();
				// //someList.setPeers(ds.getPeers()); //datastructure was
				// modified
				// } catch (InterruptedException e){
				// //Nothing
				// }
				// }
			}
		}).start();
	}

}