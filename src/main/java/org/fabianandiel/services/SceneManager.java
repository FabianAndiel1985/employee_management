package org.fabianandiel.services;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import org.fabianandiel.constants.Constants;

import java.io.IOException;


public class SceneManager {

    @Setter
    private static Stage stage;

    private SceneManager() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * Central method to switch the scene dynamically
     *
     * @param view   fxml you want to load
     * @param width  Width of the scene you want to set
     * @param height Height of the scene you want to set
     * @param title  Title of the scene
     * @throws IllegalArgumentException when the one of the arguments doesnt pass validation
     */
    public static void switchScene(String view, int width, int height, String title) throws IllegalArgumentException, IOException {
        try {

            if (title.trim().isEmpty() || title == null) {
                throw new IllegalArgumentException("Title has to be valid text");
            }

            if (!view.endsWith(".fxml")) {
                throw new IllegalArgumentException("Only valid fxml files are allowed");
            }

            FXMLLoader fxmlLoader = new FXMLLoader(SceneManager.class.getResource(view));
            Scene scene = new Scene(fxmlLoader.load(), width, height);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            StringBuilder sb = new StringBuilder("Something went wrong loading the ").append(title).append(" view");
            throw new IOException(sb.toString());
        }

    }

    /**
     * Navigates the application back to the main view by loading the corresponding FXML scene.
     * <p>
     * If loading the main view fails a  error message is displayed in the given node.
     *
     * @param errorText where error messages should be displayed
     */
    public static void goBackToMainView(Text errorText) {
        try {
            switchScene("/org/fabianandiel/gui/mainView.fxml", 400, 400, "Main");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, errorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, errorText);
        }
    }


}
