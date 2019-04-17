package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Component
public class CarRentalRepositoryImpl implements CarRentalRepository {

    @Autowired
    private DataSource dataSource;

    public CarRentalRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CarType[] getCarTypes() {
        CarType[] carTypes = CarType.values();
        return carTypes;
    }

    @Override
    public List<Car> getAvailableCars(CarType car_type) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cars WHERE car_type = ? AND available= 'TRUE'")) {
            ps.setString(1, car_type.getDisplayName());
            ResultSet rs = ps.executeQuery();
            List<Car> cars = new ArrayList<>();

            while (rs.next()) cars.add(rsCar(rs));
            return cars;

        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    public List<Car> getAllCars() {
        try (Connection conn = dataSource.getConnection();
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

    @Override
    public void addBooking(RentalRequest rentalRequest) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO rent_cars(pick_up_date, pick_up_time) VALUES (?,?)")){
            ps.setDate(1, Date.valueOf(rentalRequest.getPickUpDate()));
            ps.setTime(2, Time.valueOf(rentalRequest.getPickUpTime()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }

    @Override
    public void addCustomer(RentalRequest rentalRequest) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO customers(bday, last_four_digits) VALUES (?,?)")){
            ps.setDate(1, Date.valueOf(rentalRequest.getPickUpDate()));
            ps.setInt(2, rentalRequest.getLastFourDigits());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
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
