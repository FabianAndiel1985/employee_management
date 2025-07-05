package org.fabianandiel.services;

import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;

public class GUIService {

    //Protection against intiation
    private GUIService() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * Places the error message in the dedicated text field
     */
    public static void setErrorText(String errorMessage, Text errorText ) {
        errorText.setText(errorMessage);
        errorText.setVisible(true);
    }
}
