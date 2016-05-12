import java.util.ArrayList;

public class Peer{
	private String name;
	private ArrayList<String> files;

	public Peer(String name){
		this.name = name;
		files = new ArrayList<>();
	}

	public synchronized void addFile(String file){
		files.add(file);
	}
}