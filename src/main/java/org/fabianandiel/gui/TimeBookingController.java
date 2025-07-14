package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TimeBookingController implements Initializable {

    @FXML
    private Button timeBookingGoBack;

    @FXML
    private Button  timeBookingClockIn;

    @FXML
    private Button timeBookingClockOut;

    @FXML
    private Text timeBookingErrorText;

    @FXML
    private TextField timeBookingStartTime;

    @FXML
    private TextField timeBookingEndTime;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.timeBookingStartTime.clear();
        this.timeBookingEndTime.clear();
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

    public void clockIn() {
        if(timeBookingStartTime.getText().isEmpty()) {
            timeBookingStartTime.setText(LocalDateTime.now().toString());
        }


        //ToDo change person state to attending
    }

    public void clockOut() {

        if(!timeBookingStartTime.getText().isEmpty()) {
            timeBookingStartTime.setText(LocalDateTime.now().toString());
        }

        //TODO Validate Clock in must have happened
        //TODO calculate working hours

        //ToDo change person state to absent
    }



}
