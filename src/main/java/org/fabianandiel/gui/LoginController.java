package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.fabianandiel.dto.LoginRequest;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;


public class LoginController implements Initializable {

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
            this.loginErrorText.setText(firstViolation.getMessage());
            this.loginErrorText.setVisible(true);
        } else {
            if(this.loginErrorText.isVisible())
            this.loginErrorText.setVisible(false);
            //Todo check credentials with database

            //Go to main view
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainView.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 400);

                Stage stage = (Stage) loginUsername.getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Main View");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * End the programm if the user wants to
     */
    public void endProgramm(){
        System.exit(0);
    }

}
