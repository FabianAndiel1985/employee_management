package org.fabianandiel.gui;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import org.fabianandiel.validation.LoginRequest;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;


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
            GUIService.setErrorText(firstViolation.getMessage(),loginErrorText);
        } else {
            if(this.loginErrorText.isVisible())
            this.loginErrorText.setVisible(false);

            //TODO check credentials with database

            PersonDAO<Person, UUID> dao = new PersonDAO<>();
            PersonController<Person,UUID> personController= new PersonController<>(dao);
            Person person = personController.getPersonByUsername("charlie");
            System.out.println(person);







           // List<Person> subordinates= DAOService.findItemsWithPropertyOrProperties(   "SELECT p FROM Person p WHERE p.superior.id = :param", Person.class,EntityManagerProvider.getEntityManager(),UUID.fromString("22222222-aaaa-4bbb-bccc-333333333333"));

            System.out.println("SUBORDINATES: ");

            UUID charlieId = UUID.fromString("bbbb2222-cccc-4ddd-eeee-aaaa00000003");

            List<Person> subordinates = DAOService.findItemsWithPropertyOrProperties(
                    "SELECT p FROM Person p WHERE p.superior.id = :param",
                    Person.class,
                    EntityManagerProvider.getEntityManager(),
                    charlieId
            );


            System.out.println(subordinates);

            //TODO give the roles array to the scene view

            //TODO make the dummy data so that each person has the previous role

            //Go to main view
            try {
                SceneManager.switchScene("/org/fabianandiel/gui/mainView.fxml", 400, 400, "Main");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                GUIService.setErrorText(USER_ERROR_MESSAGE,loginErrorText);
            } catch (IOException e) {
                System.out.println("IO Exception: " + e.getMessage());
                GUIService.setErrorText(USER_ERROR_MESSAGE,loginErrorText);
            }
        }
    }


    /**
     * End the programm if the user wants to
     */
    public void endProgramm(){
        EntityManagerProvider.shutdown();
        System.exit(0);
    }

}
