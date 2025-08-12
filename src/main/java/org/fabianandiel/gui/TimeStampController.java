package org.fabianandiel.gui;

import jakarta.persistence.EntityManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.fabianandiel.constants.Constants;
import org.fabianandiel.constants.Status;
import org.fabianandiel.context.UserContext;
import org.fabianandiel.controller.PersonController;
import org.fabianandiel.dao.PersonDAO;
import org.fabianandiel.dao.TimeStampDAO;
import org.fabianandiel.entities.Person;
import org.fabianandiel.entities.TimeStamp;
import org.fabianandiel.services.EntityManagerProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.List;

public class TimeStampController implements Initializable {

    @FXML
    private Button timeBookingGoBack;

    @FXML
    private Button timeBookingClockIn;

    @FXML
    private Button timeBookingClockOut;

    @FXML
    private Text timeBookingErrorText;

    @FXML
    private TextField timeBookingStartTime;

    @FXML
    private TextField timeBookingEndTime;

    @FXML
    private Text timeBookingTargetHours;

    @FXML
    private Text timeBookingActualHours;

    @FXML
    private Text timeBookingDifference;


    private org.fabianandiel.controller.TimeStampController timeStampController = new org.fabianandiel.controller.TimeStampController(new TimeStampDAO());

    private TimeStamp currentTimeStamp;

    private PersonController personController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.timeBookingStartTime.clear();
        this.timeBookingEndTime.clear();
        this.initializeTargetHours();
        this.initializeActualHours();
        this.initializeDifferenceBetweenActualAndTarget();

        this.personController = new PersonController<>(new PersonDAO());

        TimeStamp existingStamp = timeStampController.getTimeStampByDateAndPerson(LocalDate.now(), UserContext.getInstance().getId());
        if (existingStamp != null) {
            this.currentTimeStamp = existingStamp;
            if (existingStamp.getTimeBookingStartTime() != null)
                this.timeBookingStartTime.setText(existingStamp.getTimeBookingStartTime().toString());

            if (existingStamp.getTimeBookingEndTime() != null)
                this.timeBookingEndTime.setText(existingStamp.getTimeBookingEndTime().toString());

            if (!this.timeBookingStartTime.getText().isEmpty()) {
                this.timeBookingClockIn.setDisable(true);
            }

            if (!this.timeBookingEndTime.getText().isEmpty()) {
                this.timeBookingClockOut.setDisable(true);
            }

        } else {
            TimeStamp timeStamp = new TimeStamp();
            timeStamp.setTimeBookingDate(LocalDate.now());
            timeStamp.setPerson(UserContext.getInstance().getPerson());
            TimeStamp createdTimeStamp = (TimeStamp) this.timeStampController.create(timeStamp);
            this.currentTimeStamp = createdTimeStamp;
        }
    }

    /**
     * Initializes the field between target and actual hours
     */
    private void initializeDifferenceBetweenActualAndTarget() {
        if (!this.timeBookingTargetHours.getText().isBlank() && !this.timeBookingActualHours.getText().isBlank()) {
            int targetHours = Integer.parseInt(this.timeBookingTargetHours.getText());
            double actualHours = Double.parseDouble(this.timeBookingActualHours.getText());
            double difference = Math.round((targetHours - actualHours) * 100.0) / 100.0;
            this.timeBookingDifference.setText(String.valueOf(difference));
        }
    }

    /**
     * Initialize the actual hours worked by the user
     */
    private void initializeActualHours() {
        List<TimeStamp> timeStamps = this.timeStampController.getTimeStampsOfCurrentMonth(UserContext.getInstance().getId(), LocalDate.now());
        if (timeStamps == null) {
            double noBookingsYet = 0.00;
            this.timeBookingActualHours.setText(String.valueOf(noBookingsYet));
            return;
        }
        double workedHoursThisMonth = timeStamps.stream().mapToDouble(TimeStamp::getWorkedHours).sum();
        this.timeBookingActualHours.setText(String.valueOf(workedHoursThisMonth));
    }

    /**
     * initializes the target hours from the beginning of the current month including today
     */
    private void initializeTargetHours() {
        int workdaysOfMonth = this.calculateWorkDaysOfTheMonth();
        int workingHoursAday = UserContext.getInstance().getPerson().getWeek_work_hours() / 5;
        this.timeBookingTargetHours.setText(String.valueOf(workdaysOfMonth * workingHoursAday));
    }

    /**
     * This method calculates how many workdays (Monday to Friday) have occurred from the 1st of the current month up to today.
     *
     * @return amount of workdays
     */
    private int calculateWorkDaysOfTheMonth() {
        int workdays = 0;

        LocalDate yesterday = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();

        LocalDate currentDay = currentMonth.atDay(1);

        while (!currentDay.isAfter(yesterday)) {
            DayOfWeek day = currentDay.getDayOfWeek();
            if (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) {
                workdays++;
            }
            currentDay = currentDay.plusDays(1);
        }

        return workdays;
    }


    /**
     * goes back to main view
     */
    public void goBackToMainView() {
        SceneManager.goBackToMainView(timeBookingErrorText);
    }

    /**
     * writes the clock in time in the DB and disables button
     */
    public void clockIn() {
        if (timeBookingStartTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingStartTime() == null) {
            EntityManager em = EntityManagerProvider.getEntityManager();
            try {
                em.getTransaction().begin();
                LocalTime time = LocalTime.now();
                this.currentTimeStamp.setTimeBookingStartTime(time);
                em.merge(this.currentTimeStamp);
                this.timeBookingClockIn.setDisable(true);
                Person currentUser = UserContext.getInstance().getPerson();
                currentUser.setStatus(Status.PRESENT);
                em.merge(currentUser);
                em.getTransaction().commit();
                this.timeBookingStartTime.setText(time.toString());
            } catch (RuntimeException e) {
                this.currentTimeStamp.setTimeBookingStartTime(null);
                this.timeBookingStartTime.clear();
                this.timeBookingClockIn.setDisable(false);
                e.printStackTrace();
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.timeBookingErrorText);
            } finally {
                em.close();
            }
        }
    }

    /**
     * writes the clock out time in the DB and disables button
     */
    public void clockOut() {
        if (!timeBookingStartTime.getText().isEmpty() && timeBookingEndTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingEndTime() == null) {
            EntityManager em = EntityManagerProvider.getEntityManager();
            try {
                em.getTransaction().begin();
                LocalTime time = LocalTime.now();
                this.currentTimeStamp.setTimeBookingEndTime(time);
                this.updateWorkedHours(this.currentTimeStamp);
                em.merge(this.currentTimeStamp);
                Person currentUser = UserContext.getInstance().getPerson();
                currentUser.setStatus(Status.ABSENT);
                em.merge(currentUser);
                em.getTransaction().commit();
                this.timeBookingEndTime.setText(time.toString());
                this.timeBookingClockOut.setDisable(true);
                this.initializeActualHours();
                this.initializeDifferenceBetweenActualAndTarget();
            } catch (RuntimeException e) {
                this.currentTimeStamp.setTimeBookingEndTime(null);
                this.timeBookingEndTime.clear();
                this.timeBookingClockOut.setDisable(false);
                e.printStackTrace();
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.timeBookingErrorText);
            }
        }


    }

    /**
     * adds the worked hours to the time stamp entity
     *
     * @param currentTimeStamp timeStamp where i want to set the worked hours
     */
    private void updateWorkedHours(TimeStamp currentTimeStamp) {
        Duration duration = Duration.between(this.currentTimeStamp.getTimeBookingStartTime(), this.currentTimeStamp.getTimeBookingEndTime());
        double hours = duration.toMinutes() / 60.0;
        hours = Math.round(hours * 100.0) / 100.0;
        currentTimeStamp.setWorkedHours(hours);
    }


}
