package com.carrental.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class Car {
    private @Id @GeneratedValue Long id;
    private String registrationPlate;
    private CarType carType;
    private int mileage;

    public Car(){}

    public Car(String registrationPlate, CarType carType, int mileage) {
        this.registrationPlate = registrationPlate;
        this.carType = carType;
        this.mileage = mileage;
    }
}
