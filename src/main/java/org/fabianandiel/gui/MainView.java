package org.fabianandiel.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);

        stage.setTitle("Main");
        stage.setScene(scene);
        stage.show();
    }
}
