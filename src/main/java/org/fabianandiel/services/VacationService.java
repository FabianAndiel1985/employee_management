package org.fabianandiel.services;

import org.fabianandiel.controller.PersonController;
import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.Request;

public class VacationService {

    /**
     * gets the new amount of remaining days - deducting the requests days from the already existing remaining days
     * @param request new request of the employee
     * @return the new amount of the remaining days
     */
    public static short getRemainingDays(Request request) {
        short daysCurrentRequest = (short) (request.getEndDate().toEpochDay() - request.getStartDate().toEpochDay()+1);
        return (short) (request.getCreator().getVacation_remaining() - daysCurrentRequest);
    }



    /**
     * Updates the amount of remaining vacation days the request creator has
     * @param request request of the creator
     * @param remainingDays new amount of remaining days
     * @param personController the person controller with whom I can update the person
     * @throws RuntimeException if updating the person fails
     */
    public static void updateRemainingVacation(Request request, short remainingDays, PersonController personController) {
        try{
            Person person = request.getCreator();
            person.setVacation_remaining(remainingDays);
            personController.update(person);
        } catch (RuntimeException e) {
            throw e;
        }

    }


}
