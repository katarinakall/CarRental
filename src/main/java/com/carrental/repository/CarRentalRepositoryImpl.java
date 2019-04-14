package com.carrental.repository;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
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
    public List<Car> getAvailableCars(CarType car_type) throws SQLException {
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE car_type = ?")) {
            ps.setString(1, car_type.getDisplayName());
            List<Car> cars = new ArrayList<>();

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new CarRentalRepositoryException("No cars with car type " + car_type);
                else {
                    while (rs.next()) {
                        cars.add(rsCar(rs));
                    }
                    return cars;
                }

            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }
    }

    public List<Car> getAllCars(){
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars")) {
            List<Car> cars = new ArrayList<>();
            while (rs.next()) {
                cars.add(rsCar(rs));
            }
            return cars;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }


    @Override
    public Car addCar(String registrationPlate, CarType carType, int mileage) {
        Car car = new Car(registrationPlate, carType.getDisplayName(), mileage);
        return car;
    }

    private Car rsCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getLong("id"),
                rs.getString("registration_plate"),
                rs.getString("car_type"),
                rs.getInt("mileage")
        );
    }
}
