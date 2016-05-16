package swif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class FileReceiver extends Thread {
	private static final int BUFFER_SIZE = 10;
	private TransferState state = TransferState.PENDING;
	private Socket socket;
	private File savePath = null;

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
		InputStream input = null;
		Scanner scan = null;
		FileOutputStream output = null;
		try {
			input = socket.getInputStream();
			scan = new Scanner(input);
			String filename = scan.nextLine();
			System.out.println("Receiving file with name '" + filename + "'");
			int length = Integer.parseInt(scan.nextLine());
			System.out.println("File length is " + length);
			accept(new File("testfile")); // TODO Remove, for testing
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
			output = new FileOutputStream(savePath);
			while (byteCounter < length) {
				int remaining = length - byteCounter;
				System.out.println(remaining + " more bytes to read.");
				int result = input.read(buffer, 0, Math.min(buffer.length, remaining));
				System.out.println("Received " + byteCounter + " bytes of file data");
				output.write(buffer, 0, result);
				byteCounter += result;
			}
			System.out.println("Done, received " + byteCounter + " bytes of file data");
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
		} finally {
			try {
				if (input != null) {
					input.close();
				}
				if (scan != null) {
					input.close();
				}
				if (output != null) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	/**
	 * Marks this transfer as accepted by the user. After calling this method,
	 * the transfer of data will begin and it will automatically be saved to
	 * disk.
	 * 
	 * @param savePath
	 *            The file path where the received data should be saved.
	 */
	public synchronized void accept(File savePath) {
		if (state == TransferState.PENDING) {
			state = TransferState.ACCEPTED;
			this.savePath = savePath;
		}
		notifyAll();
	}
}
