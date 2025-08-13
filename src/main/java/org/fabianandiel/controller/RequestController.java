package org.fabianandiel.controller;

import org.fabianandiel.constants.RequestStatus;
import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;
import org.fabianandiel.interfaces.DAOInterface;
import java.time.LocalDate;
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
        try {
            List<Request> requestsWithStatus = new ArrayList<>();
            subordinates.stream().forEach((person) -> {
                List<Request> subordinateRequests = this.requestDAO.findRequestsByCreatorAndStatus(person.getId(), requestStatus);
                if (!subordinateRequests.isEmpty())
                    requestsWithStatus.addAll(subordinateRequests);
            });
            return requestsWithStatus;
        } catch (RuntimeException e) {
            throw e;
        }
    }


    /**
     * get all the requests from a specific employee
     *
     * @param id the id of the employee
     * @return a List of all the vacation requests of an employee
     */
    public List<Request> getRequestsByCreator(UUID id) {
        try {
            return this.requestDAO.getRequestsByCreator(id);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * changes the status of request before certain date
     *
     * @param date           request statuses before this date get updated
     * @param originalStatus update requests with this status
     * @param statusToSetTo  set requests to this status
     */
    public void changeRequestStatusBeforeDate(LocalDate date, RequestStatus originalStatus, RequestStatus statusToSetTo) {
        try {
            this.requestDAO.changeRequestStatusBeforeDate(date, originalStatus, statusToSetTo);
        } catch (RuntimeException e) {
            throw e;
        }
    }




    /**
     * get the requests that have either one status or another one
     *
     * @param status    to start
     * @param statusOne status to end
     * @return gives a list of those requests
     */
    public List<Request> getRequestsByStatus(RequestStatus status, RequestStatus statusOne) {
        try {
            List<Request> requests = this.requestDAO.getRequestsByStatus(status, statusOne);
            return requests;
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
