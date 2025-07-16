package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.RequestController;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.services.ValidatorProvider;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Set;

public class VacationsController implements Initializable {

    @FXML
    private Button vacationsGoBack;

    @FXML
    private Text vacationsErrorText;

    @FXML
    private Button vacationsSubmit;

    @FXML
    private DatePicker vacationsStartDate;

    @FXML
    private DatePicker vacationsEndDate;

    @FXML
    private TextField vacationsNotes;

    private RequestController requestController = new RequestController<>(new RequestDAO());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,vacationsErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.vacationsErrorText);
        }
    }

    /**
     * Validates and submits the request to be written into the DB
     */
    public void submit(){
        Request request = new Request();
        fillRequestObject(request);

        Set<ConstraintViolation<Request>> violations = ValidatorProvider.getValidator().validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation<Request> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), this.vacationsErrorText);
            return;
        }
        this.vacationsErrorText.setVisible(false);

        LocalDate startDate= request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if(!endDate.isAfter(startDate) && !startDate.equals(endDate) ) {
            GUIService.setErrorText("End date has to be after start date", this.vacationsErrorText);
            return;
        }

        //TODO check if there has been already a booking for the wanted dates

        //TODO error handling
        this.requestController.create(request);
        //TODO - show in the above
        System.out.println(request);
        UserContext.getInstance().getPerson().getRequests().add(request);
    }


    private void fillRequestObject(Request request) {
        request.setStatus(RequestStatus.PENDING);
        request.setCreationDate(LocalDateTime.now());
        request.setCreator(UserContext.getInstance().getPerson());
        request.setStartDate(this.vacationsStartDate.getValue());
        request.setEndDate(this.vacationsEndDate.getValue());
        request.setNotes(this.vacationsNotes.getText());
    }


}
