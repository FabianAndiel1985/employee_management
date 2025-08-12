package org.fabianandiel.dao;

import jakarta.persistence.EntityManager;
import org.fabianandiel.interfaces.DAOInterface;
import org.fabianandiel.services.EntityManagerProvider;

import java.util.List;

public abstract class BaseDAO<T,ID> implements DAOInterface<T,ID> {

    public T save(T entity) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error when saving ", e);
        } finally {
            em.close();
        }
    }


    @Override
    public T update(T entity) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();
            T updatedEntity = em.merge(entity);
            em.getTransaction().commit();
            return updatedEntity;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error actualizing the data ", e);
        } finally {
            em.close();
        }
    }


    @Override
    public T findById(ID id, Class<T> entityClass) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            return em.find(entityClass, id);
        } catch (Exception e) {
            throw new RuntimeException("error finding by id", e);
        }
        finally {
            em.close();
        }
    }


    @Override
    public void delete(T entity) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            em.getTransaction().begin();
            //if the object isn`t in the hibernate persistence context manage it
            if (!em.contains(entity)) {
                entity = em.merge(entity);
            }
            em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Fehler beim Löschen", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll(Class<T> entityClass) {
        EntityManager em = EntityManagerProvider.getEntityManager();
        try {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error finding all entities",e);
        }
        finally {
            em.close();
        }
    }
}
