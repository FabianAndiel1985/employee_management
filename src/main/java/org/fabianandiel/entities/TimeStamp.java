package org.fabianandiel.entities;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table (name="time_stamp")
public class TimeStamp {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name="time_stamp_id",nullable = false,updatable = false)
   @EqualsAndHashCode.Include
   private UUID id;

   @Column(name="start_time")
   private LocalTime timeBookingStartTime;

   @Column(name="end_time")
   private LocalTime timeBookingEndTime;

   @Column(name="date")
   private LocalDate timeBookingDate;

   @Column(name="worked_hours")
   private double workedHours;

   @ManyToOne
   @JoinColumn(name = "person_id")
   private Person person;
}
