package org.fabianandiel.dao;

import org.fabianandiel.constants.Role;
import org.fabianandiel.entities.Person;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.util.List;


public class PersonDAO<T, ID> extends BaseDAO<T, ID> {

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
    public List<Person> getPersonsByRole(Role role) {
        String jpql = "SELECT p FROM Person p WHERE :param MEMBER OF p.roles";
        return DAOService.findItemsWithPropertyOrProperties(jpql,Person.class,EntityManagerProvider.getEntityManager(),role);
    }


    //TODO when time and login is done -
    //find person by name
    //find persons by role
    //find personByNameAndRole

}
