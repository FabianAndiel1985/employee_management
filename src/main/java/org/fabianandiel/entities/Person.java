package org.fabianandiel.entities;

import jakarta.persistence.*;
import lombok.*;

import org.fabianandiel.constants.Role;
import org.fabianandiel.constants.Status;

import java.util.*;


@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table (name="person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="person_id",nullable = false,updatable = false)
    @EqualsAndHashCode.Include
    private UUID id;
    @Column(name="firstname",nullable = false)
    private String firstname;
    @Column(name="lastname",nullable = false)
    private String lastname;
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    @Column(name="telephone",nullable = false)
    private int telephone;
    @Column(name="email",nullable = false,unique = true)
    @EqualsAndHashCode.Include
    private String email;
    @Column(name="username",nullable = false,unique = true)
    @EqualsAndHashCode.Include
    private String username;
    @Column(name="password",nullable = false)
    private String password;
    @ManyToOne
    @JoinColumn(name = "superior_id")
    private Person superior;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<TimeStamp> timeStamps = new ArrayList<>();

    @OneToMany(mappedBy = "superior", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Person> subordinates = new HashSet<>();

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "person_id")
    )
    @Column(name = "role")
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;


    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
    List<Request> requests;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== Person ===========\n");
        sb.append("ID: ").append(id).append("\n");
        sb.append("Firstname: ").append(firstname).append("\n");
        sb.append("Lastname: ").append(lastname).append("\n");
        sb.append("Address: ").append(address).append("\n");
        sb.append("Telephone: ").append(telephone).append("\n");
        sb.append("Email: ").append(email).append("\n");
        sb.append("Roles: ").append(roles != null ? roles.stream().map(Role::name).toList() : "[]").append("\n");
        sb.append("Subordinates: ").append(subordinates != null ? subordinates.stream().map(Person::getId).toList() : "[]").append("\n");
        sb.append("Status: ").append(status).append("\n");
        sb.append("================================");
        return sb.toString();
    }
}

