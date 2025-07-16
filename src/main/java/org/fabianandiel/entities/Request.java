package org.fabianandiel.entities;

import jakarta.persistence.*;
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

    @Column(name="start_date",nullable = false)
    private LocalDate startDate;
    @Column(name="end_date",nullable = false)
    private LocalDate endDate;
    @Column(name="notes")
    private String notes;
    @Column(name="status",nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    @Column(name="creation_date",nullable = false)
    private LocalDateTime creationDate;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person creator;

    @Override
    public String toString() {
        return "Request{" +
                "status=" + status +
                ", creationDate=" + creationDate +
                ", creator=" + creator +
                '}';
    }
}
