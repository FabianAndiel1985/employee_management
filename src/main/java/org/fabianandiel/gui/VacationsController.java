package org.fabianandiel.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class VacationsController implements Initializable {

    @FXML
    private Button vacationsGoBack;

    @FXML
    private Text vacationsErrorText;

    @FXML
    private Button vacationsSubmit;

    @FXML
    private DatePicker vacationsStartDate;

    @FXML
    private DatePicker vacationsEndDate;

    @FXML
    private TextField vacationsNotes;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void goBackToMainView() {
        try {
            SceneManager.goBackToMain();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,vacationsErrorText);
        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
            GUIService.setErrorText(Constants.USER_ERROR_MESSAGE,vacationsErrorText);
        }
    }


    public void submit(){
        Request request = new Request();
        setPreKnownData(request);



        System.out.println(request);

        //ToDo Am Ende Person im User Context updaten
    }


    private void setPreKnownData(Request request) {
        request.setStatus(RequestStatus.PENDING);
        request.setCreationDate(LocalDateTime.now());
        request.setCreator(UserContext.getInstance().getPerson());
    }


}
