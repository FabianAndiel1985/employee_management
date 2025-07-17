package org.fabianandiel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.fabianandiel.constants.RequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name="request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="request_id",nullable = false,updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;

    @NotNull(message = "start date can not be empty")
    @FutureOrPresent(message = "Invalid start date")
    @Column(name="start_date",nullable = false)
    @EqualsAndHashCode.Include
    private LocalDate startDate;

    @NotNull(message = "end date can not be empty")
    @FutureOrPresent(message = "Invalid end date")
    @Column(name="end_date",nullable = false)
    @EqualsAndHashCode.Include
    private LocalDate endDate;

    @Size(max = 255, message = "Notes can be at most 255 characters long")
    @Column(name="notes", length = 255)
    private String notes;

    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name="creation_date",nullable = false)
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person creator;

    //TODO rework or leave to string
    @Override
    public String toString() {
        return "Request{" +
                "status=" + status +
                ", creationDate=" + creationDate +
                ", creator=" + creator +
                '}';
    }
}
