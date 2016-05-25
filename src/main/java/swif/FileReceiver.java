package swif;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class FileReceiver extends Thread {
	private TransferState state = TransferState.CONNECTING;
	private Socket socket;
	private File savePath = null;
	private String fileName;
	private int length;

	public FileReceiver(Socket socket) {
		this.socket = socket;
	}

	private void sendState() {
		String message = state.toString() + "\n";
		try {
			socket.getOutputStream().write(message.getBytes());
			socket.getOutputStream().flush();
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
			fileName = scan.nextLine();
			length = Integer.parseInt(scan.nextLine());
			setState(TransferState.PENDING);
			waitForStateChange(TransferState.PENDING);
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
			byte[] buffer = new byte[PeerData.BUFFER_SIZE];
			int byteCounter = 0;
			output = new FileOutputStream(savePath);
			while (byteCounter < length) {
				int remaining = length - byteCounter;
				// System.out.println(remaining + " more bytes to read.");
				int result = input.read(buffer, 0, Math.min(buffer.length, remaining));
				// System.out.println("Received " + byteCounter + " bytes of file data");
				output.write(buffer, 0, result);
				byteCounter += result;
			}
			// System.out.println("Done, received " + byteCounter + " bytes of file data");
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

	public String getFileName() {
		return fileName;
	}

	public int getLength() {
		return length;
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
			this.savePath = savePath;
			setState(TransferState.ACCEPTED);
		}
	}

	private synchronized void setState(TransferState newState) {
		if (state != newState) {
			state = newState;
			notifyAll();
			sendState();
		}
	}

	public synchronized TransferState waitForStateChange(TransferState lastState) throws InterruptedException {
		while (state == lastState) {
			wait();
		}
		return state;
	}
}
