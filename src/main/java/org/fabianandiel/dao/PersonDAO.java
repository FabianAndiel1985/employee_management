package org.fabianandiel.dao;

import jakarta.persistence.EntityManager;
import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.util.List;
import java.util.UUID;


public class PersonDAO<T, ID> extends BaseDAO<T, ID> {


    /**
     * gets the person by username
     *
     * @param username of the person you wants
     * @return List of persons who fulfill the query
     */
    public List<Person> getPersonByUsername(String username) {
        try {
            List<Person> persons = DAOService.findItemsWithPropertyOrProperties("""
                        SELECT DISTINCT p 
                        FROM Person p
                        LEFT JOIN FETCH p.subordinates s
                        JOIN FETCH p.roles
                        WHERE p.username = :param
                    """, Person.class, EntityManagerProvider.getEntityManager(), username);
            return persons;
        } catch (Exception e) {
            throw new RuntimeException("Error loading person by username.",e);
        }
    }

    /**
     * Gets all the persons who have at least the role
     *
     * @param role role that persons at least have to have
     * @return a list of persons that at least have that role
     */
    public List<Person> getPersonsByRole(Role role) {
        try {
            String jpql = "SELECT p FROM Person p WHERE :param MEMBER OF p.roles";
            return DAOService.findItemsWithPropertyOrProperties(jpql, Person.class, EntityManagerProvider.getEntityManager(), role);
        } catch (Exception e) {
            throw new RuntimeException("Error loading person by role",e);
        }
    }


    /**
     * gets all persons that have only one role
     *
     * @param role role that persons exactly have to have
     * @return a list of persons that at least have only that role
     */
    public List<Person> getEmployeesWithoutSuperior(Role role) {
        try {
            String jpql = "SELECT p FROM Person p WHERE SIZE(p.roles) = 1 AND :param MEMBER OF p.roles AND p.superior IS NULL";
            return DAOService.findItemsWithPropertyOrProperties(jpql, Person.class, EntityManagerProvider.getEntityManager(), role);
        } catch (Exception e) {
            throw new RuntimeException("Error loading employees without superiors",e);
        }
    }


    /**
     * gets person by their superior
     *
     * @param id of the superior of the person
     * @return liste von personen mit einem superior
     */
    public List<Person> getPersonBySuperiorID(UUID id) {
        try {
            String jpql = "SELECT p FROM Person p WHERE p.superior.id = :param";
            return DAOService.findItemsWithPropertyOrProperties(jpql, Person.class, EntityManagerProvider.getEntityManager(), id);
        } catch (Exception e) {
            throw new RuntimeException("Error loading person without superior.",e);
        }
    }

}

