package org.fabianandiel.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Setter;

import java.io.IOException;
import java.nio.channels.IllegalChannelGroupException;

public class SceneManager {

    @Setter
    private static Stage stage;

    /**
     * Central method to switch the scene dynamically
     * @param view fxml you want to load
     * @param width Width of the scene you want to set
     * @param height Height of the scene you want to set
     * @param title Title of the scene
     * @throws IllegalArgumentException when the one of the arguments doesnt pass validation
     */
    public static void switchScene(String view, int width,int height, String title) throws IllegalArgumentException,IOException {
        try {
            //Validation
            if(width >= 601 || width <= 199)
                throw new IllegalArgumentException("Width has to be mimum 200 and maximum 600");

            if( height != width)
                throw new IllegalArgumentException("Height has to be equal to width");

            if(title.trim().isEmpty() || title == null) {
                throw new IllegalArgumentException("Title has to be valid text");
            }

            if(!view.endsWith(".fxml")) {
                throw new IllegalArgumentException("Only valid fxml files are allowed");
            }


            //Todo Check if the title file exists in resource directory - usage of file system classes

            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(view));
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            StringBuilder sb = new StringBuilder("Something went wrong loading the ").append(title).append(" view");
            throw new IOException(sb.toString());
        }

    }

}
