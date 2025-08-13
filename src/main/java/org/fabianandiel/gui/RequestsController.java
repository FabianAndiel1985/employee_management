package org.fabianandiel.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.controller.RequestController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.services.VacationService;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RequestsController implements Initializable {

    @FXML
    private Button requestsGoBack;

    @FXML
    private Text requestsErrorText;

    @FXML
    private TableView<Request> pendingRequestsTableView;

    @FXML
    private TableColumn<Request, LocalDate> pendingRequestsStartDate;

    @FXML
    private TableColumn<Request, LocalDate> pendingRequestsEndDate;

    @FXML
    private TableColumn<Request, String> pendingRequestsNotes;

    @FXML
    private TableColumn<Request, Person> pendingRequestsCreator;

    @FXML
    private TableColumn<Request, Void> pendingRequestsConfirm;

    @FXML
    private TableColumn<Request, Void> pendingRequestsDeny;

    private final ObservableList<Request> pendingRequestsList = FXCollections.observableArrayList();

    private RequestController requestController = new RequestController<>(new RequestDAO());

    private PersonController personController = new PersonController(new PersonDAO());

    private ExecutorService executorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Set<Person> subordinates = UserContext.getInstance().getPerson().getSubordinates();
        if (subordinates.isEmpty()) {
            GUIService.setErrorText("You dont have subordinates",this.requestsErrorText);
        }
        this.executorService = Executors.newFixedThreadPool(2);
        this.executorService.submit(() -> {
            //set pending requests, with start date in the past to expired status
            this.requestController.changeRequestStatusBeforeDate(LocalDate.now(), RequestStatus.PENDING, RequestStatus.EXPIRED);
            try {
                List<Request> pendingRequests = requestController.getRequestsOfSubordinatesByStatus(subordinates, RequestStatus.PENDING);
                Platform.runLater(() -> {
                    this.initalizeTableColumn();
                    this.pendingRequestsList.addAll(pendingRequests);
                    this.pendingRequestsTableView.setItems(this.pendingRequestsList);
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(()->GUIService.setErrorText("Error loading requests",this.requestsErrorText));
            }
        });
    }


    /**
     * Sets the vacation request to approved or disapproved
     *
     * @param request vacation request of employee
     * @param status  request status that shall be set
     */
    public void handleApproveOrDisapprove(Request request, RequestStatus status) {
        short remainingDays = VacationService.getRemainingDays(request);

        if (status == RequestStatus.ACCEPTED && remainingDays < 0) {
            GUIService.setErrorText("Employee is requesting too much vacation", this.requestsErrorText);
            return;
        }

        request.setStatus(status);
        this.executorService.submit(() -> {
                    try {
                        if (status.equals(RequestStatus.ACCEPTED)) {
                            VacationService.updateRemainingVacation(request, remainingDays, this.personController);
                        }
                        this.requestController.update(request);
                        Platform.runLater(() -> {
                            this.pendingRequestsList.remove(request);
                        });
                    } catch (Exception e) {
                        GUIService.setErrorText(e.getMessage(),this.requestsErrorText);
                        e.printStackTrace();
                    }
                }
        );
    }


    /**
     * goes back to main view
     */
    public void goBackToMainView() {
        SceneManager.goBackToMainView(requestsErrorText);
    }


    /**
     * Initializes the table columns
     */
    private void initalizeTableColumn() {
        this.pendingRequestsStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        this.pendingRequestsEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        this.pendingRequestsNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        this.pendingRequestsCreator.setCellValueFactory(new PropertyValueFactory<>("creator"));
        this.pendingRequestsCreator.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (empty || person == null) {
                    setText(null);
                } else {
                    setText(person.getFirstname() + " " + person.getLastname());
                }
            }
        });
        this.pendingRequestsConfirm.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Approve");

            {
                btn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleApproveOrDisapprove(request, RequestStatus.ACCEPTED);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        this.pendingRequestsDeny.setCellFactory(column -> new TableCell<>() {
            private final Button btn = new Button("Disapprove");

            {
                btn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleApproveOrDisapprove(request, RequestStatus.DENIED);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }
}
