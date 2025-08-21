package org.fabianandiel.services;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/*
return validator throught the application
 */
public class ValidatorProvider {
    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static Validator getValidator() {
        return validator;
    }
}
