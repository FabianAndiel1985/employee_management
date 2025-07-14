package org.fabianandiel.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name="time_stamp")
public class TimeStamp {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name="time_stamp_id",nullable = false,updatable = false)
   private UUID id;

   @Column(name="start_time")
   private LocalDateTime timeBookingStartTime;

   @Column(name="end_time")
   private LocalDateTime timeBookingEndTime;

   @ManyToOne
   @JoinColumn(name = "person_id")
   private Person person;
}
