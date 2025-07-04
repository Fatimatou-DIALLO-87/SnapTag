package com.example.projet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.util.Objects;

import javafx.scene.image.Image;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Charger le fichier FXML
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        StackPane root = fxmlLoader.load();

        // Créer la scène avec un layout AnchorPane
        Scene scene = new Scene(root, 900, 600);

        //fixer les tailles minimale et maximale de la fenetre
        stage.setMinWidth(700);

        stage.setMinHeight(680);
        stage.setMaxHeight(750);

        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("logo/logo.png")));
        stage.getIcons().add(img);


        // Configurer la fenêtre (Stage)
        stage.setTitle("SnapTag");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}