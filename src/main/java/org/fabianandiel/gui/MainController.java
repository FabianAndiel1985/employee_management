package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button mainTime;

    @FXML
    private Button mainVacation;

    @FXML
    private Button mainRequests;

    @FXML
    private Button mainEmployees;

    @FXML
    private Button mainLogout;

    @FXML
    private HBox mainManagerAdminRow;


    @FXML
    private Text mainErrorText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //TODO remove this trial code
        List<String> roles = new ArrayList<>();

        if(roles.contains("employee") && roles.size() ==1 ) {
            this.mainManagerAdminRow.setVisible(false);
            this.mainManagerAdminRow.setManaged(false);
        }
    }


    //TODO Make it a switch statement
    public void switchToVacation() {
        this.switchView("/org/fabianandiel/gui/vacationsView.fxml",400,400,"Vacations");
    }


    public void switchToTimeBooking() {
        this.switchView("/org/fabianandiel/gui/timeBookingView.fxml",400,400,"Time Booking");
    }

    public void switchToRequests() {
        this.switchView("/org/fabianandiel/gui/requestsView.fxml",400,400,"Pending Requests");
    }

    public void switchToEmployeeOverview() {
        this.switchView("/org/fabianandiel/gui/employeeOverviewView.fxml",400,400,"Employee Overview");
    }


    public void switchToLoginView() {
        //TODO set the User login object to null
        this.switchView("/org/fabianandiel/gui/loginView.fxml",400,400,"Login");
    }

    private void switchView(String view, int width, int height, String title){
        try {
            SceneManager.switchScene(view, width, height, title);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,mainErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,mainErrorText);
        }
    }

}
