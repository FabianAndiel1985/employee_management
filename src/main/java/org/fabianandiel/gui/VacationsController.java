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
import org.fabianandiel.controller.RequestController;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
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


    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.vacationsErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.vacationsErrorText);
        }
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
            try {
                this.requestController.create(request);
                Platform.runLater(() -> {
                    this.requestList.add(request);
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

        if (this.requestController.getRequestsByStartDate(startDate).size() > 0) {
            GUIService.setErrorText("You can not submit two requests with the same start date", this.vacationsErrorText);
            return false;
        }

        long totalDaysOfCurrentRequest = endDate.toEpochDay() - startDate.toEpochDay() + 1;

        List<Request> pastRequests = this.requestController.getRequestsByStatus(RequestStatus.ACCEPTED,RequestStatus.PENDING);

        long totalDaysOfPastRequest= pastRequests == null || pastRequests.size() == 0 ? 0 : this.calculateDaysOfPastRequests(pastRequests);


        if(UserContext.getInstance().getPerson().getVacation_entitlement()-totalDaysOfCurrentRequest-totalDaysOfPastRequest <= 0)  {
            GUIService.setErrorText("You're asking fore more holiday than you're entitled to", this.vacationsErrorText);
            return false;
        }

        return true;
    }

    private long calculateDaysOfPastRequests(List<Request> pastRequests) {
            return pastRequests.stream()
                    .mapToLong(req -> req.getEndDate().toEpochDay() - req.getStartDate().toEpochDay() + 1)
                    .sum();
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
