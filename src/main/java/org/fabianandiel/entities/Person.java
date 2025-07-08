package org.fabianandiel.entities;

import jakarta.persistence.*;
import lombok.Data;

import org.fabianandiel.constants.Role;
import org.fabianandiel.constants.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table (name="person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="person_id",nullable = false,updatable = false)
    private UUID id;
    @Column(name="firstname",nullable = false)
    private String firstname;
    @Column(name="lastname",nullable = false)
    private String lastname;
    //TODO Annotate connection here
    private UUID address;
    @Column(name="telephone",nullable = false)
    private int telephone;
    @Column(name="email",nullable = false,unique = true)
    private String email;
    @Column(name="password",nullable = false)
    private String password;
    @ManyToOne
    @JoinColumn(name = "superior_id")
    private Person superior;
    //TODO try the subordinates with a set
    @OneToMany(mappedBy = "superior", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Person> subordinates = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
    private Status status;

}

