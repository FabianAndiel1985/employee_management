package org.fabianandiel.gui;

import jakarta.persistence.EntityManager;
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
import org.fabianandiel.controller.AddressController;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.AddressDAO;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Address;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.EmployeeCRUDService;
import org.fabianandiel.services.EntityManagerProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.validation.EmployeeFormValidationService;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EmployeeFormController implements Initializable {

    @FXML
    private Button createEmployeeGoBack;

    @FXML
    private Text createEmployeeErrorText;

    @FXML
    private TextField createEmployeeFirstname;

    @FXML
    private TextField createEmployeeLastname;

    @FXML
    private ComboBox<Address> createEmployeeAddress;

    @FXML
    private Button createEmployeeNewAddress;

    @FXML
    private Button createEmployeeSubmitEmployee;

    @FXML
    private TextField createEmployeeTelephone;

    @FXML
    private TextField createEmployeeEmail;

    @FXML
    private CheckBox createEmployeeRolesBoxEmployee;

    @FXML
    private CheckBox createEmployeeRolesBoxManager;

    @FXML
    private CheckBox createEmployeeRolesBoxAdmin;

    @FXML
    private ComboBox<Person> createEmployeeSuperior;

    @FXML
    private TableView createEmployeeSubordinates;

    @FXML
    private TextField createEmployeeUsername;

    @FXML
    private TextField createEmployeePassword;

    @FXML
    private TextField createEmployeeVacationEntitlement;

    @FXML
    private TextField createEmployeeHoursWeek;

    @FXML
    private TableColumn<Person, String> createEmployeeSubordinateFirstName;

    @FXML
    private TableColumn<Person, String> createEmployeeSubordinateLastName;

    @FXML
    private Text employeeFormHeading;

    @FXML
    private Text vacationsRequestVacationEntitlement;

    //TODO continue the vaction fields

    @FXML
    private Text vacationsRequestRestVacation;

    private PersonController personController = new PersonController<>(new PersonDAO<>());

    private AddressController addressController = new AddressController<>(new AddressDAO<>());

    //TODO when I add a person with role Manager also add to superiors list
    private final ObservableList<Person> superiors = FXCollections.observableArrayList();

    //TODO when I add a person with role employee also add to subordinates list
    private final ObservableList<Person> subordinates = FXCollections.observableArrayList();

    //TODO when I create a address add it here
    private final ObservableList<Address> addresses = FXCollections.observableArrayList();

    private boolean userHasManagerRole;

    private boolean userHasManagerAndAdminRole;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Person personToUpdate = SelectedEmployeeContext.getPersonToUpdate();

        this.userHasManagerRole = UserContext.getInstance().hasRole(Role.EMPLOYEE) && UserContext.getInstance().hasRole(Role.MANAGER) && !UserContext.getInstance().hasRole(Role.ADMIN);
        this.userHasManagerAndAdminRole = UserContext.getInstance().hasRole(Role.ADMIN);
        initializeAddressDropDown();
        initalizeSuperiorDropdown();
        initializeCheckBoxesAccordingToAuthorization();
        initializeEmployeeTableViewAccordingToAuthorization();

        if (personToUpdate != null) {
            this.changeGUIintoUpdateMode();
            if (this.userHasManagerAndAdminRole) {
                this.createEmployeeRolesBoxEmployee.setDisable(true);
                this.createEmployeeRolesBoxManager.setDisable(true);

                List<Person> persons = this.personController.getPersonBySuperiorID(personToUpdate.getId());
                if (persons != null) {
                    this.subordinates.addAll(persons);
                }
            }
           if (this.userHasManagerRole) {
               this.createEmployeeRolesBoxEmployee.setDisable(true);
               if(this.createEmployeeRolesBoxManager.isDisabled()){
                   this.createEmployeeRolesBoxManager.setDisable(false);
               }
               this.createEmployeeRolesBoxAdmin.setDisable(true);
           }

            this.fillTheFieldsWithPersonToUpdatesValue(personToUpdate);
        }
    }

    private void fillTheFieldsWithPersonToUpdatesValue(Person person) {

        this.createEmployeeFirstname.setText(person.getFirstname());
        this.createEmployeeLastname.setText(person.getLastname());
        this.createEmployeeTelephone.setText(String.valueOf(person.getTelephone()));
        this.createEmployeeEmail.setText(person.getEmail());
        this.createEmployeeUsername.setText(person.getUsername());
        this.createEmployeePassword.setText(person.getPassword());
        this.createEmployeeAddress.getSelectionModel().select(person.getAddress());
        this.createEmployeeVacationEntitlement.setText(String.valueOf(person.getVacation_entitlement()));
        if (person.getSuperior() != null) {
            this.createEmployeeSuperior.getSelectionModel().select(person.getSuperior());
        }
        Set<Role> roles = person.getRoles();
        this.createEmployeeRolesBoxEmployee.setSelected(roles.contains(Role.EMPLOYEE));
        this.createEmployeeRolesBoxManager.setSelected(roles.contains(Role.MANAGER));
        this.createEmployeeRolesBoxAdmin.setSelected(roles.contains(Role.ADMIN));
        if (person.getSubordinates() != null && !person.getSubordinates().isEmpty()) {
            for (Person subordinate : person.getSubordinates()) {
                this.createEmployeeSubordinates.getSelectionModel().select(subordinate);
            }
        }
    }

    /**
     * submits and validates the new employee
     */
    public void employeeFormUpdateCreate() {
        Person createdPerson = this.createPersonFromFields();
        if (createdPerson == null) {
            return;
        }

        if (!EmployeeFormValidationService.validateCreatedPerson(createdPerson, this.createEmployeeErrorText))
            return;

        if (this.createEmployeeErrorText.isVisible())
            this.createEmployeeErrorText.setVisible(false);

        EntityManager em = EntityManagerProvider.getEntityManager();
        Set<Person> selectedSubordinates = new HashSet<>();


        if (SelectedEmployeeContext.getPersonToUpdate() == null) {
            createdPerson.setStatus(Status.JUST_CREATED);
            EmployeeCRUDService.createNewPerson(em, createdPerson, this.subordinates, this.superiors);
        } else {
            EmployeeCRUDService.updatePerson(em, createdPerson, this.subordinates, this.superiors);
        }
        this.onReset();
    }


    //=======================================================================================
    //=========================HELPER METHODS ===============================================


    /**
     * Changes the create employee gui into an update gui
     */
    private void changeGUIintoUpdateMode() {
        this.employeeFormHeading.setText("Update employee");
        this.createEmployeeSubmitEmployee.setText("Update");
    }


    /**
     * goes back to address form
     */
    public void goToAddressForm() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/newAddress.fxml", 407, 471, "Create Address");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.createEmployeeErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.createEmployeeErrorText);
        }
    }

    /**
     * goes back to employee overview
     */
    public void goBackEmployeeOverview() {
        try {
            SelectedEmployeeContext.clearSession();
            SceneManager.switchScene("/org/fabianandiel/gui/employeeOverviewView.fxml", 630, 732, "Employee Overview");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.createEmployeeErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.createEmployeeErrorText);
        }
    }

    /**
     * creates person from the form fields
     *
     * @return person newly created
     */
    private Person createPersonFromFields() {
        Person personToCreate = new Person();

        personToCreate.setFirstname(this.createEmployeeFirstname.getText());
        personToCreate.setLastname(this.createEmployeeLastname.getText());
        personToCreate.setAddress(this.createEmployeeAddress.getValue());
        try {
            String telephoneNumber = this.createEmployeeTelephone.getText();
            if (telephoneNumber.trim().isEmpty()) {
                GUIService.setErrorText("Telephone can not be empty", this.createEmployeeErrorText);
                return null;
            }
            personToCreate.setTelephone(Integer.parseInt(telephoneNumber));
        } catch (NumberFormatException e) {
            GUIService.setErrorText("Invalid telephone number format", this.createEmployeeErrorText);
            return null;
        }
        try {
            String vacationEntitlement = this.createEmployeeVacationEntitlement.getText();
            if (vacationEntitlement.trim().isEmpty()) {
                GUIService.setErrorText("Vacation entitlement can not be empty", this.createEmployeeErrorText);
                return null;
            }
            personToCreate.setVacation_entitlement(Short.parseShort(vacationEntitlement));
        } catch (NumberFormatException e) {
            GUIService.setErrorText("Invalid vacation number format", this.createEmployeeErrorText);
            return null;
        }
            personToCreate.setEmail(createEmployeeEmail.getText());
            Set<Role> roles = new HashSet<>();
            if (this.createEmployeeRolesBoxEmployee.isSelected()) {
                roles.add(Role.EMPLOYEE);
            }
            if (this.createEmployeeRolesBoxManager.isSelected()) {
                roles.add(Role.MANAGER);
            }
            if (this.createEmployeeRolesBoxAdmin.isSelected()) {
                roles.add(Role.ADMIN);
            }
            personToCreate.setRoles(roles);
            ObservableList<Person> selectedPersons = createEmployeeSubordinates.getSelectionModel().getSelectedItems();
            personToCreate.setSubordinates(new HashSet<>(selectedPersons));
            personToCreate.setSuperior(this.createEmployeeSuperior.getValue());
            personToCreate.setUsername(this.createEmployeeUsername.getText());
            personToCreate.setPassword(this.createEmployeePassword.getText());

            return personToCreate;
        }


        /**
         * Initializes the Subordinates Table View
         */
        private void initializeEmployeeTableViewAccordingToAuthorization () {
            this.createEmployeeSubordinates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            if (userHasManagerAndAdminRole) {
                this.createEmployeeSubordinateFirstName.setCellValueFactory(new PropertyValueFactory<Person, String>("firstname"));
                this.createEmployeeSubordinateLastName.setCellValueFactory(new PropertyValueFactory<Person, String>("lastname"));
                List<Person> subordinates = this.personController.getEmployeesWithoutSuperior();
                this.subordinates.addAll(subordinates);
                this.createEmployeeSubordinates.setItems(this.subordinates);
            } else {
                this.createEmployeeSubordinates.setDisable(true);
            }
        }


        /**
         * Displays the role checkboxes according to authorization
         */
        private void initializeCheckBoxesAccordingToAuthorization () {
            if (this.userHasManagerRole) {
                this.createEmployeeRolesBoxEmployee.setSelected(true);
                this.createEmployeeRolesBoxEmployee.setDisable(true);
                this.createEmployeeRolesBoxManager.setDisable(true);
                this.createEmployeeRolesBoxAdmin.setDisable(true);
            }
            if(this.userHasManagerAndAdminRole) {
                this.createEmployeeRolesBoxEmployee.setSelected(true);
                this.createEmployeeRolesBoxEmployee.setDisable(true);
                this.createEmployeeRolesBoxManager.setSelected(true);
                this.createEmployeeRolesBoxManager.setDisable(true);
                this.createEmployeeSuperior.setDisable(true);
            }

        }

        /**
         * Initializes AddressDropdown
         */
        private void initializeAddressDropDown () {
            List<Address> queriedAddresses = this.addressController.getAll(Address.class);
            this.addresses.addAll(queriedAddresses);
            this.createEmployeeAddress.setItems(this.addresses);
        }


        /**
         * reset the form values
         */
        public void onReset () {
            this.createEmployeeFirstname.clear();
            this.createEmployeeLastname.clear();
            this.createEmployeeTelephone.clear();
            this.createEmployeeEmail.clear();
            this.createEmployeeUsername.clear();
            this.createEmployeePassword.clear();
            this.createEmployeeAddress.getSelectionModel().clearSelection();
            this.createEmployeeVacationEntitlement.clear();
            this.createEmployeeSuperior.getSelectionModel().clearSelection();
            this.createEmployeeRolesBoxEmployee.setSelected(false);
            this.createEmployeeRolesBoxManager.setSelected(false);
            this.createEmployeeRolesBoxAdmin.setSelected(false);
            this.createEmployeeSubordinates.getSelectionModel().clearSelection();
            this.createEmployeeErrorText.setVisible(false);
        }


        /**
         * Initializes the Superior Dropdown
         */
        private void initalizeSuperiorDropdown () {
            List<Person> possibleSuperiors = this.personController.getPersonsByRole(Role.MANAGER);
            this.superiors.addAll(possibleSuperiors);
            this.createEmployeeSuperior.setItems(this.superiors);

            createEmployeeSuperior.setCellFactory(comboBox -> new ListCell<Person>() {
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

            createEmployeeSuperior.setButtonCell(new ListCell<Person>() {
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
        }
    }
