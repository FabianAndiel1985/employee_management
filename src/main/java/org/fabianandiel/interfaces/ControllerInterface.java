package org.fabianandiel.interfaces;

import java.util.UUID;

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
     * @return The updated entity object
     */
    T update(T entity);

    /**
     * Finds an entity object by its ID.
     *
     * @param id The ID of the entity to find
     * @return The found entity object, or null if not found
     */
    T getById(ID id,Class<T> entityClass);
}
