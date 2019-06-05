package com.carrental.domain;

import lombok.Data;

@Data
public class Customer{
    private String name;
    private String surname;
    private String ssn;
    private String member;
    private int nrRented;
    private int distanceDriven;

    public Customer() {
    }

    public Customer(String name, String surname, String ssn, int nrRented) {
        this.name = name;
        this.surname = surname;
        this.ssn = ssn;
        this.nrRented = nrRented;
    }

    public Customer(String name, String surname, String ssn, String member, int nrRented, int distanceDriven) {
        this.name = name;
        this.surname = surname;
        this.ssn = ssn;
        this.member = member;
        this.nrRented = nrRented;
        this.distanceDriven = distanceDriven;
    }
}

