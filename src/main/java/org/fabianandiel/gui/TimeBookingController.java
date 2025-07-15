package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;

import org.fabianandiel.constants.Status;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.controller.TimeStampController;

import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;


import java.io.IOException;
import java.net.URL;

import java.time.LocalDate;
import java.time.LocalTime;
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

    private TimeStamp currentTimeStamp;

    private PersonController personController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.timeBookingStartTime.clear();
        this.timeBookingEndTime.clear();

        this.personController = new PersonController<>(new PersonDAO());

        TimeStamp existingStamp = timeStampController.findTimeStampByDate(LocalDate.now(), UserContext.getInstance().getId());
        if (existingStamp != null) {
            this.currentTimeStamp = existingStamp;
            if(existingStamp.getTimeBookingStartTime() != null)
                this.timeBookingStartTime.setText(existingStamp.getTimeBookingStartTime().toString());

            if(existingStamp.getTimeBookingEndTime() != null)
            this.timeBookingEndTime.setText(existingStamp.getTimeBookingEndTime().toString());

            if(!this.timeBookingStartTime.getText().isEmpty()) {
                this.timeBookingClockIn.setDisable(true);
            }

            if(!this.timeBookingEndTime.getText().isEmpty()) {
                this.timeBookingClockOut.setDisable(true);
            }

        }
        else  {
            TimeStamp timeStamp = new TimeStamp();
            timeStamp.setTimeBookingDate(LocalDate.now());
            timeStamp.setPerson(UserContext.getInstance().getPerson());
            this.currentTimeStamp = timeStamp;
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
        if(timeBookingStartTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingStartTime() == null) {
            LocalTime time = LocalTime.now();
            this.currentTimeStamp.setTimeBookingStartTime(time);
            timeBookingStartTime.setText(time.toString());
            //TODO error handling here
            timeStampController.update(currentTimeStamp);
            timeBookingClockIn.setDisable(true);
            UserContext.getInstance().getPerson().setStatus(Status.PRESENT);
            personController.update(UserContext.getInstance().getPerson());
        }
    }

    public void clockOut() {

        if(!timeBookingStartTime.getText().isEmpty() && timeBookingEndTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingEndTime() == null ) {
            LocalTime time = LocalTime.now();
            this.currentTimeStamp.setTimeBookingEndTime(time);
            timeBookingEndTime.setText(time.toString());
            //TODO error handling here
            timeStampController.update(currentTimeStamp);
            timeBookingClockOut.setDisable(true);
            UserContext.getInstance().getPerson().setStatus(Status.ABSENT);
            personController.update(UserContext.getInstance().getPerson());
        }


        //TODO calculate working hours


    }



}
