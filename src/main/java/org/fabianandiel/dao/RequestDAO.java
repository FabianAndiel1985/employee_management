package org.fabianandiel.dao;

import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.entities.Request;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class RequestDAO<T,ID> extends BaseDAO<T,ID> {

    /**
     * find requests by whom created them and their statusd
     * @param id the id of the person who created the requests
     * @param requestStatus status of searched requests
     * @return requests of the creator with the id and status
     */
    public List<Request> findRequestsByCreatorAndStatus(UUID id, RequestStatus requestStatus) {
       try {
           List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param AND r.status = :param1 ", Request.class, EntityManagerProvider.getEntityManager(), id, requestStatus);
           return requests;
       } catch (RuntimeException e) {
           throw new RuntimeException("Error loading requests by creator and status");
       }
    }

    /**
     * find requests by whom created them
     * @param id the id of the person who created the requests
     * @return requests of the creator with the id
     */
    public List<Request> getRequestsByCreator(UUID id) {
        try {
            List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param", Request.class, EntityManagerProvider.getEntityManager(), id);
            return requests;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error getting requests from this id: "+id);
        }
    }


    /**
     * changes the status of request before certain date
     * @param date request statuses before this date get updated
     * @param originalStatus update requests with this status
     * @param statusToSetTo set requests to this status
     */
    public void changeRequestStatusBeforeDate(LocalDate date, RequestStatus originalStatus, RequestStatus statusToSetTo) {
        try {
            DAOService.changeRequestStatusBeforeDate(date,originalStatus,statusToSetTo);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error setting the requests to expired");
        }
    }



    /**
     * get requests that have either one statur or another one
     * @param status first status possibility
     * @param statusOne second status possibility
     * @return a list of possible statuses
     */
    public List<Request> getRequestsByStatus(RequestStatus status, RequestStatus statusOne) {
        try {
            List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE (r.status = :param OR r.status = :param1)  " +
                    "AND r.creator.id = :param2", Request.class, EntityManagerProvider.getEntityManager(), status, statusOne, UserContext.getInstance().getId());
            if (requests.size() == 0) {
                return null;
            }
            return requests;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error loading requests by status");
        }
    }
}
