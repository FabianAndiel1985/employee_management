package org.fabianandiel.dao;

import org.fabianandiel.constants.Constants;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TimeStampDAO<T, ID> extends BaseDAO<T, ID> {


    /**
     * get timestamp by a certain date and person
     *
     * @param queryDate date of the timestamp you want to have
     * @param id        of the person you want to have the timestamp of
     * @return timestamp of the certain date and person
     */
    public TimeStamp findTimeStampByDate(LocalDate queryDate, UUID id) {
        try {
           List<TimeStamp> timeStamp = DAOService.findItemsWithPropertyOrProperties("SELECT e FROM TimeStamp e WHERE e.timeBookingDate = :param AND e.person.id = :param1 ", TimeStamp.class, EntityManagerProvider.getEntityManager(), queryDate, id);
            if (timeStamp.size() == 0)
                return null;
            return timeStamp.getFirst();
        } catch (Exception e) {
            throw new RuntimeException(Constants.LOADING_TIMESTAMP_BY_DATE,e);
        }
    }

    /**
     * gets the timestamps of a certain person up to a certain date of the month
     *
     * @param id       of the person you want to have the timestamps of
     * @param upToDate date of the current month
     * @return timestamps of a certain person up to a certain date of the month
     */
    public List<TimeStamp> getTimeStampsOfCurrentMonth(UUID id, LocalDate upToDate) {
        LocalDate firstDayOfCurrentMonth = upToDate.withDayOfMonth(1);
        try {
             List<TimeStamp> timeStamps = DAOService.findItemsWithPropertyOrProperties("SELECT e FROM TimeStamp e WHERE e.timeBookingDate BETWEEN :param AND :param1 AND e.person.id = :param2", TimeStamp.class, EntityManagerProvider.getEntityManager(), firstDayOfCurrentMonth, upToDate, id);
             if (timeStamps.size() == 0)
                return null;
            return timeStamps;
        } catch (Exception e) {
            throw new RuntimeException("Error loading time stamps of current month",e);
        }
    }
}
