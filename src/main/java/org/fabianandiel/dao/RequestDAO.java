package org.fabianandiel.dao;

import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.entities.Request;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;

import java.util.List;
import java.util.UUID;

public class RequestDAO<T,ID> extends BaseDAO<T,ID> {


    public List<Request> findRequestsByCreatorAndStatus(UUID id, RequestStatus requestStatus) {
        List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param AND r.status = :param1 ",Request.class,EntityManagerProvider.getEntityManager(),id,requestStatus);
        return requests;
    }


    public List<Request> findRequestsByCreator(UUID id) {
        List<Request> requests = DAOService.findItemsWithPropertyOrProperties("SELECT r FROM Request r WHERE r.creator.id = :param",Request.class,EntityManagerProvider.getEntityManager(),id);
        return requests;
    }
}
