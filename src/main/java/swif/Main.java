package swif;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			// TODO Run interactive
			new BroadCastThread().start();
			new PeerListener().start();
			new FileListener().start();
			Gui g = new Gui();
		} else {
			switch (args[0]) {
			case "send":
				send(args);
				return;
			case "receive":
				receive(args);
				return;
			default:
				System.out.println("Usage: swif <send|receive>");
				System.exit(1);
			}
		}
	}

	private static void receive(String[] args) {
		ServerSocket server = null;
		Scanner cli = new Scanner(System.in);
		try {
			server = new ServerSocket(PeerData.FILE_PORT);
			System.out.println("Waiting for incoming connection...");
			Socket socket = server.accept();
			FileReceiver receiver = new FileReceiver(socket);
			receiver.start();
			System.out.println("Connection established. Waiting for metadata...");
			receiver.waitForStateChange(TransferState.CONNECTING);
			System.out.println("Receiving '" + receiver.getFileName() + "' (" + receiver.getLength() + " bytes)");
			System.out.print("Enter save path: ");
			String filename = cli.nextLine();
			receiver.accept(new File(filename));
			receiver.join();
			System.out.println("Transfer complete");
		} catch (IOException e) {
			System.out.println("Receive error: " + e.getMessage());
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (server != null) {
				try {
					server.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			cli.close();
		}
	}

	private static void send(String[] args) {
		if (args.length < 3) {
			System.out.println("Usage: swif send <filename> <host>");
			System.exit(1);
		}
		File file = new File(args[1]);
		if (!file.exists()) {
			System.out.println("File not found");
			System.exit(1);
		}
		try {
			InetAddress address = InetAddress.getByName(args[2]);
		} catch (UnknownHostException e) {
			System.out.println("Invalid host: " + e.getMessage());
			System.exit(1);
		}
	}

}