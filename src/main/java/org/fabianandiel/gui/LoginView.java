package org.fabianandiel.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;

public class LoginView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            SceneManager.setStage(stage);
            SceneManager.switchScene("/org/fabianandiel/gui/loginMenue.fxml", 400, 400, "Login");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            System.exit(0);
        }

    }
}
