package org.fabianandiel.dao;

import org.fabianandiel.constants.RequestStatus;
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
        List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param AND r.status = :param1 ",Request.class,EntityManagerProvider.getEntityManager(),id,requestStatus);
        return requests;
    }

    /**
     * find requests by whom created them
     * @param id the id of the person who created the requests
     * @return requests of the creator with the id
     */
    public List<Request> findRequestsByCreator(UUID id) {
        List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param",Request.class,EntityManagerProvider.getEntityManager(),id);
        return requests;
    }


    /**
     * changes the status of request before certain date
     * @param date request statuses before this date get updated
     * @param originalStatus update requests with this status
     * @param statusToSetTo set requests to this status
     */
    public void changeRequestStatusBeforeDate(LocalDate date, RequestStatus originalStatus, RequestStatus statusToSetTo) {
    DAOService.changeRequestStatusBeforeDate(date,originalStatus,statusToSetTo);
    }
}
