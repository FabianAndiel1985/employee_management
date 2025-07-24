package org.fabianandiel.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.Role;
import org.fabianandiel.context.UpdateContext;
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
    private Button employeeOverviewDeleteEmployee;

    @FXML
    private Button employeeOverviewUpdateEmployee;


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
    private TableView<Person> employeeOverviewUpdateableEmployees;

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

    @FXML
    private Text employeeOverviewText;

    private ObservableList<Person> allEmployees = FXCollections.observableArrayList();

/*
    private ObservableList<Person> employeesReportingToMe= FXCollections.observableArrayList();

    //Only used for Admin
    private ObservableList<Person> employeesReportingToMeAndManagers= FXCollections.observableArrayList();


 */
    private ObservableList<Person> updateableEmployees= FXCollections.observableArrayList();


    private PersonController personController= new PersonController(new PersonDAO());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeAllEmployees();
        initializeUpdateableEmployees();
        this.employeeOverviewUpdateEmployee.setDisable(true);
        if (UserContext.getInstance().hasRole(Role.ADMIN)) {
            this.employeeOverviewText.setText("Managers and Admins");
        }
    }

    private void handleRowSelection(Person person) {
        this.employeeOverviewUpdateEmployee.setDisable(false);
    }


    /**
     * Shows all employees reporting to user
     */
    private void initializeUpdateableEmployees() {
        this.employeeOverviewUpdateableEmployees.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
        //Includes the managers and other admins to table view that they can be updated by the logged in admin
        if(UserContext.getInstance().hasRole(Role.ADMIN)) {
            List<Person> managersAndAdmins = this.allEmployees.stream().filter((person) -> {
                return (person.getRoles().contains(Role.MANAGER) || person.getRoles().contains(Role.ADMIN)) && !person.getId().equals(UserContext.getInstance().getId());
            }).toList();
            this.updateableEmployees.addAll(managersAndAdmins);
        }
        //Includes only the persons reporting to loggedIn manager
        else {
            this.updateableEmployees.addAll(personsWithUserAsSuperior);
        }
        this.employeeOverviewUpdateableEmployees.setItems(this.updateableEmployees);

        this.employeeOverviewUpdateableEmployees.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        handleRowSelection(newValue);
                    }
                    else {
                        this.employeeOverviewUpdateEmployee.setDisable(true);
                    }
                });
    }



    public void onUpdate() {
    ObservableList<Person> selectedPersons = this.employeeOverviewUpdateableEmployees.getSelectionModel().getSelectedItems();
    if(selectedPersons.size() == 0 || selectedPersons.size() > 1) {
        this.employeeOverviewUpdateEmployee.setDisable(true);
        return;
    }
        this.employeeOverviewUpdateEmployee.setDisable(false);
        Person person = selectedPersons.getFirst();
        UpdateContext.initSession(person);
        this.goToCreateEmployee();
    }


    /**
     * initializes view for all Employees
     */
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

    /**
     * goes to create employee form
     */
    public void goToCreateEmployee() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/createEmployeeView.fxml", 530, 607, "Create Employee");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        }
    }

    /**
     * goes to main view
     */
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
