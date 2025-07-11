package org.fabianandiel.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.fabianandiel.constants.Constants;


public class EntityManagerProvider {

    private static EntityManagerFactory EMF;

    private EntityManagerProvider() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * Persits the Entity Manager Factory in the provider
     */
    public static void launch() {
        EMF = Persistence.createEntityManagerFactory("wifi-persistence-unit");
    }

    /**
     * returns a new Entity Manager instance
     * @return EntityManager
     */
    public static EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Shuts down the entity Manager Factory. Should be closed when program is shut down
     */
    public static void shutdown() {
        if (EMF != null && EMF.isOpen()) {
            EMF.close();
        }
    }
}
