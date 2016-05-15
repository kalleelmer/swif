package swif;

public class Gui{
	private DataStructure ds;
	private OfferThreads ot;

	public Gui(DataStructure ds, OfferThreads ot){
		this.ds = ds;
		this.ot = ot;

		//Faktiskt gui

		startListening();
	}


	private void startListening(){
		new Thread(new Runnable(){
			@Override
			public void run(){
				while(true){
					try{
						this.wait();
						//someList.setPeers(ds.getPeers()); //datastructure was modified
					} catch (InterruptedException e){
						//Nothing
					}
				}
			}
		}).start();
	}


}