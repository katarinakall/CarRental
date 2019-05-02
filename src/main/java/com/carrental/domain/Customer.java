package com.carrental.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Customer {
    private String name;
    private String surname;
    private String ssn;

    public Customer(String name, String surname, String ssn) {
        this.name = name;
        this.surname = surname;
        this.ssn = ssn;
    }
}
