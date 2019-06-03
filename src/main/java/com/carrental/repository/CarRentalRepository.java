package com.carrental.repository;

import com.carrental.domain.Log;
import com.carrental.RentalRequest;
import com.carrental.ReturnRequest;
import com.carrental.domain.Booking;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.domain.Customer;
import org.springframework.stereotype.Repository;

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
    void addNewCar(Car car);
    Booking getBooking(String bookingNumber);
    Car getCar(int id);
    List<Customer> getAllCustomers();
    List<Booking> getAllBookingsForCustomer(String ssn);
    List<Log> getAllLogs();
    List<Log> getCustomersLogs(String ssn);
}

