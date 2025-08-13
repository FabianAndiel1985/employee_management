package org.fabianandiel.gui;

import java.util.function.Consumer;
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
import org.fabianandiel.services.*;
import javafx.application.Platform;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;


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
        this.executorService = ExecutorServiceProvider.getExecutorService();
        this.executorService.submit(() -> {
            try {
                //set pending requests, with start date in the past to expired status
                this.requestController.changeRequestStatusBeforeDate(LocalDate.now(), RequestStatus.PENDING, RequestStatus.EXPIRED);
                List<Request> requests = this.requestController.getRequestsByCreator(UserContext.getInstance().getId());
                Platform.runLater(() -> {
                    initalizeTableColumn();
                    this.vacationsRequestVacationEntitlement.setText(String.valueOf(UserContext.getInstance().getPerson().getVacation_entitlement()));
                    this.vacationsRequestRestVacation.setText(String.valueOf(UserContext.getInstance().getPerson().getVacation_remaining()));
                    if (isManagerOrAdmin()) {
                        this.vacationsText.setText("Book your vacation");
                    }
                    this.requestList.addAll(requests);
                    this.vacationsRequestTable.setItems(this.requestList);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> GUIService.setErrorText(e.getMessage(), this.vacationsErrorText));
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
        this.fillRequestObject(request);

        this.validateRequest(request, (isValid) -> {
            this.handleValidationResult(request,isValid);
        });
    }


    /**
     * submits the result of the validation
     * @param request request for vacation
     * @param isValid result of the validation
     */
    private void handleValidationResult(Request request, boolean isValid) {
        if(!isValid) return;
        Short remainingDays = null;
        String remainingDaysText = null;
        if (this.isManagerOrAdmin()) {
            remainingDays = VacationService.getRemainingDays(request);
            remainingDaysText = String.valueOf(remainingDays);
        }
        final String remainingDaysTextFinal = remainingDaysText;

        executorService.submit(() -> {

            try {
                this.requestController.create(request);
                UserContext.getInstance().getPerson().getRequests().add(request);
                Platform.runLater(() -> {
                    this.requestList.add(request);
                    if (isManagerOrAdmin() && remainingDaysTextFinal != null) {
                        this.vacationsRequestRestVacation.setText(remainingDaysTextFinal);
                    }
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.vacationsErrorText);
                });
            }
        });
    }

    /**
     * Checks if the current user is manager or admin
     *
     * @return true if user is at least a manager, false if not
     */
    private boolean isManagerOrAdmin() {
        return UserContext.getInstance().hasRole(Role.MANAGER) || UserContext.getInstance().hasRole(Role.ADMIN);
    }


    /**
     * validates the request
     *
     * @param request request to be validated
     */
    private void validateRequest(Request request, Consumer<Boolean> onComplete) {
        Set<ConstraintViolation<Request>> violations = ValidatorProvider.getValidator().validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation<Request> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), this.vacationsErrorText);
            onComplete.accept(false);
            return;
        }
        this.vacationsErrorText.setVisible(false);

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        if (!endDate.isAfter(startDate) && !startDate.equals(endDate)) {
            GUIService.setErrorText("End date has to be after start date", this.vacationsErrorText);
            onComplete.accept(false);
            return;
        }

        executorService.submit(() -> {
            try {
                List<Request> pastRequests = this.requestController.getRequestsByStatus(RequestStatus.ACCEPTED, RequestStatus.PENDING);

                if (pastRequests != null && this.checkIfStartDateOrEndDateIsInPastRequestRange(startDate, endDate, pastRequests)) {
                   Platform.runLater(()->GUIService.setErrorText("The start date or end date can not lie in the range of a past request", this.vacationsErrorText));
                    onComplete.accept(false);
                    return;
                }

                short totalDaysOfCurrentRequest = (short) (endDate.toEpochDay() - startDate.toEpochDay() + 1);

                short totalDaysOfPastRequest = pastRequests == null || pastRequests.size() == 0 ? 0 : this.calculateDaysOfPastRequests(pastRequests);
                if (UserContext.getInstance().getPerson().getVacation_remaining() - totalDaysOfCurrentRequest - totalDaysOfPastRequest <= 0) {
                   Platform.runLater(()->GUIService.setErrorText("You're asking fore more holiday than you're entitled to", this.vacationsErrorText)); ;
                    onComplete.accept(false);
                    return;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
                Platform.runLater(()->GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.vacationsErrorText));
                onComplete.accept(false);
                return;
            }
            onComplete.accept(true);
        });
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
