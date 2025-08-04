package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.constants.Role;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.controller.RequestController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.services.VacationService;
import org.fabianandiel.services.ValidatorProvider;
import javafx.application.Platform;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VacationsController implements Initializable {

    @FXML
    private Text vacationsRequestVacationEntitlement;

    @FXML
    private Text vacationsRequestRestVacation;

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


    @FXML
    private TableView<Request> vacationsRequestTable;

    @FXML
    private TableColumn<Request, LocalDate> vacationsStartDateColumn;

    @FXML
    private TableColumn<Request, LocalDate> vacationsEndDateColumn;

    @FXML
    private TableColumn<Request, String> vacationsNotesColumn;

    @FXML
    private TableColumn<Request, RequestStatus> vacationsStatusColumn;

    @FXML
    private Text vacationsText;

    private RequestController requestController = new RequestController<>(new RequestDAO());

    private PersonController personController = new PersonController(new PersonDAO());

    private final ObservableList<Request> requestList = FXCollections.observableArrayList();

    private ExecutorService executorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initalizeTableColumn();
        if (isManagerOrAdmin()) {
            this.vacationsText.setText("Book your vacation");
        }
        executorService = Executors.newFixedThreadPool(2);
        executorService.submit(() -> {
            try {
                //set pending requests, with start date in the past to expired status
                this.requestController.changeRequestStatusBeforeDate(LocalDate.now(), RequestStatus.PENDING, RequestStatus.EXPIRED);
                this.vacationsRequestVacationEntitlement.setText(String.valueOf(UserContext.getInstance().getPerson().getVacation_entitlement()));
                this.vacationsRequestRestVacation.setText(String.valueOf(UserContext.getInstance().getPerson().getVacation_remaining()));
                Platform.runLater(() -> {
                    this.requestList.addAll(this.requestController.getRequestsByCreator(UserContext.getInstance().getId()));
                    this.vacationsRequestTable.setItems(this.requestList);
                });
            } catch (Exception e) {
                //TODO Error handling mit Fehlermeldung für User
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * goes back to main view
     */
    public void goBackToMainView() {
        SceneManager.goBackToMainView(this.vacationsErrorText);
    }

    /**
     * Validates and submits the request to be written into the DB
     */
    public void submit() {
        Request request = new Request();
        fillRequestObject(request);

        if (!validateRequest(request)) return;

        UserContext.getInstance().getPerson().getRequests().add(request);

        executorService.submit(() -> {
            Short remainingDays=null;
            String remainingDaysText=null;
            if(isManagerOrAdmin()) {
                remainingDays = VacationService.getRemainingDays(request);
                remainingDaysText = String.valueOf(remainingDays);
            }
            final String remainingDaysTextFinal = remainingDaysText;

            try {
                this.requestController.create(request);

                Platform.runLater(() -> {
                    this.requestList.add(request);
                    if(isManagerOrAdmin() && remainingDaysTextFinal  != null) {
                        this.vacationsRequestRestVacation.setText(remainingDaysTextFinal);
                    }
                });
            } catch (Exception e) {
                //TODO add an error text
                throw new RuntimeException(e);
            }
        });
        //TODO error handling
    }

    /**
     * Checks if the current user is a manager
     *
     * @return true if user is at least a manager, false if not
     */
    private boolean isManagerOrAdmin() {
        return UserContext.getInstance().hasRole(Role.EMPLOYEE) && UserContext.getInstance().hasRole(Role.MANAGER);
    }


    /**
     * checks if the request is valid
     *
     * @param request the request to be checked
     * @return if the request is valid
     */
    private boolean validateRequest(Request request) {
        Set<ConstraintViolation<Request>> violations = ValidatorProvider.getValidator().validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation<Request> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), this.vacationsErrorText);
            return false;
        }
        this.vacationsErrorText.setVisible(false);

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (!endDate.isAfter(startDate) && !startDate.equals(endDate)) {
            GUIService.setErrorText("End date has to be after start date", this.vacationsErrorText);
            return false;
        }

        List<Request> pastRequests = this.requestController.getRequestsByStatus(RequestStatus.ACCEPTED, RequestStatus.PENDING);

        if (pastRequests != null && this.checkIfStartDateOrEndDateIsInPastRequestRange(startDate, endDate, pastRequests)) {
            GUIService.setErrorText("The start date or end date can not lie in the range of a past request", this.vacationsErrorText);
            return false;
        }

        short totalDaysOfCurrentRequest = (short) (endDate.toEpochDay() - startDate.toEpochDay() + 1);

        short totalDaysOfPastRequest = pastRequests == null || pastRequests.size() == 0 ? 0 : this.calculateDaysOfPastRequests(pastRequests);
        //TODO take remaining days calculate from here and update admin and manager
        if (UserContext.getInstance().getPerson().getVacation_remaining() - totalDaysOfCurrentRequest - totalDaysOfPastRequest <= 0) {
            GUIService.setErrorText("You're asking fore more holiday than you're entitled to", this.vacationsErrorText);
            return false;
        }

        return true;
    }


    /**
     * check if the CURRENT request lies within past requests
     *
     * @param startDate start date of current request
     * @param endDate   end date of current request
     * @return returns true if the request lies in the past range and false if not
     */
    private boolean checkIfStartDateOrEndDateIsInPastRequestRange(LocalDate startDate, LocalDate endDate, List<Request> pastRequests) {
        for (Request request : pastRequests) {
            LocalDate pastRequestStartDate = request.getStartDate();
            LocalDate pastRequestEndDate = request.getEndDate();
            if ((pastRequestStartDate.isBefore(startDate) && pastRequestEndDate.isAfter(startDate)) || (pastRequestStartDate.isBefore(endDate) && pastRequestEndDate.isAfter(endDate))) {
                return true;
            }
        }
        return false;
    }


    /**
     * sums up the sum of days of the past requests that have either been accepted or rejected
     *
     * @param pastRequests requests of the past that have either been accepted or rejected
     * @return the amount of days of the past requests
     */
    private short calculateDaysOfPastRequests(List<Request> pastRequests) {
        long totalDays = pastRequests.stream()
                .mapToLong(req -> req.getEndDate().toEpochDay() - req.getStartDate().toEpochDay() + 1)
                .sum();

        return (short) totalDays;
    }


    private void fillRequestObject(Request request) {
        request.setStatus(isManagerOrAdmin() ? RequestStatus.ACCEPTED : RequestStatus.PENDING);
        request.setCreationDate(LocalDateTime.now());
        request.setCreator(UserContext.getInstance().getPerson());
        request.setStartDate(this.vacationsStartDate.getValue());
        request.setEndDate(this.vacationsEndDate.getValue());
        request.setNotes(this.vacationsNotes.getText());
    }

    /**
     * initalizes table columns where user sees his requests
     */
    private void initalizeTableColumn() {
        this.vacationsStartDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        this.vacationsEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        this.vacationsNotesColumn.setCellValueFactory(new PropertyValueFactory<>("notes"));
        this.vacationsStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }


}
