package org.fabianandiel.entities;

import lombok.Data;

import org.fabianandiel.constants.Role;
import org.fabianandiel.constants.Status;

import java.util.Set;
import java.util.UUID;

@Data
public class Person {

    private UUID id;
    private String firstname;
    private String lastname;
    private UUID address;
    private int telephone;
    private String email;
    private UUID superior;
    private Set<Role> roles;
    private Status status;

}
