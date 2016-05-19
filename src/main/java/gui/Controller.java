package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import swif.FileSender;
import swif.Peer;
import swif.PeerData;
import swif.TransferState;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {
	private Stage stage;
	private File selectedFile = null;

	@FXML
	private Label fileheading;
	@FXML
	private Label filelabel;
	@FXML
	private Label local_ip_label;
	@FXML
	private Rectangle drop_area;
	@FXML
	private Button send_button;
	@FXML
	private TextField target_input;
	@FXML
	private ListView<InetAddress> target_list;

	@FXML
	public void initialize() {
		try {
			local_ip_label.setText(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		initializeTargetList();
	}

	private void initializeTargetList() {
		target_input.setText(""); // Needs to be set before adding the listener
		target_list.getSelectionModel().selectedItemProperty().addListener((observableValue, o, selectedAddress) -> {
			target_input.setText(selectedAddress.getHostAddress());
		});
	}

	public void initStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	public void openFilePicker() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select file");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			setFile(selectedFile);
		}
	}

	public void setFile(File file) {
		selectedFile = file;
		fileheading.setText("File selected!");
		filelabel.setText(file.getName());
		drop_area.setStroke(Color.valueOf("#A8ECBE"));
		send_button.setVisible(true);
	}

	@FXML
	public void sendFile() {
		String target = target_input.getText();
		try {
			sendFile(InetAddress.getByName(target), selectedFile);
			new FileSender(InetAddress.getByName(target), selectedFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendFile(InetAddress target, File file) {
		try {
			InetAddress address = target;
			FileSender sender = new FileSender(address, file);
			sender.start();
			System.out.println("Connecting...");
			sender.waitForStateChange(TransferState.CONNECTING);
			System.out.println("Waiting for receiver to accept...");
			sender.waitForStateChange(TransferState.PENDING);
			System.out.println("Sending...");
			sender.join();
			System.out.println("Sent!");
		} catch (UnknownHostException e) {
			System.out.println("Invalid host: " + e.getMessage());
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Sending error: " + e.getMessage());
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void addTargetAddresses(List<InetAddress> targetAddresses) {
		target_list.setItems(FXCollections.observableList(targetAddresses));
	}

	public File openFileSaveDialog(String host, String filename) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Save file");
		alert.setHeaderText("Someone wants to send you a file!");
		alert.setContentText("Do you want to receive '" + filename + "' from '" + host + "?");
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK) {
			return openSaveDialog(filename);
		}
		return null;
	}

	private File openSaveDialog(String filename) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select file location");
		fileChooser.setInitialFileName(filename);
		File selectedFile = fileChooser.showSaveDialog(stage);
		if (selectedFile != null) {
			return selectedFile;
		}
		return null;
	}

}
