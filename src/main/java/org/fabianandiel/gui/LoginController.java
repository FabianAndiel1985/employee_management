package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.Status;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.*;
import org.fabianandiel.validation.LoginRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;


public class LoginController implements Initializable {

    String USER_ERROR_MESSAGE = "Issue with loading next screen please contact support";

    @FXML
    private TextField loginUsername;

    @FXML
    private TextField loginPassword;

    @FXML
    private Text loginErrorText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginUsername.clear();
        loginPassword.clear();
    }

    /**
     * Conducts the login when the user enters the correct data
     */
    public void conductLogin() {
        String username = this.loginUsername.getText();
        String password = this.loginPassword.getText();
        //Helper object to run bean validation on
        LoginRequest lr = new LoginRequest(username, password);

        Set<ConstraintViolation<LoginRequest>> violations = ValidatorProvider.getValidator().validate(lr);

        if (!violations.isEmpty()) {
            ConstraintViolation<LoginRequest> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), loginErrorText);
            return;
        }
        if (this.loginErrorText.isVisible())
            this.loginErrorText.setVisible(false);

        PersonDAO<Person, UUID> dao = new PersonDAO<>();
        PersonController<Person, UUID> personController = new PersonController<>(dao);


        ExecutorService executorService = ExecutorServiceProvider.getExecutorService();

        executorService.submit(() -> {
            Person person;
            try {
                person = personController.getPersonByUsername(username);
            } catch (RuntimeException e) {
                Platform.runLater(() -> GUIService.setErrorText(e.getMessage(), this.loginErrorText));
                e.printStackTrace();
                return;
            }

            if (person == null) {
                Platform.runLater(() -> GUIService.setErrorText(Constants.LOGIN_ERROR_MESSAGE, loginErrorText));
                return;
            }

            if (person.getStatus().equals(Status.INACTIVE)) {
                Platform.runLater(() -> GUIService.setErrorText("Inactive employee can not login", loginErrorText));
                return;
            }

            if (!person.getPassword().equals(password)) {
                Platform.runLater(() -> GUIService.setErrorText(Constants.LOGIN_ERROR_MESSAGE, loginErrorText));
                return;
            }
            Platform.runLater(() ->
            {
                UserContext.getInstance().initSession(username, person.getRoles(), person.getId(), person);

                //Go to main view
                try {
                    SceneManager.switchScene("/org/fabianandiel/gui/mainView.fxml", 400, 400, "Main");
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    GUIService.setErrorText(USER_ERROR_MESSAGE, loginErrorText);
                } catch (IOException e) {
                    e.printStackTrace();
                    GUIService.setErrorText(USER_ERROR_MESSAGE, loginErrorText);
                }
            });
        });
    }


    /**
     * End the programm if the user wants to
     */
    public void endProgramm() {
        EntityManagerProvider.shutdown();
        System.exit(0);
    }

}
