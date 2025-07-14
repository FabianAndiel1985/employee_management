package org.fabianandiel.controller;

import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.interfaces.DAOInterface;

public class TimeStampController<T,ID> extends BaseController<T,ID> {

    private TimeStampDAO<T, ID> timeStampDAO;

    public TimeStampController(DAOInterface<T, ID> dao) {
        super(dao);
        if (dao instanceof TimeStampDAO) {
            this.timeStampDAO = (TimeStampDAO<T, ID>) dao;
        } else {
            throw new IllegalArgumentException("Invalid DAO.");
        }
    }
}
