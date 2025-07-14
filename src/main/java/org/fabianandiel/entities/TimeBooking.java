package org.fabianandiel.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeBooking {

   private LocalDateTime timeBookingStartTime;
   private LocalDateTime timeBookingEndTime;

}
