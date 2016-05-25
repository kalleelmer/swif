package swif;

import gui.Gui;
import javafx.application.Platform;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FileListener extends Thread {
	private final PeerData ds;
	private final Gui gui;

	public FileListener(Gui gui) {
		ds = PeerData.getInstance();
		this.gui = gui;
	}

	public void run() {
		ServerSocket server = null;
		try {
			server = new ServerSocket(PeerData.FILE_PORT);
			while (!isInterrupted()) {
				Socket socket = server.accept();
				FileReceiver receiver = new FileReceiver(socket);
				ds.addReceiver(receiver);
				receiver.start();
				receiver.waitForStateChange(TransferState.CONNECTING);
				System.out.println("Receiving '" + receiver.getFileName() + "' (" + receiver.getLength() + " bytes)");
				File filename = getFileLocation(receiver);
				receiver.accept(filename);
				receiver.join();
				System.out.println("Transfer complete");
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO better error handling with user-friendly message
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private File getFileLocation(FileReceiver receiver) throws ExecutionException, InterruptedException {
		FutureTask<File> task = new FutureTask<>( () -> gui.getController().openFileSaveDialog("", receiver.getFileName()));
		Platform.runLater(task);
		return task.get();
	}
}
