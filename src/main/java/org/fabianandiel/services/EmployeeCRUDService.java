package org.fabianandiel.services;

import jakarta.persistence.EntityManager;
import javafx.collections.ObservableList;
import org.fabianandiel.constants.Role;
import org.fabianandiel.constants.Status;
import org.fabianandiel.context.SelectedEmployeeContext;
import org.fabianandiel.entities.Address;
import org.fabianandiel.entities.Person;

import java.util.HashSet;
import java.util.Set;

public class EmployeeCRUDService {

    /**
     * creates a new person of any role
     *
     * @param em            EntityManager instance
     * @param createdPerson person that shall be saved in the DB
     */
    public static void createNewPerson(EntityManager em, Person createdPerson) {
        if (em == null || createdPerson == null)
            return;

        Set<Person> selectedSubordinates;
        try {
            em.getTransaction().begin();
            Address rawAddress = createdPerson.getAddress();
            //manage the address in the persistence context
            if (rawAddress != null) {
                Address managedAddress = em.find(Address.class, rawAddress.getId());
                createdPerson.setAddress(managedAddress);
            }

            Person rawSuperior = createdPerson.getSuperior();
            //manage the superior in the persistence context
            if (rawSuperior != null) {
                Person managedSuperior = em.find(Person.class, rawSuperior.getId());
                createdPerson.setSuperior(managedSuperior);
            }

            //Don't need to set subordinates in the person to persist the new person
            selectedSubordinates = createdPerson.getSubordinates();
            createdPerson.setSubordinates(new HashSet<>());
            //persist fully persistence context managed person
            em.persist(createdPerson);
            em.flush();


            //makes subordinates managed and updates them with the new superior
            if (selectedSubordinates != null) {
                int batchSize = 30;
                int i = 0;

                for (Person sub : selectedSubordinates) {
                    Person managedSub = em.find(Person.class, sub.getId());
                    managedSub.setSuperior(createdPerson);
                    em.merge(managedSub);

                    if (++i % batchSize == 0) {
                        em.flush();
                        em.clear();
                        createdPerson = em.find(Person.class, createdPerson.getId());
                    }
                }

                if (++i % batchSize != 0) {
                    em.flush();
                    em.clear();
                }
                em.getTransaction().commit();
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            throw new RuntimeException("Error creating the person");
        }

        em.close();
    }


    /**
     * Updates an existing entity in the database
     *
     * @param em             the entity manager instance
     * @param personToUpdate the person to updated
     * @return the fully updated person
     * @throws RuntimeException if the update operation fails
     */
    public static Person updatePerson(EntityManager em, Person personToUpdate) {
        if (em == null || personToUpdate == null)
            return null;

        personToUpdate.setId(SelectedEmployeeContext.getPersonToUpdate().getId());

        Set<Person> selectedSubordinates;
        try {
            em.getTransaction().begin();
            Address rawAddress = personToUpdate.getAddress();
            //manage the address in the persistence context
            if (rawAddress != null) {
                Address managedAddress = em.find(Address.class, rawAddress.getId());
                personToUpdate.setAddress(managedAddress);
            }

            Person rawSuperior = personToUpdate.getSuperior();
            //manage the superior in the persistence context
            if (rawSuperior != null) {
                Person managedSuperior = em.find(Person.class, rawSuperior.getId());
                personToUpdate.setSuperior(managedSuperior);
            }

            //Don't need to set subordinates in the person to persist the new person
            selectedSubordinates = personToUpdate.getSubordinates();
            personToUpdate.setSubordinates(new HashSet<>());
            //persist fully persistence context managed person
            em.merge(personToUpdate);
            em.flush();

            //makes subordinates managed and updates them with the new superior
            if (selectedSubordinates != null) {
                int batchSize = 30;
                int i = 0;

                for (Person sub : selectedSubordinates) {
                    Person managedSub = em.find(Person.class, sub.getId());
                    managedSub.setSuperior(personToUpdate);
                    em.merge(managedSub);

                    if (++i % batchSize == 0) {
                        em.flush();
                        em.clear();
                        personToUpdate = em.find(Person.class, personToUpdate.getId());
                    }
                }

                if (++i % batchSize != 0) {
                    em.flush();
                    em.clear();
                }
                em.getTransaction().commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            throw new RuntimeException("Error updating the person");
        }
        em.close();
        personToUpdate.setSubordinates(selectedSubordinates);

        return personToUpdate;
    }


    /**
     * Sets a given person entity to inactive status and updates their relationships accordingly.
     * This method performs several operations to ensure that the person and all their connected
     * entities (such as address, superior, and subordinates) are correctly updated.
     *
     * @param em                    the entity manager instance used to perform the operation
     * @param personToSetToInactive the person entity to be set inactive
     * @throws RuntimeException if a database error occurs while processing
     */
    public static void setPersonInactive(EntityManager em, Person personToSetToInactive) {
        if (em == null || personToSetToInactive == null)
            return;

        personToSetToInactive.setStatus(Status.INACTIVE);

        Set<Person> selectedSubordinates;
        try {
            em.getTransaction().begin();
            Address rawAddress = personToSetToInactive.getAddress();
            //manage the address in the persistence context
            if (rawAddress != null) {
                Address managedAddress = em.find(Address.class, rawAddress.getId());
                personToSetToInactive.setAddress(managedAddress);
            }

            //CASE EMPLOYEE

            if (personToSetToInactive.getRoles().contains(Role.EMPLOYEE) && personToSetToInactive.getRoles().size() == 1) {
                Person rawSuperior = personToSetToInactive.getSuperior();
                //manage the superior in the persistence context
                if (rawSuperior != null) {
                    Person managedSuperior = em.find(Person.class, rawSuperior.getId());
                    personToSetToInactive.setSuperior(null);
                    managedSuperior.getSubordinates().remove(personToSetToInactive);
                    em.merge(managedSuperior);
                    em.flush();
                }
            }

            //Don't need to set subordinates in the person to persist the new person
            selectedSubordinates = personToSetToInactive.getSubordinates();
            personToSetToInactive.setSubordinates(new HashSet<>());
            //persist fully persistence context managed person
            em.merge(personToSetToInactive);
            em.flush();

            //CASE MANAGER, ADMIN

            //makes subordinates managed and updates them with the new superior
            if (selectedSubordinates != null) {
                int batchSize = 30;
                int i = 0;

                for (Person sub : selectedSubordinates) {
                    Person managedSub = em.find(Person.class, sub.getId());
                    managedSub.setSuperior(null);
                    em.merge(managedSub);

                    if (++i % batchSize == 0) {
                        em.flush();
                        em.clear();
                    }
                }
                if (++i % batchSize != 0) {
                    em.flush();
                    em.clear();
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            em.close();
            throw new RuntimeException("Error setting person to inactive");
        }
        em.close();
    }


}
