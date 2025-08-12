package org.fabianandiel.controller;

import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.interfaces.DAOInterface;
import java.time.LocalDate;
import java.util.List;
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

    /**
     * get timestamp by a certain date and person
     * @param queryDate date of the timestamp you want to have
     * @param id of the person you want to have the timestamp of
     * @return timestamp of the certain date and person
     */
    public TimeStamp getTimeStampByDateAndPerson(LocalDate queryDate, UUID id){
        try {
            return timeStampDAO.findTimeStampByDate(queryDate, id);
        } catch (RuntimeException e) {
            throw e;
        }
    }


    /**
     * gets the timestamps of a certain person up to a certain date of the month
     * @param id of the person you want to have the timestamps of
     * @param upToDate date of the current month
     * @return timestamps of a certain person up to a certain date of the month
     */
    public List<TimeStamp> getTimeStampsOfCurrentMonth(UUID id, LocalDate upToDate) {
        try {
            return this.timeStampDAO.getTimeStampsOfCurrentMonth(id, upToDate);
        } catch (RuntimeException e) {
            throw e;
        }
    }



}
