package org.fabianandiel.controller;

import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.dao.RequestDAO;

import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;
import org.fabianandiel.interfaces.DAOInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RequestController<T, ID> extends BaseController<T, ID> {

    private RequestDAO<T, ID> requestDAO;


    public RequestController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof RequestDAO<T, ID>) {
            this.requestDAO = (RequestDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Invalid DAO.");
        }
    }

    /**
     * gets the requests of a manager`s subordinates with a certain status
     *
     * @param subordinates  of the manager
     * @param requestStatus of the requests
     * @return list of requests of a manager`s subordinates with a certain status
     */
    public List<Request> getRequestsOfSubordinatesByStatus(Set<Person> subordinates, RequestStatus requestStatus) {

        List<Request> requestsWithStatus = new ArrayList<>();

        subordinates.stream().forEach((person) -> {
            List<Request> subordinateRequests = this.requestDAO.findRequestsByCreatorAndStatus(person.getId(), requestStatus);
            if (!subordinateRequests.isEmpty())
                requestsWithStatus.addAll(subordinateRequests);
        });
        return requestsWithStatus;
    }

    /**
     * gets the requests by a certain creator with a certain status
     *
     * @param id            of the requests creator
     * @param requestStatus of the requests
     * @return list of requests of a certain person and status
     */
    public List<Request> getRequestsByCreatorAndStatus(UUID id, RequestStatus requestStatus) {
        return this.requestDAO.findRequestsByCreatorAndStatus(id, requestStatus);
    }

    public List<Request> getRequestsByCreator(UUID id) {
        return this.requestDAO.findRequestsByCreator(id);
    }


}
