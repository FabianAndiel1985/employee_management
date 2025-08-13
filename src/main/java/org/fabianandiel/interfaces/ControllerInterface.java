package org.fabianandiel.interfaces;

import java.util.List;

public interface ControllerInterface<T,ID> {

    /**
     * Saves a new entity object in the database.
     *
     * @param entity The entity object to be saved
     * @return The saved entity object with generated ID
     */
    T create(T entity);

    /**
     * Updates an existing entity object in the database.
     *
     * @param entity The entity object to be updated
     */
    T update(T entity);

    /**
     * Finds an entity object by its ID.
     *
     * @param id The ID of the entity to find
     * @return The found entity object, or null if not found
     */
    T getById(ID id,Class<T> entityClass);

    /**
     * returns all entites of the provided class
     * @param entityClass the provided class
     * @return all entities of the provided type
     */
    List<T> getAll(Class<T> entityClass);


}
