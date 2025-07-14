package org.fabianandiel.interfaces;

import java.util.List;

/**
 * Generic DAO interface for basic CRUD operations.
 *
 * @param <T>  The type of the entity
 * @param <ID> The type of the entity's ID
 */
public interface DAOInterface<T, ID> {

    /**
     * Saves a new entity in the database.
     *
     * @param entity The entity to be saved
     * @return The saved entity with a generated ID
     */
    T save(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity to be updated
     * @return The updated entity
     */
    T update(T entity);

    /**
     * Deletes an entity from the database.
     *
     * @param entity The entity to be deleted
     */
    void delete(T entity);

    /**
     * Finds an entity by its ID.
     *
     * @param id The ID of the entity to be found
     * @param entityClass The class of the entity that shall be found
     * @return The found entity, or null if not found
     */
    T findById(ID id,Class<T> entityClass);

    /**
     * Returns all entities of a given type.
     *
     * @return A list of all entities
     */
    List<T> findAll(Class<T> entityClass);
}
