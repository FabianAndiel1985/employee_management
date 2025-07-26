package org.fabianandiel.gui;

import javafx.beans.property.ReadOnlyStringWrapper;
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
import org.fabianandiel.services.EmployeeCRUDService;
import org.fabianandiel.services.EntityManagerProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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
    private TableColumn employeeOverviewStatus;

    @FXML
    private TableColumn<Person, String> employeeOverviewRole;

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
    private TableColumn<Person, String>  employeeOverviewRTMrole;

    @FXML
    private Text employeeOverviewText;

    private ObservableList<Person> allEmployees = FXCollections.observableArrayList();

    private ObservableList<Person> updateableEmployees = FXCollections.observableArrayList();


    private PersonController personController = new PersonController(new PersonDAO());

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
        if (UserContext.getInstance().hasRole(Role.MANAGER) && UserContext.getInstance().getRoles().size() == 2 ) {
            List<Person> personsWithUserAsSuperior = this.allEmployees.stream()
                    .filter((empl) -> {
                        if (empl.getSuperior() != null) {
                            //TODO think if managers may assign unassigned employees to themselves
                            return empl.getSuperior().getId().equals(UserContext.getInstance().getId());
                        }
                        return false;
                    })
                    .toList();

            this.updateableEmployees.addAll(personsWithUserAsSuperior);
        }
        this.employeeOverviewRTMstatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.employeeOverviewRTMrole.setCellValueFactory(param ->
                new ReadOnlyStringWrapper(this.getHighestRoleAsString(param.getValue()))
        );
        //Includes the managers and other admins to table view that they can be updated by the logged in admin
        if (UserContext.getInstance().hasRole(Role.ADMIN)) {
            List<Person> managersAndAdmins = this.allEmployees.stream().filter((person) -> {
                return (person.getRoles().contains(Role.MANAGER) || person.getRoles().contains(Role.ADMIN)) && !person.getId().equals(UserContext.getInstance().getId());
            }).toList();
            this.updateableEmployees.addAll(managersAndAdmins);
        }

        this.employeeOverviewUpdateableEmployees.setItems(this.updateableEmployees);

        this.employeeOverviewUpdateableEmployees.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        handleRowSelection(newValue);
                    } else {
                        this.employeeOverviewUpdateEmployee.setDisable(true);
                    }
                });
    }


    /**
     * saves the person to update to a context and continue
     */
    public void onUpdate() {
        ObservableList<Person> selectedPersons = this.employeeOverviewUpdateableEmployees.getSelectionModel().getSelectedItems();
        if (selectedPersons.size() == 0 || selectedPersons.size() > 1) {
            this.employeeOverviewUpdateEmployee.setDisable(true);
            return;
        }
        UpdateContext.clearSession();
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
        this.employeeOverviewStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        this.employeeOverviewRole.setCellValueFactory(param ->
                new ReadOnlyStringWrapper(getHighestRoleAsString(param.getValue()))
        );
        List<Person> allEmployees = personController.getAll(Person.class);
        this.allEmployees.addAll(allEmployees);
        this.employeeOverviewAllEmployees.setItems(this.allEmployees);
    }

    /**
     * returns the highest role a person has
     * @param person of which the highest role shall be returned
     * @return a String representation of the highest role a person has
     */
    private String getHighestRoleAsString(Person person) {
        Set<Role> roles = person.getRoles();
        if(roles.contains(Role.EMPLOYEE) && roles.size() == 1) {
            return "EMPLOYEE";
        }
        else if(roles.contains(Role.MANAGER) && roles.size() == 2) {
            return "MANAGER";
        }
        else if(roles.contains(Role.ADMIN) && roles.size() == 3)
        {
            return "ADMIN";
        }
        return null;
    }


    /**
     * Sets the employee to update to inactive and removes subordinates and superiors
     */
    public void setEmployeeToInactive() {
        ObservableList<Person> selectedPersons = this.employeeOverviewUpdateableEmployees.getSelectionModel().getSelectedItems();

        Person person = selectedPersons.getFirst();

        EmployeeCRUDService.setPersonInactive(EntityManagerProvider.getEntityManager(),person,this.allEmployees,this.updateableEmployees);
        this.initializeAllEmployees();
    }


    /**
     * goes to create employee form
     */
    public void goToCreateEmployee() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/employeeFormView.fxml", 530, 607, "Create Employee");
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
