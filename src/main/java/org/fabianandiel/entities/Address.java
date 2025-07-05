package org.fabianandiel.entities;



import lombok.Data;

import java.util.UUID;

@Data
//TODO add rest
public class Address {

    //which package to take UUID from?
    private UUID id;
    private String street;
    private String housenumber;
    private String doornumber;
    private String zip;
    private String city;
    private String country;

}
