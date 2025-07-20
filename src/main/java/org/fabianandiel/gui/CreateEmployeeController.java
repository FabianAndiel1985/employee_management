package org.fabianandiel.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

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

    private PersonController personController = new PersonController<>(new PersonDAO<>());

    private AddressController addressController = new AddressController<>(new AddressDAO<>());

    //TODO when I add a person with role Manager also add to superiors list
    private final ObservableList<Person> superiors = FXCollections.observableArrayList();

    //TODO when I create a address add it here
    private final ObservableList<Address> addresses = FXCollections.observableArrayList();

    private boolean hasManagerRole;

    private boolean hasManagerAndAdminRole;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.hasManagerRole = UserContext.getInstance().hasRole(Role.EMPLOYEE) && UserContext.getInstance().hasRole(Role.MANAGER) && !UserContext.getInstance().hasRole(Role.ADMIN);
        this.hasManagerAndAdminRole= UserContext.getInstance().hasRole(Role.ADMIN);
        initializeAddressDropDown();
        initalizeSuperiorDropdown();
        displayCheckBoxesAccordingToAuthorization();
        initializeEmployeeTableViewAccordingToAuthorization();
    }


    /**
     * submits and validates the person object
     */
    private void createEmployeeSubmit() {
        Person createdPerson = createPersonFromFields();
    }



    //=========================HELPER METHODS ===============================================


    /**
     * goes back to employee overview
     */
    public void goBackEmployeeOverview() {
        try {
            SceneManager.switchScene("/org/fabianandiel/gui/employeeOverviewView.fxml",530,671,"Employee Overview");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.createEmployeeErrorText);
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,this.createEmployeeErrorText);
        }
    }

    /**
     * creates person from the form fields
     * @return person newly created
     */
    private Person createPersonFromFields() {
        Person personToCreate = new Person();

        personToCreate.setFirstname(this.createEmployeeFirstname.getText());
        personToCreate.setLastname(this.createEmployeeLastname.getText());
        personToCreate.setAddress(this.createEmployeeAddress.getValue());
        String telephoneNumber = this.createEmployeeTelephone.getText();
        //TODO handle error here
        personToCreate.setTelephone(Integer.parseInt(telephoneNumber));
        personToCreate.setEmail(createEmployeeEmail.getText());
        Set<Role> roles = new HashSet<>();
        if(this.createEmployeeRolesBoxEmployee.isSelected()){
            roles.add(Role.EMPLOYEE);
        }
        if(this.createEmployeeRolesBoxManager.isSelected()){
            roles.add(Role.MANAGER);
        }
        if(this.createEmployeeRolesBoxAdmin.isSelected()) {
            roles.add(Role.ADMIN);
        }



      /*
        @FXML
        private ComboBox createEmployeeSuperior;

        @FXML
        private TableView createEmployeeSubordinates;*/



    return null;
    }



    /**
     * Initializes the Subordinates Table View
     */
    private void initializeEmployeeTableViewAccordingToAuthorization() {
        if(hasManagerAndAdminRole) {
            //TODO initialize the employees to choose
        }
        else  {
            this.createEmployeeSubordinates.setDisable(true);
        }
    }






    /*
        Displays the role checkboxes according to authorization
     */
    private void displayCheckBoxesAccordingToAuthorization() {
        if(this.hasManagerRole) {
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
