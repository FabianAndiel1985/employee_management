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
     * save a person with a particular entity manager instance
     * @param person that is supposed to be saved
     * @param em instance I want to use
     * @return the successfully saved person
     * @throws RuntimeException when there is an issue saving the person
     */
    public Person save(Person person, EntityManager em) throws RuntimeException {
        try {
            em.persist(person);
            //em.getTransaction().commit();
            return person;
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * gets the person by username
     * @param username of the person you wants
     * @return List of persons who fulfill the query
     */
    public List<Person> getPersonByUsername(String username) {
        //TODO look at left join fetch closer
        List<Person> persons = DAOService.findItemsWithPropertyOrProperties("""
            SELECT DISTINCT p 
            FROM Person p
            LEFT JOIN FETCH p.subordinates s
            LEFT JOIN FETCH p.roles
            WHERE p.username = :param
        """, Person.class, EntityManagerProvider.getEntityManager(), username);
        return persons;
    }



    //TODO look at member in closer
    /**
     * Gets all the persons who have at least the role
     * @param role role that persons at least have to have
     * @return a list of persons that at least have that role
     */
    public List<Person> getPersonsByRole(Role role) {
        String jpql = "SELECT p FROM Person p WHERE :param MEMBER OF p.roles";
        return DAOService.findItemsWithPropertyOrProperties(jpql,Person.class,EntityManagerProvider.getEntityManager(),role);
    }



    /**
     * gets all persons that have only one role
     * @param role role that persons exactly have to have
     * @return a list of persons that at least have only that role
     */
    public List<Person> getEmployeesWithoutSuperior(Role role) {
        String jpql = "SELECT p FROM Person p WHERE SIZE(p.roles) = 1 AND :param MEMBER OF p.roles AND p.superior IS NULL";
        return DAOService.findItemsWithPropertyOrProperties(jpql,Person.class,EntityManagerProvider.getEntityManager(),role);
    }

    public List<Person> getPersonBySuperiorID(UUID id) {
        String jpql = "SELECT p FROM Person p WHERE p.superior.id = :param";
        return DAOService.findItemsWithPropertyOrProperties(jpql,Person.class,EntityManagerProvider.getEntityManager(),id);
    }

    //TODO when time and login is done -
    //find person by name
    //find persons by role
    //find personByNameAndRole

}

