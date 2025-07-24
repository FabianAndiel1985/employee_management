package org.fabianandiel.controller;

import jakarta.persistence.EntityManager;
import org.fabianandiel.constants.Role;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.interfaces.DAOInterface;
import java.util.List;
import java.util.UUID;

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
     * save person to database using one specific entity manager instance
     * @param person the person I want to save
     * @param em specific entity manager instance
     * @return the created person
     */
    public Person create(Person person, EntityManager em) throws RuntimeException {
        try {
            return this.personDAO.save(person, em);
        } catch (RuntimeException e) {
            throw e;
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

    /**
     * Get person object by superiorID.
     *
     * @param id of the superior
     */
    public List<Person> getPersonBySuperiorID(UUID id) {
        List<Person> persons = this.personDAO.getPersonBySuperiorID(id);
        if (persons.size() == 0) {
            return null;
        }
        return persons;
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
     * gets all employees without a superior
     * @return a list of employees with no other role and no superior
     */
    public List<Person> getEmployeesWithoutSuperior() {
        return this.personDAO.getEmployeesWithoutSuperior(Role.EMPLOYEE);
    }

}
