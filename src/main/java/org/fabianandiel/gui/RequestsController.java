package org.fabianandiel.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
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

    private final ObservableList<Request> pendingRequestsList = FXCollections.observableArrayList();

    private RequestController requestController = new RequestController<>(new RequestDAO());


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO get all the pending requests of all subordintes

        Set<Person> subordinates = UserContext.getInstance().getPerson().getSubordinates();

        if(subordinates.isEmpty()){
            //TODO show error text that no subordinates are found
            return;
        }

        //TODO buttons adden

        List<Request> pendingRequests = requestController.getPendingRequestsOfSubordinates(subordinates);

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
