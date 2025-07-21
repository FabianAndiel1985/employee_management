package org.fabianandiel.controller;

import jakarta.persistence.EntityManager;
import org.fabianandiel.constants.Role;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.interfaces.DAOInterface;
import java.util.List;

public class PersonController<T, ID> extends BaseController<T, ID> {

    private PersonDAO<T, ID> personDAO;

    public PersonController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof PersonDAO) {
            this.personDAO = (PersonDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }

//TODO build it that it is all the same entity manager instance
/* public Person create(Person person, EntityManager em) {
        try {
            return this.personDAO.save(person, em);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
*/

    /**
     * Get person object by username.
     *
     * @param username of the person you are searching for
     */
    public Person getPersonByUsername(String username) {
        List<Person> personList = this.personDAO.getPersonByUsername(username);
        if (personList.size() == 0) {
            return null;
        }
        Person person = personList.getFirst();
        return person;
    }

    /**
     * Get persons by role.
     *
     * @param role of the persons you are searching for
     */
    public List<Person> getPersonsByRole(Role role) {
        return this.personDAO.getPersonsByRole(role);
    }


    /**
     * gets all persons that have only one role
     * @param role role that persons exactly have to have
     * @return a list of persons that at least have only that role
     */
    public List<Person> getPersonsByExactRole(Role role) {
        return this.personDAO.getPersonsByExactRole(role);
    }

}
