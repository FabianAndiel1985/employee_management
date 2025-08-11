package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.constants.Role;
import org.fabianandiel.context.SelectedEmployeeContext;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.SceneManager;
import java.util.Set;
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

    @FXML
    private Text masterDataSubordinatesHeading;

    @FXML
    private Text masterDataSuperiorHeading;

    @FXML
    private Text masterDataRequestsHeading;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Person person = SelectedEmployeeContext.getPersonToUpdate();
        this.masterDataFirstname.setText(person.getFirstname());
        this.masterDataLastname.setText(person.getLastname());
        this.masterDataAddress.setText(person.getAddress().toString());
        this.masterDataTelephone.setText(person.getTelephone() + "");
        this.masterDataEmail.setText(person.getEmail());
        this.masterDataUsername.setText(person.getUsername());
        this.masterDataPassword.setText(person.getPassword());
        if (person.getRoles().size() == 1 && person.getRoles().contains(Role.EMPLOYEE)) {
            this.displaySuperior(person);
        }
        if (person.getRoles().contains(Role.ADMIN) || person.getRoles().contains(Role.MANAGER)) {
            this.displaySubordinates(person);
        }
        this.masterDataRoles.setText(person.getRoles().toString());
        this.masterDataStatus.setText(person.getStatus().toString());
        this.displayPendingRequests(person);
        this.masterDataVacationEntitlement.setText(person.getVacation_entitlement() + "");
        this.masterDataVacationRemaining.setText(person.getVacation_remaining()+"");
        this.masterDataWorkingHours.setText(person.getWeek_work_hours() + "");

    }


    /**
     * Displays pending requests according to role of person
     * @param person
     */
    private void displayPendingRequests(Person person) {
        if (person.getRoles().size() == 1 && person.getRoles().contains(Role.EMPLOYEE)) {
            int amountOfPendingRequests = person.getRequests().stream().filter(r->r.getStatus()== RequestStatus.PENDING).toList().size();
            this.masterDataRequests.setText(String.valueOf(amountOfPendingRequests));
        }
        else {
            this.masterDataRequests.setVisible(false);
            this.masterDataRequests.setManaged(false);
            this.masterDataRequestsHeading.setVisible(false);
            this.masterDataRequestsHeading.setManaged(false);
        }
    }

    /**
     * displays the subordinates of the person in the selected employee context
     *
     * @param person person in the selected employee context
     */
    private void displaySubordinates(Person person) {
        Set<Person> subordinates = person.getSubordinates();
        this.masterDataSuperiorHeading.setVisible(false);
        this.masterDataSuperiorHeading.setManaged(false);
        this.masterDataSuperior.setVisible(false);
        this.masterDataSuperior.setVisible(false);

        if (subordinates.size() == 0) {
            this.masterDataSubordinates.setText("No subordinates at the moment");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Person subordinate : subordinates) {
            sb.append(subordinate.getFirstname()).append(" ").append(subordinate.getLastname());
            if (index < subordinates.size()) {
                sb.append(", ");
            }
            index++;
        }
        this.masterDataSubordinates.setText(sb.toString());
    }


    /**
     * displays the superior of the person in the selected employee context
     *
     * @param person person in the selected employee context
     */
    private void displaySuperior(Person person) {
        if(person.getSuperior() == null)
            return;
        this.masterDataSuperior.setText(person.getSuperior().getFirstname() + " " + person.getSuperior().getLastname());
        this.masterDataSubordinates.setVisible(false);
        this.masterDataSubordinates.setManaged(false);
        this.masterDataSubordinatesHeading.setVisible(false);
        this.masterDataSubordinatesHeading.setManaged(false);
    }


    public void goBackToMain() {
        SceneManager.goBackToMainView(this.masterDataErrorText);
    }

}
