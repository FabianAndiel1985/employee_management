package org.fabianandiel.validation;

import jakarta.validation.ConstraintViolation;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.ValidatorProvider;

import java.util.Set;

public class CreateEmployeeValidationService {

    /**
     * validates the the roles of a person, if there is a combination, that is`t supposed to exist
     * @param personToValidate person that roles have to be validate
     * @param createEmployeeErrorText where the text gets written in case of error
     * @return if a non valid role combination was found
     */
    private static boolean validateRoles(Person personToValidate, Text createEmployeeErrorText) {
        if(personToValidate.getRoles().size()== 0){
            GUIService.setErrorText("Every person has at least to have the role employee!", createEmployeeErrorText);
            return false;
        }

        if(personToValidate.getRoles().size() == 1 && !personToValidate.getRoles().contains(Role.EMPLOYEE)){
            GUIService.setErrorText("Every person has at least to have the role employee!", createEmployeeErrorText);
            return false;
        }

        if(personToValidate.getRoles().size()== 2 && personToValidate.getRoles().contains(Role.EMPLOYEE) && personToValidate.getRoles().contains(Role.ADMIN) ) {
            GUIService.setErrorText("Admins must have the roles employee and manager!", createEmployeeErrorText);
            return false;
        }

        if(personToValidate.getRoles().size()== 2 && !personToValidate.getRoles().contains(Role.EMPLOYEE)) {
            GUIService.setErrorText("Admins must have the roles employee and manager", createEmployeeErrorText);
            return false;
        }
        createEmployeeErrorText.setVisible(false);
        return true;
    }

    /**
     *
     * @param subordinates subordinates of the person that has to be validated
     * @param superior superior of the person that has to be validated
     * @param roles roles the person that has to be validated has
     * @return
     */
    private static boolean validateSubordinatesAndSuperior(Set<Person> subordinates, Person superior, Set<Role> roles, Text createEmployeeErrorText ) {
       if(roles.contains(Role.EMPLOYEE) && roles.size() == 1 && subordinates.size()>= 1 ) {
           GUIService.setErrorText("Employees can't have subordinates", createEmployeeErrorText);
           return false;
       }
        if(roles.size() == 2 && superior != null ) {
            GUIService.setErrorText("Employees can't have subordinates", createEmployeeErrorText);
            return false;
        }
        createEmployeeErrorText.setVisible(false);
        return true;
    }

    public static boolean validateCreatedPerson(Person createdPerson, Text createEmployeeErrorText) {

        Set<ConstraintViolation<Person>> violations = ValidatorProvider.getValidator().validate(createdPerson);

        if (!violations.isEmpty()) {
            ConstraintViolation<Person> firstViolation = violations.iterator().next();
            GUIService.setErrorText(firstViolation.getMessage(), createEmployeeErrorText);
            return false;
        }

        if(!validateRoles(createdPerson,createEmployeeErrorText))
            return false;

        if(!validateSubordinatesAndSuperior(createdPerson.getSubordinates(), createdPerson.getSuperior(), createdPerson.getRoles(),createEmployeeErrorText))
            return false;

        return true;
    }
}
