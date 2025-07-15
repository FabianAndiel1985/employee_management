package org.fabianandiel.services;

import jakarta.persistence.EntityManager;
import org.fabianandiel.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class DAOService {

    public DAOService() {
        throw new UnsupportedOperationException(Constants.WARNING_UTILITY_CLASS);
    }

    public static <T, K> List<T> findItemsWithPropertyOrProperties(String jpql, Class<T> resultClass, EntityManager em, K... params) throws IllegalArgumentException {
        List<T> items = new ArrayList<>();
        try {
            if (params.length == 1) {

                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).getResultList();
            } else if (params.length == 2) {
                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).setParameter("param1", params[1]).getResultList();
            }
            else if (params.length == 3) {
                System.out.println("Goes into query");
                items = em.createQuery(jpql, resultClass)
                        .setParameter("param", params[0]).setParameter("param1", params[1]).setParameter("param2", params[2]).getResultList();
            }
            else {
                throw new IllegalArgumentException("Max 3 parameters are allowed");
            }
        } finally {
            em.close();
            return items;
        }
    }

}
