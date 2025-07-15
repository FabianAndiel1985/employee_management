package org.fabianandiel.controller;

import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.interfaces.DAOInterface;
import java.time.LocalDate;
import java.util.UUID;


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

    public TimeStamp findTimeStampByDate(LocalDate queryDate, UUID id){
        return timeStampDAO.findTimeStampByDate(queryDate, id);
    }


}
