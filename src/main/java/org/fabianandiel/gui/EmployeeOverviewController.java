package org.fabianandiel.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeOverviewController implements Initializable {

    @FXML
    private Button employeesGoBack;

    @FXML
    private Text employeeOverviewErrorText;

    @FXML
    private Button employeeOverviewNewEmployee;

    @FXML
    private TableView<Person> employeeOverviewAllEmployees;

    @FXML
    private TableColumn employeeOverviewId;

    @FXML
    private TableColumn employeeOverviewFirstname;

    @FXML
    private TableColumn employeeOverviewLastname;

    @FXML
    private TableColumn employeeOverviewSuperior;

    @FXML
    private TableView employeeOverviewSubordinates;

    @FXML
    private TableColumn employeeOverviewRTMid;

    @FXML
    private TableColumn employeeOverviewRTMfirstname;

    @FXML
    private TableColumn employeeOverviewRTMlastname;

    @FXML
    private TableColumn employeeOverviewRTMaddress;

    @FXML
    private TableColumn employeeOverviewRTMstatus;

    private ObservableList<Person> allEmployees = FXCollections.observableArrayList();

    private ObservableList<Person> employeesReportingToMe= FXCollections.observableArrayList();

    private PersonController personController= new PersonController(new PersonDAO());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAllEmployees();
        initalizeEmployeesReportingToUser();
    }


    private void initalizeEmployeesReportingToUser() {
        this.employeeOverviewSubordinates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.employeeOverviewRTMid.setCellValueFactory(new PropertyValueFactory<Person, String>("id"));
        this.employeeOverviewRTMfirstname.setCellValueFactory(new PropertyValueFactory<Person, String>("firstname"));
        this.employeeOverviewRTMlastname.setCellValueFactory(new PropertyValueFactory<Person, String>("lastname"));
        this.employeeOverviewRTMaddress.setCellValueFactory(new PropertyValueFactory<Person, String>("address"));
        this.employeeOverviewRTMstatus.setCellValueFactory(new PropertyValueFactory<Person, String>("status"));
        List<Person> personsWithUserAsSuperior = this.allEmployees.stream()
                .filter((empl) -> {
                    if (empl.getSuperior() != null) {
                        return empl.getSuperior().getId().equals(UserContext.getInstance().getId());
                    }
                    return false;
                })
                .toList();
        this.employeesReportingToMe.addAll(personsWithUserAsSuperior);
        this.employeeOverviewSubordinates.setItems(this.employeesReportingToMe);
    }

    private void initializeAllEmployees() {
        this.employeeOverviewAllEmployees.getSelectionModel().setSelectionMode(null);
        this.employeeOverviewId.setCellValueFactory(new PropertyValueFactory<Person, String>("id"));
        this.employeeOverviewFirstname.setCellValueFactory(new PropertyValueFactory<Person, String>("firstname"));
        this.employeeOverviewLastname.setCellValueFactory(new PropertyValueFactory<Person, String>("lastname"));
        this.employeeOverviewSuperior.setCellValueFactory(new PropertyValueFactory<>("superior"));
        this.employeeOverviewSuperior.setCellFactory(column -> new TableCell<Person, Person>() {
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
        List<Person> allEmployees = personController.getAll(Person.class);
        this.allEmployees.addAll(allEmployees);
        this.employeeOverviewAllEmployees.setItems(this.allEmployees);
    }

    public void goToCreateEmployee() {
        try {
            System.out.println("Goes here");
            SceneManager.switchScene("/org/fabianandiel/gui/createEmployeeView.fxml", 530, 607, "Create Employee");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        }
    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        }
    }
}
