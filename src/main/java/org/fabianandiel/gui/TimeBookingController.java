package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.Status;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.TimeStampController;
import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;


import java.io.IOException;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.UUID;

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

    private TimeStampController timeStampController = new TimeStampController(new TimeStampDAO());



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.timeBookingStartTime.clear();

        TimeStamp existingStamp = timeStampController.findTimeStampByDate(LocalDate.now(), UserContext.getInstance().getId());
        if (existingStamp != null) {
            if(existingStamp.getTimeBookingStartTime() != null)
                this.timeBookingStartTime.setText(existingStamp.getTimeBookingStartTime().toString());

            if(existingStamp.getTimeBookingEndTime() != null)
            this.timeBookingEndTime.setText(existingStamp.getTimeBookingEndTime().toString());
        }
        else  {
            TimeStamp timeStamp = new TimeStamp();
            timeStamp.setTimeBookingDate(LocalDate.now());
            timeStamp.setPerson(UserContext.getInstance().getPerson());
            this.timeStampController.create(timeStamp);
        }
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
            TimeStamp existingStamp = timeStampController.findTimeStampByDate(LocalDate.now(), UserContext.getInstance().getId());
        }



        //Todo get timestamp by id
        //Todo write timestamp in the database
        //ToDo change person state to attending
    }

    public void clockOut() {

        if(!timeBookingStartTime.getText().isEmpty()) {
            timeBookingStartTime.setText(LocalDateTime.now().toString());
            //TODO update status
        }

        //TODO Validate Clock in must have happened
        //TODO calculate working hours

        //ToDo change person state to absent
    }



}
