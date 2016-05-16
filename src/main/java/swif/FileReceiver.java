package swif;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class FileReceiver extends Thread {
	private static final int BUFFER_SIZE = 10;
	private TransferState state = TransferState.PENDING;
	private Socket socket;

	public FileReceiver(Socket socket) {
		this.socket = socket;
	}

	private void sendState() {
		String message = state.toString() + "\n";
		try {
			socket.getOutputStream().write(message.getBytes());
		} catch (IOException e) {
			System.out.println("FileReceiver failed to send state to sender: " + e.getMessage());
			errorAbort();
		}
	}

	public void run() {
		try {
			InputStream input = socket.getInputStream();
			Scanner scan = new Scanner(input);
			String filename = scan.nextLine();
			System.out.println("Receiving file with name '" + filename + "'");
			int length = Integer.parseInt(scan.nextLine());
			System.out.println("File length is " + length);
			accept(); // TODO Remove, for testing
			waitForAccept();
			switch (state) {
			case ACCEPTED:
				sendState();
				break;
			case ABORTED:
				abort();
				return;
			default:
				errorAbort();
				return;
			}
			byte[] buffer = new byte[BUFFER_SIZE];
			int byteCounter = 0;
			while (byteCounter < length) {
				int remaining = length - byteCounter;
				System.out.println(remaining + " more bytes to read.");
				byteCounter += input.read(buffer, 0, Math.min(buffer.length, remaining));
				System.out.println("Received " + byteCounter + " bytes of file data");
			}
			System.out.println("Done, received " + byteCounter + " bytes of file data");
			// TODO Save file
			close();

		} catch (IOException e) {
			System.out.println("FileReceiver failed to read data: " + e.getMessage());
			errorAbort();
		} catch (NumberFormatException e) {
			System.out.println("FileReceiver bad length from sender: " + e.getMessage());
			errorAbort();
		} catch (InterruptedException e) {
			System.out.println("FileReceiver was interrupted. Aborting.");
			errorAbort();
		}
	}

	private void close() {
		state = TransferState.COMPLETED;
		sendState();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void abort() {
		state = TransferState.ABORTED;
		sendState();
		try {
			socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void errorAbort() {
		state = TransferState.ERROR;
		try {
			socket.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private synchronized void waitForAccept() throws InterruptedException {
		while (state == TransferState.PENDING) {
			wait();
		}
	}

	public synchronized void accept() {
		if (state == TransferState.PENDING) {
			state = TransferState.ACCEPTED;
		}
		// TODO Add path parameter from user
		notifyAll();
	}
}
