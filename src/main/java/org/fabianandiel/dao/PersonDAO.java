package org.fabianandiel.dao;

import jakarta.persistence.EntityManager;
import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.util.List;


public class PersonDAO<T, ID> extends BaseDAO<T, ID> {



//TODO build it that it is all the same entity manager instance
/*
    public Person save(Person person, EntityManager em) {
        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            return person;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error when saving: " + e.getMessage());
            throw new RuntimeException("Error when saving ", e);
        } finally {
            em.close();
        }
    }
    */







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
    public List<Person> getPersonsByExactRole(Role role) {
        String jpql = "SELECT p FROM Person p WHERE :param MEMBER OF p.roles AND SIZE(p.roles) = 1 AND p.superior IS NULL";
        return DAOService.findItemsWithPropertyOrProperties(jpql,Person.class,EntityManagerProvider.getEntityManager(),role);
    }

    //TODO when time and login is done -
    //find person by name
    //find persons by role
    //find personByNameAndRole

}
