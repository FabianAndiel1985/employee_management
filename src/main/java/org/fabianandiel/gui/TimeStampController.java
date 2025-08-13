package org.fabianandiel.gui;

import jakarta.persistence.EntityManager;
import javafx.application.Platform;
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
import org.fabianandiel.services.ExecutorServiceProvider;
import org.fabianandiel.services.GUIService;
import org.fabianandiel.services.SceneManager;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

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

    private List<TimeStamp> timeStampsOfCurrentMonth = null;

    private ExecutorService executorService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.personController = new PersonController<>(new PersonDAO());
        this.executorService = ExecutorServiceProvider.getExecutorService();
        executorService.submit(() -> {
            try {
                TimeStamp existingStamp = timeStampController.getTimeStampByDateAndPerson(LocalDate.now(), UserContext.getInstance().getId());
                List<TimeStamp> timeStamps = this.timeStampController.getTimeStampsOfCurrentMonth(UserContext.getInstance().getId(), LocalDate.now());
                this.timeStampsOfCurrentMonth = timeStamps;
                if (existingStamp != null) {
                    this.currentTimeStamp = existingStamp;
                    Platform.runLater(() -> updateUIWithExistingStamp(existingStamp));
                } else {
                    TimeStamp timeStamp = new TimeStamp();
                    timeStamp.setTimeBookingDate(LocalDate.now());
                    timeStamp.setPerson(UserContext.getInstance().getPerson());
                    TimeStamp newTimeStamp = (TimeStamp) this.timeStampController.create(timeStamp);
                    this.currentTimeStamp = newTimeStamp;
                    Platform.runLater(() -> updateUIWithNewTimestamp());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    GUIService.setErrorText(e.getMessage(), this.timeBookingErrorText);
                    if (Constants.LOADING_TIMESTAMP_BY_DATE.equals(e.getMessage())) {
                        this.timeBookingStartTime.setDisable(true);
                        this.timeBookingEndTime.setDisable(true);
                    }
                });
            }
        });
    }

    /**
     * Initalizes the UI with a new timestamp
     */
    private void updateUIWithNewTimestamp() {
        this.timeBookingStartTime.clear();
        this.timeBookingEndTime.clear();
        this.initializeTargetHours();
        this.initializeActualHours();
        this.initializeDifferenceBetweenActualAndTarget();
    }

    /**
     * updates the UI with existing TimeStamp
     *
     * @param existingStamp
     */
    private void updateUIWithExistingStamp(TimeStamp existingStamp) {
        this.timeBookingStartTime.clear();
        this.timeBookingEndTime.clear();
        this.initializeTargetHours();
        this.initializeActualHours();
        this.initializeDifferenceBetweenActualAndTarget();

        if (existingStamp.getTimeBookingStartTime() != null) {
            this.timeBookingStartTime.setText(existingStamp.getTimeBookingStartTime().toString());
            this.timeBookingClockIn.setDisable(true);
        }

        if (existingStamp.getTimeBookingEndTime() != null) {
            this.timeBookingEndTime.setText(existingStamp.getTimeBookingEndTime().toString());
            this.timeBookingClockOut.setDisable(true);
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
        if (this.timeStampsOfCurrentMonth == null) {
            GUIService.setErrorText("No time bookings for the month yet", this.timeBookingErrorText);
            return;
        }
        double workedHoursThisMonth = this.timeStampsOfCurrentMonth.stream().mapToDouble(TimeStamp::getWorkedHours).sum();
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
     * goes back to main menu
     */
    public void goBackToMainView() {
        SceneManager.goBackToMainView(this.timeBookingErrorText);
    }

    /**
     * writes the clock in time in the DB and disables button
     */
    public void clockIn() {
        if (timeBookingStartTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingStartTime() == null) {
            this.executorService.submit(() -> {
                EntityManager em = EntityManagerProvider.getEntityManager();
                try {
                    em.getTransaction().begin();

                    LocalTime time = LocalTime.now();
                    UUID currentTimeStampId = this.currentTimeStamp.getId();
                    //No em.merge() because I alredy have the entity with em.find
                    TimeStamp currentTimeStamp = em.find(TimeStamp.class, currentTimeStampId);
                    currentTimeStamp.setTimeBookingStartTime(time);


                    UUID userId = UserContext.getInstance().getId();
                    //No em.merge() because I alredy have the entity with em.find
                    Person currentUser = em.find(Person.class, userId);
                    currentUser.setStatus(Status.PRESENT);
                    em.getTransaction().commit();
                    Platform.runLater(
                            () -> {
                                UserContext.getInstance().getPerson().setStatus(Status.PRESENT);
                                this.currentTimeStamp.setTimeBookingStartTime(time);
                                this.timeBookingClockIn.setDisable(true);
                                this.timeBookingStartTime.setText(time.toString());
                            }
                    );
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    Platform.runLater(()->{
                        GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.timeBookingErrorText);
                        this.currentTimeStamp.setTimeBookingStartTime(null);
                        this.timeBookingStartTime.clear();
                        this.timeBookingClockIn.setDisable(false);
                    });
                } finally {
                    em.close();
                }
            });
        }
    }


    /**
     * writes the clock out time in the DB and disables button
     */
    public void clockOut() {
        if (!timeBookingStartTime.getText().isEmpty() && timeBookingEndTime.getText().isEmpty() && this.currentTimeStamp.getTimeBookingEndTime() == null) {
            this.executorService.submit(() -> {
                EntityManager em = EntityManagerProvider.getEntityManager();
                try {
                    em.getTransaction().begin();

                    LocalTime time = LocalTime.now();

                    UUID currentTimeStampId = this.currentTimeStamp.getId();
                    //No em.merge because I alredy have the entity with em.find
                    TimeStamp currentTimeStamp = em.find(TimeStamp.class, currentTimeStampId);
                    currentTimeStamp.setTimeBookingEndTime(time);
                    double hours = calculateWorkedHours(currentTimeStamp);
                    currentTimeStamp.setWorkedHours(hours);

                    UUID userId = UserContext.getInstance().getId();
                    //No em.merge because I alredy have the entity with em.find
                    Person currentUser = em.find(Person.class, userId);
                    currentUser.setStatus(Status.ABSENT);

                    em.getTransaction().commit();

                    Platform.runLater(() -> {
                        this.currentTimeStamp.setTimeBookingEndTime(time);
                        this.currentTimeStamp.setWorkedHours(hours);
                        UserContext.getInstance().getPerson().setStatus(Status.ABSENT);
                        this.timeBookingEndTime.setText(time.toString());
                        this.timeBookingClockOut.setDisable(true);
                        this.initializeActualHours();
                        this.initializeDifferenceBetweenActualAndTarget();
                    });
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    Platform.runLater(() -> {
                        GUIService.setErrorText(Constants.PLEASE_CONTACT_SUPPORT, this.timeBookingErrorText);
                        this.currentTimeStamp.setTimeBookingEndTime(null);
                        this.timeBookingEndTime.clear();
                        this.timeBookingClockOut.setDisable(false);
                    });
                } finally {
                    em.close();
                }
            });
        }
        ;
    }


    /**
     * adds the worked hours to the time stamp entity
     *
     * @param currentTimeStamp timeStamp where i want to set the worked hours
     */
    private double calculateWorkedHours(TimeStamp currentTimeStamp) {
        Duration duration = Duration.between(currentTimeStamp.getTimeBookingStartTime(), currentTimeStamp.getTimeBookingEndTime());
        double hours = duration.toMinutes() / 60.0;
        return Math.round(hours * 100.0) / 100.0;
    }
}
