package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.ReturnRequest;
import com.carrental.domain.Booking;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.domain.Customer;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface CarRentalRepository {

    CarType[] getCarTypes();
    List<Car> getAllCars();
    List<Car> getAvailableCars(CarType car_type);
    List<Booking> getActiveBookings();
    void selectCar(int carId, String ssn);
    void addBooking(RentalRequest rentalRequest);
    String getCustomerSsn(RentalRequest rentalRequest);
    void returnCar(ReturnRequest request, String bookingNumber);
    void toggleCarAvailability(int carId, boolean available);
    void toggleCarCleaning(int carId, boolean clean);
    void toggleService(int carId, boolean service);
    void updateTimesRented(int carId);
    void updateCarMileage(int carId, int mileage);
    void removeCar(int carId);
    Booking getBooking(String bookingNumber);
    Car getCar(int id);
    List<Customer> getAllCustomers();
    List<Booking> getAllBookingsForCustomer(String ssn);
}

