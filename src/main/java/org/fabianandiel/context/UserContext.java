package org.fabianandiel.context;

import org.fabianandiel.constants.Role;

import java.util.Set;

public class UserContext {
    private static UserContext instance;
    private String username;
    private Set<Role> roles;

    private UserContext() {
    }

    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void initSession(String username, Set<Role> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public void clearSession() {
        this.username = null;
        this.roles = null;
    }

}
