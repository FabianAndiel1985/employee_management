package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TimeBookingController implements Initializable {

    @FXML
    private Button timeBookingGoBack;

    @FXML
    private Text timeBookingErrorText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,timeBookingErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,timeBookingErrorText);
        }
    }
}
