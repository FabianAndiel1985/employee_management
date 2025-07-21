package org.fabianandiel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Firstname can’t be blank") //Jakarta Valdiation
    @Size(max = 50, message = "Firstname can be at most 50 characters") //Jakarta Valdiation
    @Column(name="firstname",nullable = false,length = 50)
    private String firstname;


    @NotBlank(message = "Lastname can’t be blank") //Jakarta Valdiation
    @Size(max = 50, message = "Lastname can be at most 50 characters") //Jakarta Valdiation
    @Column(name="lastname",nullable = false,length = 50)
    private String lastname;

    @NotNull(message = "Address can`t be empty") //Jakarta Valdiation
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @NotNull(message = "Telephone can`t be empty") //Jakarta Valdiation
    @Min(value = 100, message = "Telephone must be a valid number") //Jakarta Valdiation
    @Column(name="telephone",nullable = false)
    private int telephone;


    @NotBlank(message = "Email can’t be empty") //Jakarta Valdiation
    @Email(message = "Email must be valid") //Jakarta Valdiation
    @Column(name="email",nullable = false,unique = true)
    @EqualsAndHashCode.Include
    private String email;

    @NotBlank(message = "Username can’t be empty") //Jakarta Valdiation
    @Size(max = 50, message = "Username can be at most 50 characters") //Jakarta Valdiation
    @Column(name="username",nullable = false,unique = true, length=50)
    @EqualsAndHashCode.Include
    private String username;

    @NotBlank(message = "Password can`t be empty") //Jakarta Valdiation
    @Size(max = 50, message = "Password can be at most 50 characters") //Jakarta Valdiation
    @Column(name="password",nullable = false,length=50)
    private String password;

    @ManyToOne
    @JoinColumn(name = "superior_id")
    private Person superior;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL)
    private List<TimeStamp> timeStamps = new ArrayList<>();

    @OneToMany(mappedBy = "superior", cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.EAGER)
    private Set<Person> subordinates = new HashSet<>();

    @Size(min = 1, message = "Employee must have at least role employee")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "person_id")
    )
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(mappedBy = "creator", fetch = FetchType.EAGER)
    List<Request> requests = new ArrayList<>();


    //TODO rework toString();
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

