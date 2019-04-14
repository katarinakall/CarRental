package com.carrental.repository;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import static com.carrental.domain.CarType.MINIBUS;
import static com.carrental.domain.CarType.SMALL;
import static com.carrental.domain.CarType.VAN;

@Component
public class CarRentalRepositoryImpl implements CarRentalRepository {

    @Autowired
    private DatabaseConnection dbConnection;

    public CarRentalRepositoryImpl(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public List<CarType> getCarTypes() {
        List<CarType> carTypes = new ArrayList<>();
        carTypes.add(SMALL);
        carTypes.add(VAN);
        carTypes.add(MINIBUS);
        return carTypes;
    }

    @Override
    public List<Car> getAvailableCars() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Car car = new Car("ABC 123", CarType.VAN, 0);
        Car car1 = new Car("CDE 123", CarType.VAN, 0);
        Car car2 = new Car("EFG 123", CarType.VAN, 0);
        Car car3 = new Car("ABC 123", CarType.SMALL, 0);
        Car car4 = new Car("CDE 123", CarType.SMALL, 0);
        Car car5 = new Car("EFG 123", CarType.SMALL, 0);
        Car car6 = new Car("ABC 123", CarType.MINIBUS, 0);
        Car car7 = new Car("CDE 123", CarType.MINIBUS, 0);
        Car car8 = new Car("EFG 123", CarType.MINIBUS, 0);
        return null;
    }

    @Override
    public Car addCar(String registrationPlate, CarType carType, int mileage) {
        Car car = new Car(registrationPlate, carType, mileage);
        return car;
    }
}
