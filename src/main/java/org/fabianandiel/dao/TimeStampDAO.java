package org.fabianandiel.dao;

import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.DAOService;
import org.fabianandiel.services.EntityManagerProvider;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TimeStampDAO<T, ID> extends BaseDAO<T, ID> {

    public TimeStamp findTimeStampByDate(LocalDate queryDate, UUID id){

        List<TimeStamp> timeStamp= DAOService.findItemsWithPropertyOrProperties("SELECT e FROM TimeStamp e WHERE e.timeBookingDate = :param AND e.person.id = :param1 ", TimeStamp.class, EntityManagerProvider.getEntityManager(),queryDate, id);

        System.out.println(timeStamp);
        if(timeStamp.size() == 0)
            return null;

        return timeStamp.getFirst();
    }



}
