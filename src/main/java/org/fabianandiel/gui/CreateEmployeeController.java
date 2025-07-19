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

public class CreateEmployeeController implements Initializable {

    @FXML
    private Button createEmployeeGoBack;

    @FXML
    private Text createEmployeeErrorText;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void goBackEmployeeOverview() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/employeeOverviewView.fxml",530,671,"Employee Overview");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.createEmployeeErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.createEmployeeErrorText);
        }
    }
}
