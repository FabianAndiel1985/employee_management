package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.validation.LoginRequest;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


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
    public void conductLogin(){
        String username = this.loginUsername.getText();
        String password = this.loginPassword.getText();
        //Helper object to run bean validation on
        LoginRequest lr = new LoginRequest(username,password);


        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(lr);

        if (!violations.isEmpty()) {
            ConstraintViolation<LoginRequest> firstViolation = violations.iterator().next();
            setLoginErrorText(firstViolation.getMessage());
        } else {
            if(this.loginErrorText.isVisible())
            this.loginErrorText.setVisible(false);
            //Todo check credentials with database

            //Go to main view
            //Todo give the roles array to the scene view

            try {
                SceneManager.switchScene("/org/fabianandiel/gui/mainView.fxml", 400, 400, "Main");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                setLoginErrorText(USER_ERROR_MESSAGE);
            } catch (IOException e) {
                System.out.println("IO Exception: " + e.getMessage());
                setLoginErrorText(USER_ERROR_MESSAGE);
            }
        }


    }

    /**
     * Places the error message in the dedicated text field
     */
    private void setLoginErrorText(String errorMessage) {
        this.loginErrorText.setText(errorMessage);
        this.loginErrorText.setVisible(true);
    }


    /**
     * End the programm if the user wants to
     */
    public void endProgramm(){
        System.exit(0);
    }

}
