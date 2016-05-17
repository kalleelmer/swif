package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    public void initialize() {
        String localIp = "";
        try {
            local_ip_label.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
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
            this.selectedFile = selectedFile;
            fileheading.setText("File selected!");
            filelabel.setText(selectedFile.getName());
        }
    }

    @FXML
    public void onDragDropped() {
        System.out.println("ffffff");
    }
}
