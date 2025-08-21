package org.fabianandiel.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.RequestStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DAOService {

    public DAOService() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    /**
     * Executes a JPQL query with up to three parameters and returns the results as a list.
     *
     * This method is a generic helper method for querying entities using JPQL.
     * It supports up to three named parameters, which will be set as "param", "param1", and "param2".
     *
     * @param <T>         the type of the result entities
     * @param <K>         the type of the query parameters
     * @param jpql        the JPQL query string (must contain named parameters: "param", "param1", "param2")
     * @param resultClass the entity class expected in the result list
     * @param em          the  EntityManager instance used to create and execute the query
     * @param params      one to three parameter values for the query
     * @return            returns a list of entities matching the query
     * @throws IllegalArgumentException if more than three parameters are provided
     * @throws RuntimeException         if query execution fails
     */
    public static <T, K> List<T> findItemsWithPropertyOrProperties(String jpql, Class<T> resultClass, EntityManager em, K... params) throws IllegalArgumentException {
        List<T> items = new ArrayList<>();
        try {
            if (params.length == 1) {
                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).getResultList();
            } else if (params.length == 2) {
                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).setParameter("param1", params[1]).getResultList();
            } else if (params.length == 3) {
                System.out.println("Goes into query");
                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).setParameter("param1", params[1]).setParameter("param2", params[2]).getResultList();
            } else {
                throw new IllegalArgumentException("Max 3 parameters are allowed");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        finally {
            em.close();
        }
        return items;
    }

    /**
     * changes the status of request before certain date except for the accepted ones
     *
     * @param date           request statuses before this date get updated
     * @param originalStatus update requests with this status
     * @param statusToSetTo  set requests to this status
     */
    public static void changeRequestStatusBeforeDate(LocalDate date, RequestStatus originalStatus, RequestStatus statusToSetTo) {
        String jpql = "UPDATE Request r SET r.status = :param WHERE r.status = :param1 AND r.startDate < :param2 AND r.status <> :param3 ";

        EntityManager em = EntityManagerProvider.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            int updatedCount = em.createQuery(jpql)
                    .setParameter("param", statusToSetTo)
                    .setParameter("param1", originalStatus)
                    .setParameter("param2", date)
                    .setParameter("param3", RequestStatus.ACCEPTED)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new RuntimeException("Error occurred with changing request status before date");
        } finally {
            em.close();
        }
    }
}
