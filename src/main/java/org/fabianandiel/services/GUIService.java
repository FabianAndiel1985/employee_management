package org.fabianandiel.services;

import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;

public class GUIService {

    //Protection against intiation
    private GUIService() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * Displays an error text at a text field
     * @param errorMessage what the error text is supposed to say
     * @param errorText where the error message is supposed to be displayed
     */
    public static void setErrorText(String errorMessage, Text errorText ) {
        errorText.setText(errorMessage);
        errorText.setVisible(true);
    }
}
