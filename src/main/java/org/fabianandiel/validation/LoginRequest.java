package org.fabianandiel.validation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * This is a helper class to run the bean validation of Hibernate on and
 * reduce boilerplate code
 */
@AllArgsConstructor
@Getter
public class LoginRequest {

    @NotBlank(message="The username can not be empty")
    @Size(min = 2, max = 50, message = "Your username`s length is incorrect.")
    private String username;

    @NotBlank(message="The password can not be empty")
    @Size(min = 4, max = 50,message="Your password`s length is incorrect.")
    private String password;
}