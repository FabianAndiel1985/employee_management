package org.fabianandiel.gui;

import javafx.application.Platform;
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
import org.fabianandiel.constants.Status;
import org.fabianandiel.context.SelectedEmployeeContext;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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
    private TableColumn<Person, String> employeeOverviewRTMrole;

    @FXML
    private Text employeeOverviewText;

    @FXML
    private Button employeeOverviewMasterData;

    private ObservableList<Person> allEmployees = FXCollections.observableArrayList();

    private ObservableList<Person> updateableEmployees = FXCollections.observableArrayList();

    private PersonController personController = new PersonController(new PersonDAO());

    private ExecutorService executorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.executorService = ExecutorServiceProvider.getExecutorService();
        this.executorService.submit(
                () -> {
                    List<Person> allEmployees = this.personController.getAll(Person.class);
                    Platform.runLater(
                            () -> {
                                if (allEmployees != null && !allEmployees.isEmpty())
                                    initializeAllEmployees(allEmployees);
                                initializeUpdateableEmployees();
                                this.employeeOverviewUpdateEmployee.setDisable(true);
                                this.employeeOverviewDeleteEmployee.setDisable(true);
                                this.employeeOverviewMasterData.setDisable(true);
                                if (UserContext.getInstance().hasRole(Role.ADMIN)) {
                                    this.employeeOverviewText.setText("Managers and Admins");
                                }
                            }
                    );
                }
        );
    }

    /**
     * disables the update and master data button when person is selected
     */
    private void handleRowSelection() {
        this.employeeOverviewUpdateEmployee.setDisable(false);
        this.employeeOverviewDeleteEmployee.setDisable(false);
        this.employeeOverviewMasterData.setDisable(false);
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
        if (UserContext.getInstance().hasRole(Role.MANAGER) && UserContext.getInstance().getRoles().size() == 2) {
            List<Person> personsWithUserAsSuperior = this.allEmployees.stream()
                    .filter((empl) -> empl.getSuperior() == null || empl.getSuperior().getId().equals(UserContext.getInstance().getId()))
                    .filter((empl)-> empl.getStatus() != Status.INACTIVE)
                    .filter((empl)->empl.getRoles().size() == 1 && empl.getRoles().contains(Role.EMPLOYEE))
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
                        handleRowSelection();
                    } else {
                        this.employeeOverviewDeleteEmployee.setDisable(true);
                        this.employeeOverviewMasterData.setDisable(true);
                        this.employeeOverviewUpdateEmployee.setDisable(true);
                    }
                });
    }


    /**
     * saves the person to update to a context and continues to Update View
     */
    public void onUpdate() {
        if (this.savePersonToContext(this.employeeOverviewUpdateEmployee))
            this.goToCreateEmployee();
    }


    /**
     * saves the person to update to a context and continues to MasterData View
     */
    public void onCheckMasterData() {
        if (this.savePersonToContext(this.employeeOverviewMasterData))
            this.goToMasterDataView();
    }


    /**
     * saves the selected person to the context
     *
     * @param button to disable
     * @return true if saving to context was successfull, false if not
     */
    private boolean savePersonToContext(Button button) {
        ObservableList<Person> selectedPersons = this.employeeOverviewUpdateableEmployees.getSelectionModel().getSelectedItems();
        if (selectedPersons.size() == 0 || selectedPersons.size() > 1) {
            button.setDisable(true);
            return false;
        }
        Person person = selectedPersons.getFirst();
        if (person.getStatus() != null && person.getStatus().equals(Status.INACTIVE)) {
            GUIService.setErrorText("You can not update an inactive employee", employeeOverviewErrorText);
            return false;
        }

        SelectedEmployeeContext.clearSession();
        button.setDisable(false);

        SelectedEmployeeContext.initSession(person);
        return true;
    }


    /**
     * initializes view for all Employees
     */
    private void initializeAllEmployees(List<Person> allEmployees) {
        this.employeeOverviewAllEmployees.setSelectionModel(null);
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
        this.allEmployees.addAll(allEmployees);
        this.employeeOverviewAllEmployees.setItems(this.allEmployees);
    }

    /**
     * returns the highest role a person has
     *
     * @param person of which the highest role shall be returned
     * @return a String representation of the highest role a person has
     */
    private String getHighestRoleAsString(Person person) {
        Set<Role> roles = person.getRoles();
        if (roles.contains(Role.EMPLOYEE) && roles.size() == 1) {
            return "EMPLOYEE";
        } else if (roles.contains(Role.MANAGER) && roles.size() == 2) {
            return "MANAGER";
        } else if (roles.contains(Role.ADMIN) && roles.size() == 3) {
            return "ADMIN";
        }
        return null;
    }

    /**
     * sets the employee to inactive
     */
    public void setEmployeeToInactive() {
        Person selected = employeeOverviewUpdateableEmployees.getSelectionModel().getSelectedItem();
        final var selectedId = selected.getId();

        executorService.submit(() -> {
            try {
                EmployeeCRUDService.setPersonInactive(EntityManagerProvider.getEntityManager(), selected);

                //reloading the values for rerendering on update
                Person updated = (Person) this.personController.getById(selectedId,Person.class);
                List<Person> persons = this.personController.getAll(Person.class);

                Platform.runLater(() -> {
                    this.allEmployees.clear();
                    this.allEmployees.addAll(persons);

                    //making a 100% sure that the most actual version of updated person is seen in the list
                    int indexOfUpdatedEmployee = getIndexUpdatedEmployeeInList(allEmployees, selectedId);
                    if (indexOfUpdatedEmployee >= 0) {
                        this.allEmployees.set(indexOfUpdatedEmployee, updated);
                    }

                    //Refresh for good measure
                    this.employeeOverviewAllEmployees.refresh();
                    this.employeeOverviewUpdateableEmployees.refresh();

                    employeeOverviewUpdateableEmployees.getSelectionModel().clearSelection();
                    employeeOverviewUpdateEmployee.setDisable(true);
                    employeeOverviewDeleteEmployee.setDisable(true);
                    employeeOverviewMasterData.setDisable(true);
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        GUIService.setErrorText("Error setting employee to INACTIVE.", employeeOverviewErrorText)
                );
            }
        });
    }

    /**
     * get index of employee in list
     * @param list list of employees where I want to find the provided id
     * @param id id of the updated employee
     * @return index of updated employee in list
     */
    private static int getIndexUpdatedEmployeeInList(ObservableList<Person> list, UUID id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(id)) return i;
        }
        return -1;
    }


    /**
     * goes to master data view
     */
    public void goToMasterDataView() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/masterDataView.fxml", 600, 599, "Master Data");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        }
    }


    /**
     * goes to create employee form
     */
    public void goToCreateEmployee() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/employeeFormView.fxml", 530, 691, "Create Employee");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, employeeOverviewErrorText);
        }
    }

    /**
     * goes to main view
     */
    public void goBackToMainView() {
        SceneManager.goBackToMainView(employeeOverviewErrorText);
    }
}
