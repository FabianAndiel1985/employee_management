package org.fabianandiel.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;

@Setter
public class SceneManager {

    private static Stage stage;

    public static void switchScene(String view, int width,int height, String title) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(view));
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
