package org.fabianandiel.validation;

import javafx.scene.text.Text;
import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.GUIService;

public class CreateEmployeeValidationService {

    public static boolean validateRoles(Person personToValidate, Text createEmployeeErrorText) {
        if(personToValidate.getRoles() == null || personToValidate.getRoles().size()== 0  ){
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





}
