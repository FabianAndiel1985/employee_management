package org.fabianandiel.dao;

import org.fabianandiel.context.UserContext;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.time.LocalDate;
import java.util.List;

public class VacationDAO<T, ID> extends BaseDAO<T, ID> {


    public List<Request> getRequestByStartDate(LocalDate startDate) {
        List<Request> requests = DAOService.findItemsWithPropertyOrProperties("""
            SELECT r FROM Request r
                    WHERE r.startDate = :startDate
                      AND r.person.id = :personId
        """, Request.class, EntityManagerProvider.getEntityManager(), startDate, UserContext.getInstance().getId());
        return requests;
    }
}
