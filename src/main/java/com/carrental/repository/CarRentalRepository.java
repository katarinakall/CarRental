package com.carrental.repository;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CarRentalRepository {

    List<CarType> getCarTypes();
    List<Car> getAvailableCars();
    Car addCar(String registrationPlate, CarType carType, int mileage);


}

