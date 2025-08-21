package org.fabianandiel.context;

import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;

import java.util.Set;
import java.util.UUID;

public class UserContext {
    private static UserContext instance;

    private String username;
    private Set<Role> roles;
    private Person person;
    private UUID id;

    private UserContext() {
    }

    /**
     * Returns the singleton instance of the context.
     *
     * @return the shared instance
     */
    public static UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }


    /**
     * Initializes the user session with the provided user data.
     *
     * @param username the name of the logged-in user
     * @param roles the set of roles assigned to the user
     * @param id the unique identifier (UUID) of the user
     * @param person the person entity representing the user
     */
    public void initSession(String username, Set<Role> roles, UUID id, Person person) {
        this.username = username;
        this.roles = roles;
        this.id = id;
        this.person = person;
    }

    public String getUsername() {
        return this.username;
    }

    public UUID getId() {
        return id;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public Person getPerson() {
        return person;
    }

    /**
     * Checks whether the current user has the specified role.
     *
     * @param role to check for (e.g., EMPLOYEE, MANAGER, ADMIN)
     * @return true if the user has the role,  return false otherwise
     */
    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Clears all user-related data stored in the context.
     */
    public void clearSession() {
        this.username = null;
        this.id= null;
        this.roles = null;
        this.person = null;
    }

}
