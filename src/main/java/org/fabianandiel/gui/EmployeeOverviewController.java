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

public class EmployeeOverviewController implements Initializable {

    @FXML
    private Button employeesGoBack;

    @FXML
    private Text employeeOverviewErrorText;

    @FXML
    private Button employeeOverviewNewEmployee;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void goToCreateEmployee() {
        try {
            System.out.println("Goes here");
            SceneManager.switchScene("/org/fabianandiel/gui/createEmployeeView.fxml",530,607,"Create Employee");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,employeeOverviewErrorText);
        }
    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,employeeOverviewErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,employeeOverviewErrorText);
        }
    }
}
