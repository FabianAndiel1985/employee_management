package org.fabianandiel.controller;

import org.fabianandiel.dao.RequestDAO;

import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;
import org.fabianandiel.interfaces.DAOInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RequestController<T,ID> extends BaseController<T,ID>{

    private RequestDAO<T, ID> requestDAO;


    public RequestController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof RequestDAO<T,ID>) {
            this.requestDAO = (RequestDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Invalid DAO.");
        }
    }

    public List<Request> getPendingRequestsOfSubordinates(Set<Person> subordinates) {

        List<Request> pendingRequest = new ArrayList<>();

        subordinates.stream().forEach((person)->{

        });

        return null;
    }
}
