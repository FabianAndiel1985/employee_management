package org.fabianandiel.controller;

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

}
