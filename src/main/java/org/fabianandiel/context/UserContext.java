package org.fabianandiel.context;

import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;

import java.util.Set;

public class UserContext {
    private static UserContext instance;
    private String username;
    private Set<Role> roles;
    private Person person;

    private UserContext() {
    }

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void initSession(String username, Set<Role> roles, Person person) {
        this.username = username;
        this.roles = roles;
        this.person = person;
    }

    public String getUsername() {
        return this.username;
    }

    public Person getPerson() {
        return person;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public void clearSession() {
        this.username = null;
        this.roles = null;
    }

}
