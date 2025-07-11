package org.fabianandiel.entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="address_id",nullable = false,updatable = false)
    private UUID id;
    @Column(name="street",nullable = false,length = 100)
    private String street;
    @Column(name="housenumber",nullable = false,length = 100)
    private String housenumber;
    @Column(name="doornumber",nullable = false,length = 100)
    private String doornumber;
    @Column(name="zip",nullable = false,length = 100)
    private String zip;
    @Column(name="city",nullable = false,length = 100)
    private String city;
    @Column(name="country",nullable = false, length = 100)
    private String country;

}