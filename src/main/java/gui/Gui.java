package gui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;

import java.io.File;

public class Gui extends Application {
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


    public static void main(String[] args) {
        launch(args);
    }

    public Controller getController() {
        return controller;
    }
}
