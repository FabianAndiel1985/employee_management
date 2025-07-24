package org.fabianandiel.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Street can not be empty") //Jakarta Valdiation
    @Size(max = 100, message = "Street can be at most 50 characters") //Jakarta Valdiation
    @Column(name="street",nullable = false,length = 100)
    private String street;

    @NotBlank(message = "Housenumber can not be empty") //Jakarta Valdiation
    @Size(max = 50, message = "Housenumber can be at most 50 characters") //Jakarta Valdiation
    @Column(name="housenumber",nullable = false,length = 50)
    private String housenumber;

    @NotBlank(message = "Doornumber can not be empty") //Jakarta Valdiation
    @Size(max = 50, message = "Doornumber can be at most 50 characters") //Jakarta Valdiation
    @Column(name="doornumber",nullable = false,length = 50)
    private String doornumber;

    @NotBlank(message = "ZIP can not be empty") //Jakarta Valdiation
    @Size(max = 50, message = "ZIP can be at most 50 characters") //Jakarta Valdiation
    @Column(name="zip",nullable = false,length = 50)
    private String zip;

    @NotBlank(message = "City can not be empty") //Jakarta Valdiation
    @Size(max = 50, message = "City can be at most 50 characters") //Jakarta Valdiation
    @Column(name="city",nullable = false,length = 50)
    private String city;

    @NotBlank(message = "Country can not be empty") //Jakarta Valdiation
    @Size(max = 100, message = "Country can be at most 50 characters") //Jakarta Valdiation
    @Column(name="country",nullable = false, length = 100)
    private String country;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(street)
                .append(" ")
                .append(housenumber)
                .append("/")
                .append(doornumber)
                .append(", ")
                .append(zip)
                .append(" ")
                .append(city)
                .append(", ")
                .append(country);
        return sb.toString();
    }

}