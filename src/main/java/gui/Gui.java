package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import swif.Peer;
import swif.PeerData;

import java.io.File;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

public class Gui extends Application implements Observer {
    private Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/swif.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();
        this.controller = controller;
        controller.initStage(primaryStage);

        Scene scene = new Scene(root, 640, 320);

        primaryStage.setTitle("Swif");
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            } else {
                event.consume();
            }
        });

        // Dropping over surface
        scene.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                success = true;
                String filePath = null;
                for (File file:db.getFiles()) {
                    controller.setFile(file);
                    filePath = file.getAbsolutePath();
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


	@Override
	public void update(Observable observable, Object o) {
		PeerData pd = (PeerData) observable;
		Map<String, Peer> peers = pd.getPeers();
		List<InetAddress> addresses = peers.entrySet().stream()
			.sorted((p1, p2) -> p1.getValue().getAddress().toString().compareTo(p2.getValue().getAddress().toString()))
			.map(peer -> peer.getValue().getAddress())
			.collect(Collectors.toList());
		controller.addTargetAddresses(addresses);
	}


    public static void main(String[] args) {
        launch(args);
    }

    public Controller getController() {
        return controller;
    }
}
