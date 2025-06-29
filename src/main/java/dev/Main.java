package dev;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Main extends Application {

    private final VBox groupContainer = new VBox(10);
    private final FaceRecognizer recognizer = new FaceRecognizer();
    private final FunnyNameGenerator nameGenerator = new FunnyNameGenerator();
    private final Map<String, FlowPane> groupPanes = new HashMap<>();


    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Label dropLabel = new Label("Drop Images Here");
        dropLabel.setFont(new Font(20));
        dropLabel.setStyle("-fx-border-color: gray; -fx-padding: 40;");
        dropLabel.setAlignment(Pos.CENTER);

        VBox dropZone = new VBox(dropLabel);
        dropZone.setAlignment(Pos.CENTER);
        dropZone.setPadding(new Insets(20));
        dropZone.setStyle("-fx-background-color: #f0f0f0;");
        dropZone.setOnDragOver(event -> {
            if (event.getGestureSource() != dropZone && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropZone.setOnDragDropped(event -> {
            List<File> files = event.getDragboard().getFiles();
            processImages(files);
            event.setDropCompleted(true);
            event.consume();
        });

        dropZone.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Images");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
            );
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
            if (selectedFiles != null) {
                processImages(selectedFiles);
            }
        });

        ScrollPane scrollPane = new ScrollPane(groupContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(dropZone);
        root.setCenter(scrollPane);

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Face Grouper");
        stage.setScene(scene);
        stage.show();

    }

    private void processImages(List<File> files) {
        Map<String, List<Image>> newImagesByGroup = new HashMap<>();

        for (File file : files) {
            try {
                byte[] bytes = Files.readAllBytes(file.toPath());
                byte[] normalizedImage = recognizer.getNormalizedImage(bytes);
                Optional<String> groupIdOpt = recognizer.detectAndClassify(bytes);

                if (groupIdOpt.isEmpty()) {
                    showAlert("No face detected in: " + file.getName());
                    continue;
                }

                String groupId = groupIdOpt.get();
                Image img = new Image(new ByteArrayInputStream(normalizedImage));
                Image img2 = new Image(new ByteArrayInputStream(bytes));
                newImagesByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(img);
                newImagesByGroup.computeIfAbsent(groupId, k -> new ArrayList<>()).add(img2);

            } catch (IOException e) {
                showAlert("Failed to read file: " + file.getName());
            }
        }

        for (Map.Entry<String, List<Image>> entry : newImagesByGroup.entrySet()) {
            String groupId = entry.getKey();
            List<Image> images = entry.getValue();

            if (groupPanes.containsKey(groupId)) {
                FlowPane thumbPane = groupPanes.get(groupId);
                for (Image img : images) {
                    ImageView iv = new ImageView(img);
                    iv.setFitWidth(100);
                    iv.setPreserveRatio(true);
                    thumbPane.getChildren().add(iv);
                }
            } else {
                addGroup(groupId, images, nameGenerator.next());
            }
        }
    }

    private void addGroup(String groupId, List<Image> images, String name) {
        Label nameLabel = new Label(name);
        TextField nameEditor = new TextField(name);
        nameEditor.setVisible(false);

        Button renameBtn = new Button("✏");
        renameBtn.setOnAction(e -> {
            nameLabel.setVisible(false);
            nameEditor.setText(nameLabel.getText());
            nameEditor.setVisible(true);
            nameEditor.requestFocus();
        });

        nameEditor.setOnAction(e -> {
            nameLabel.setText(nameEditor.getText());
            nameLabel.setVisible(true);
            nameEditor.setVisible(false);
        });

        HBox titleBar = new HBox(10, nameLabel, nameEditor, renameBtn);
        titleBar.setAlignment(Pos.CENTER_LEFT);

        FlowPane thumbPane = new FlowPane(10, 10);
        for (Image img : images) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(100);
            iv.setPreserveRatio(true);
            thumbPane.getChildren().add(iv);
        }

        groupPanes.put(groupId, thumbPane);

        VBox groupBox = new VBox(10, titleBar, thumbPane);
        groupBox.setPadding(new Insets(10));
        groupBox.setStyle("-fx-border-color: #ccc; -fx-background-color: #fafafa;");
        groupContainer.getChildren().add(groupBox);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.showAndWait();
    }

}
