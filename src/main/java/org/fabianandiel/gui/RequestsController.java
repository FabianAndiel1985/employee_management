package org.fabianandiel.gui;

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
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.RequestController;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    private TableColumn<Request,Void> pendingRequestsConfirm;

    @FXML
    private TableColumn<Request,Void> pendingRequestsDeny;



    private final ObservableList<Request> pendingRequestsList = FXCollections.observableArrayList();

    private RequestController requestController = new RequestController<>(new RequestDAO());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Set<Person> subordinates = UserContext.getInstance().getPerson().getSubordinates();
        if(subordinates.isEmpty()){
            //TODO show error text that no subordinates are found
            return;
        }
        initalizeTableColumn();
        List<Request> pendingRequests = requestController.getRequestsOfSubordinatesByStatus(subordinates, RequestStatus.PENDING);
        this.pendingRequestsList.addAll(pendingRequests);
        this.pendingRequestsTableView.setItems(this.pendingRequestsList);
    }

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
        this.pendingRequestsConfirm.setCellFactory(column-> new TableCell<>(){
            private final Button btn = new Button("Approve");

            {
                btn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleApprove(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        this.pendingRequestsDeny.setCellFactory(column-> new TableCell<>(){
            private final Button btn = new Button("Disapprove");

            {
                btn.setOnAction(event -> {
                    Request request = getTableView().getItems().get(getIndex());
                    handleDisapprove(request);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        //TODO buttons addens

    }

    private void handleDisapprove(Request request) {
        //TODO change the status of the request to declined in the DB
        //TODO update the UI accordingly - remove request from ObservableList
    }

    private void handleApprove(Request request) {
        //TODO change the status of the request to accepted in the DB
        //TODO make also time booking according to that
        //TODO update the UI accordingly - remove request from ObservableList
    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,requestsErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,requestsErrorText);
        }
    }
}
