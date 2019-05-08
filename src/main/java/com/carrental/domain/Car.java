package com.carrental.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Car {
    private @Id @GeneratedValue int id;
    private String registrationPlate;
    private String carType;
    private int mileage;
    private boolean available;
    private boolean clean;
    private int timesRented;

    public Car(){}

    public Car(String registrationPlate, String carType, int mileage) {
        this.registrationPlate = registrationPlate;
        this.carType = carType;
        this.mileage = mileage;
    }

    public Car(int id, String registrationPlate, String carType, int mileage, boolean clean, int timesRented) {
        this.id = id;
        this.registrationPlate = registrationPlate;
        this.carType = carType;
        this.mileage = mileage;
        this.clean = clean;
        this.timesRented = timesRented;
    }
}
