package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fabianandiel.context.SelectedEmployeeContext;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.SceneManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MasterDataController implements Initializable {
    @FXML
    private Text masterDataFirstname;

    @FXML
    private Text masterDataLastname;

    @FXML
    private Text masterDataAddress;

    @FXML
    private Text masterDataTelephone;

    @FXML
    private Text masterDataEmail;

    @FXML
    private Text masterDataUsername;

    @FXML
    private Text masterDataPassword;

    @FXML
    private Text masterDataSuperior;

    @FXML
    private Text masterDataSubordinates;

    @FXML
    private Text masterDataRoles;

    @FXML
    private Text masterDataStatus;

    @FXML
    private Text masterDataRequests;

    @FXML
    private Text masterDataVacationEntitlement;

    @FXML
    private Text masterDataWorkingHours;

    @FXML
    private Text masterDataVacationRemaining;

    @FXML
    private Button masterDataGoBack;

    @FXML
    private Text masterDataErrorText;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Person person = SelectedEmployeeContext.getPersonToUpdate();

        this.masterDataFirstname.setText(person.getFirstname());
        this.masterDataLastname.setText(person.getLastname());
        this.masterDataAddress.setText(person.getAddress().toString());
        this.masterDataTelephone.setText(person.getTelephone()+"");
        this.masterDataEmail.setText(person.getEmail());
        this.masterDataUsername.setText(person.getUsername());
        this.masterDataPassword.setText(person.getPassword());
       // this.masterDataSuperior.setText(person.getSuperior().toString());
       // this.masterDataSubordinates.setText(person.getSubordinates().toString());
        this.masterDataRoles.setText(person.getRoles().toString());
        this.masterDataStatus.setText(person.getStatus().toString());
        //TODO do this new
        this.masterDataRequests.setText(person.getRequests().toString());
        this.masterDataVacationEntitlement.setText(person.getVacation_entitlement()+"");
        this.masterDataWorkingHours.setText(person.getWeek_work_hours() + "");
        //TODO get this from vaction boooking
        this.masterDataVacationRemaining.setText("");
    }

    @FXML
    public void goBackToMain() {
        SceneManager.goBackToMainView(this.masterDataErrorText);
    }

}
