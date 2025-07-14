    package org.fabianandiel.dao;

    import org.fabianandiel.entities.Person;
    import org.fabianandiel.services.DAOService;
    import org.fabianandiel.services.EntityManagerProvider;

    import java.util.List;

    public class PersonDAO<T,ID> extends BaseDAO<T, ID> {

        public Person getPersonByUsername(String username) {
            List<Person> person = DAOService.findItemsWithPropertyOrProperties("SELECT DISTINCT p\n" +
                    "FROM Person p\n" +
                   "LEFT JOIN FETCH p.roles\n" +
                    "WHERE p.username = :param", Person.class, EntityManagerProvider.getEntityManager(),username);
           //TODO validation is list has more than one entry
            // TODO Case no entry found
            return person.getFirst();
        }

        //TODO when time and login is done -
        //find person by name
        //find persons by role
        //find personByNameAndRole

    }
