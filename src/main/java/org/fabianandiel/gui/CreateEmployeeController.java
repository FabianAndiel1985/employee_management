package org.fabianandiel.gui;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.validation.ConstraintViolation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.Role;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.AddressController;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.AddressDAO;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Address;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.EntityManagerProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.services.ValidatorProvider;
import org.fabianandiel.validation.CreateEmployeeValidationService;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CreateEmployeeController implements Initializable {

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
    private ComboBox createEmployeeSuperior;

    @FXML
    private TableView createEmployeeSubordinates;

    @FXML
    private TextField createEmployeeUsername;

    @FXML
    private TextField createEmployeePassword;

    @FXML
    private TableColumn<Person, String> createEmployeeSubordinateFirstName;

    @FXML
    private TableColumn<Person, String> createEmployeeSubordinateLastName;


    private PersonController personController = new PersonController<>(new PersonDAO<>());

    private AddressController addressController = new AddressController<>(new AddressDAO<>());

    //TODO when I add a person with role Manager also add to superiors list
    private final ObservableList<Person> superiors = FXCollections.observableArrayList();

    //TODO when I add a person with role employee also add to subordinates list
    private final ObservableList<Person> subordinates = FXCollections.observableArrayList();

    //TODO when I create a address add it here
    private final ObservableList<Address> addresses = FXCollections.observableArrayList();

    private boolean hasManagerRole;

    private boolean hasManagerAndAdminRole;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.hasManagerRole = UserContext.getInstance().hasRole(Role.EMPLOYEE) && UserContext.getInstance().hasRole(Role.MANAGER) && !UserContext.getInstance().hasRole(Role.ADMIN);
        this.hasManagerAndAdminRole = UserContext.getInstance().hasRole(Role.ADMIN);
        initializeAddressDropDown();
        initalizeSuperiorDropdown();
        initializeCheckBoxesAccordingToAuthorization();
        initializeEmployeeTableViewAccordingToAuthorization();
    }


    /**
     * submits and validates the person object
     */
    public void createEmployeeSubmit() {
        Person createdPerson = createPersonFromFields();
        if(createdPerson == null) {
            return;
        }

        if(!CreateEmployeeValidationService.validateCreatedPerson(createdPerson,this.createEmployeeErrorText))
            return;

        if (this.createEmployeeErrorText.isVisible())
            this.createEmployeeErrorText.setVisible(false);


        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction tx = em.getTransaction();

    try {
        tx.begin();
        Person newPerson = (Person) this.personController.create(createdPerson,em);
        int batchSize = 30;
        int i = 0;
        for (Person p : newPerson.getSubordinates()) {
            this.subordinates.remove(p);
            p.setSuperior(newPerson);
            em.merge(p);
            if (++i % batchSize == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
        em.clear();
        tx.commit();
    }
    catch (Exception e) {
        tx.rollback();
        e.printStackTrace();
    } finally {
        em.close();
    }
        //TODO CREATE ADDRESS SCREEN
        //TODO ADDRESS MODAL
    }


    //=======================================================================================
    //=========================HELPER METHODS ===============================================


    /**
     * goes back to employee overview
     */
    public void goBackEmployeeOverview() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/employeeOverviewView.fxml", 530, 671, "Employee Overview");
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
        String telephoneNumber = this.createEmployeeTelephone.getText();
        if (telephoneNumber.trim().isEmpty()) {
            GUIService.setErrorText("Telephone can not be empty", this.createEmployeeErrorText);
            return null;
        }
        personToCreate.setTelephone(Integer.parseInt(telephoneNumber));
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
        personToCreate.setUsername(this.createEmployeeUsername.getText());
        personToCreate.setPassword(this.createEmployeePassword.getText());

        return personToCreate;
    }


    /**
     * Initializes the Subordinates Table View
     */
    private void initializeEmployeeTableViewAccordingToAuthorization() {
        this.createEmployeeSubordinates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (hasManagerAndAdminRole) {
            this.createEmployeeSubordinateFirstName.setCellValueFactory(new PropertyValueFactory<Person,String>("firstname"));
            this.createEmployeeSubordinateLastName.setCellValueFactory(new PropertyValueFactory<Person,String>("lastname"));
            List<Person> subordinates = this.personController.getPersonsByExactRole(Role.EMPLOYEE);
            this.subordinates.addAll(subordinates);
            this.createEmployeeSubordinates.setItems(this.subordinates);
        } else {
            this.createEmployeeSubordinates.setDisable(true);
        }
    }


    /*
        Displays the role checkboxes according to authorization
     */
    private void initializeCheckBoxesAccordingToAuthorization() {
        if (this.hasManagerRole) {
            this.createEmployeeRolesBoxEmployee.setSelected(true);
            this.createEmployeeRolesBoxManager.setDisable(true);
            this.createEmployeeRolesBoxAdmin.setDisable(true);
        }
    }

    /**
     * Initializes AddressDropdown
     */
    private void initializeAddressDropDown() {
        List<Address> queriedAddresses = this.addressController.getAll(Address.class);
        this.addresses.addAll(queriedAddresses);
        this.createEmployeeAddress.setItems(this.addresses);
    }


    /**
     * Initializes the Superior Dropdown
     */
    private void initalizeSuperiorDropdown() {
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
