package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.controller.AddressController;
import org.fabianandiel.dao.AddressDAO;
import org.fabianandiel.entities.Address;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.services.ValidatorProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class CreateAddressController implements Initializable {

    @FXML
    private TextField newAddressStreet;

    @FXML
    private TextField newAddressHousenumber;

    @FXML
    private TextField newAddressDoornumber;

    @FXML
    private TextField newAddressZIP;

    @FXML
    private TextField newAddressCity;

    @FXML
    private TextField newAddressCountry;

    @FXML
    private Button newAddressSubmit;

    @FXML
    private Button newAddressReset;

    @FXML
    private Button newAddressGoBack;

    @FXML
    private Text newAddressErrorText;

    private AddressController addressController = new AddressController(new AddressDAO());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void submit() {
        Address address = new Address();
        address.setStreet(this.newAddressStreet.getText());
        address.setHousenumber(this.newAddressHousenumber.getText());
        address.setDoornumber(this.newAddressDoornumber.getText());
        address.setZip(this.newAddressZIP.getText());
        address.setCity(this.newAddressCity.getText());
        address.setCountry(this.newAddressCountry.getText());

        Set<ConstraintViolation<Address>> violations = ValidatorProvider.getValidator().validate(address);

        if (!violations.isEmpty()) {
            ConstraintViolation<Address> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), this.newAddressErrorText);
            this.newAddressErrorText.setVisible(true);
            return;
        }

        this.newAddressErrorText.setVisible(false);
        this.addressController.create(address);
        this.clearFields();
    }

    /**
     * returns to create employee
     */
    public void goToCreateEmployee() {
        try {
            System.out.println("Goes here");
            SceneManager.switchScene("/org/fabianandiel/gui/createEmployeeView.fxml", 530, 607, "Create Employee");
        } catch (IOException e) {
            e.printStackTrace();
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE, this.newAddressErrorText);
        }
    }




    /**
     * Clears all address input fields.
     */
    public void clearFields() {
        this.newAddressStreet.clear();
        this.newAddressHousenumber.clear();
        this.newAddressDoornumber.clear();
        this.newAddressZIP.clear();
        this.newAddressCity.clear();
        this.newAddressCountry.clear();
        this.newAddressErrorText.setVisible(false);
    }

}
