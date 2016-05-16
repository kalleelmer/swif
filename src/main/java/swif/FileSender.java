package swif;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class FileSender extends Thread {
	private TransferState state = TransferState.CONNECTING;
	private Socket socket;
	private File file;

	public FileSender(InetAddress host, File file) throws IOException {
		socket = new Socket(host, PeerData.FILE_PORT);
		this.file = file;
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
		new StateReader().start();
		FileInputStream input = null;
		try {
			input = new FileInputStream(file);
			OutputStream output = socket.getOutputStream();
			long length = file.length();
			state = TransferState.PENDING;
			output.write((file.getName() + "\n").getBytes());
			output.write((Long.toString(length) + "\n").getBytes());
			waitForStateChange(TransferState.PENDING);
			byte[] buffer = new byte[PeerData.BUFFER_SIZE];
			int byteCount = 0;
			while (byteCount < length) {
				byteCount += input.read(buffer, 0, buffer.length);
				output.write(buffer);
			}
			output.flush();
			setState(TransferState.SENT);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			setState(TransferState.ERROR);
		} catch (IOException e) {
			e.printStackTrace();
			setState(TransferState.ERROR);
		} catch (InterruptedException e) {
			e.printStackTrace();
			setState(TransferState.ERROR);
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
				if (input != null) {
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

	private synchronized void setState(TransferState newState) {
		if (state != newState) {
			state = newState;
			notifyAll();
		}
	}

	public synchronized TransferState waitForStateChange(TransferState lastState) throws InterruptedException {
		while (state == lastState) {
			wait();
		}
		return state;
	}

	private class StateReader extends Thread {

		public StateReader() {

		}

		public void run() {
			try {
				Scanner scan = new Scanner(socket.getInputStream());
				while (!isInterrupted() && scan.hasNextLine()) {
					TransferState newState = TransferState.valueOf(scan.nextLine());
					System.out.println("State changed to " + newState);
					setState(newState);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
