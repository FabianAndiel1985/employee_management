package org.fabianandiel.controller;

import org.fabianandiel.dao.RequestDAO;
import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.interfaces.DAOInterface;

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
}
