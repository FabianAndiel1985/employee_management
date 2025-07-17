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

    private RequestController requestController = new RequestController<>(new RequestDAO());

    private final ObservableList<Request> requestList = FXCollections.observableArrayList();

    private ExecutorService executorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            executorService = Executors.newFixedThreadPool(2);
            executorService.submit(()->{
            try{
                initalizeTableColumn();
                Platform.runLater(() -> {
                    this.requestList.addAll(requestController.getRequestsByCreator(UserContext.getInstance().getId()));
                    this.vacationsRequestTable.setItems(this.requestList);
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }



    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.vacationsErrorText);
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
        UserContext.getInstance().getPerson().getRequests().add(request);
        executorService.submit(()->{
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


        //TODO check if there has been already a booking for the wanted dates

        //TODO error handling

        System.out.println(request);

    }


    private void fillRequestObject(Request request) {
        request.setStatus(RequestStatus.PENDING);
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
