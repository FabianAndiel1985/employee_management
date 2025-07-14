package org.fabianandiel.controller;

import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.interfaces.DAOInterface;

public class PersonController<T,ID> extends BaseController<T,ID> {

    private PersonDAO<T,ID> personDAO;

    public PersonController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof PersonDAO ){
            this.personDAO = (PersonDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Ungültiges DAO übergeben.");
        }
    }


    /**
     * Get person object by username.
     * @param username of the person you are searching for
     */
    public Person getPersonByUsername(String username)  {
    // no indepth validation of username String since there is already an validation via the loginRequestValidator
        return this.personDAO.getPersonByUsername(username);
    }



}
