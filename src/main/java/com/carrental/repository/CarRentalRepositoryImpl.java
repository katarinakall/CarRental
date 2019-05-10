package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.ReturnRequest;
import com.carrental.domain.Booking;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;

import com.carrental.domain.Customer;
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
            throw new CarRentalRepositoryException("Error when getting all cars available. " + e);
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
            throw new CarRentalRepositoryException("Error when getting all cars. " + e);
        }
    }

    @Override
    public Car getCar(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM CARS WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new CarRentalRepositoryException("No car found with id " + id);
                else return rsCar(rs);
            }
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting car with car id: " + id + ". " + e);
        }
    }

    @Override
    public void selectCar(int carId, String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE rent_cars SET car_id = ? WHERE customer_ssn = ?")) {
            ps.setInt(1, carId);
            ps.setString(2, ssn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when selecting car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void toggleCarAvailability(int carId, boolean available) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  AVAILABLE = ? WHERE id = ?")) {
            ps.setBoolean(1, available);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when changing car availability for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void toggleCarCleaning(int carId, boolean clean) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  CLEAN = ? WHERE id = ?")) {
            ps.setBoolean(1, clean);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating clean variable for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void toggleService(int carId, boolean service) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  SERVICE = ? WHERE id = ?")) {
            ps.setBoolean(1, service);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating service variable for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void updateTimesRented(int carId) {
        int timesRented = getCar(carId).getTimesRented();
        timesRented = timesRented + 1;
        if(timesRented % 3 == 0 && timesRented > 0){
            toggleService(carId, true);
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  times_rented = ? WHERE id = ?")) {
            ps.setInt(1, timesRented);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating times rented for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void updateCarMileage(int carId, int mileage) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  MILEAGE = ? WHERE id = ?")) {
            ps.setInt(1, mileage);
            ps.setInt(2, carId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating car mileage for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void removeCar(int carId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
            ps.setInt(1, carId);
            ps.executeUpdate();
    } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when deleting car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public List<Booking> getActiveBookings() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CUSTOMER_SSN, CAR_ID, PICK_UP_DATE, BOOKING_NUMBER FROM RENT_CARS WHERE active='TRUE'")) {
            List<Booking> bookings = new ArrayList<>();
            while (rs.next()) {
                bookings.add(rsBooking(rs));
            }
            return bookings;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting all active bookings. " + e);
        }
    }

    @Override
    public List<Booking> getAllBookingsForCustomer(String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT CUSTOMER_SSN, CAR_ID, PICK_UP_DATE, BOOKING_NUMBER FROM RENT_CARS WHERE customer_ssn=?")) {
            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();
            List<Booking> bookings = new ArrayList<>();
            while (rs.next()) {
                bookings.add(rsBooking(rs));
            }
            return bookings;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting all bookings for customer with personal identification number: " + ssn + ". " + e);
        }

    }

    @Override
    public Booking getBooking(String bookingNumber) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT CUSTOMER_SSN, CAR_ID, PICK_UP_DATE, BOOKING_NUMBER FROM RENT_CARS WHERE booking_number = ?")) {
            ps.setString(1, bookingNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next())
                    throw new CarRentalRepositoryException("No booking found with booking number: " + bookingNumber);
                else return rsBooking(rs);
            }
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting booking with booking number: " + bookingNumber + ". " + e);
        }
    }

    @Override
    public void addBooking(RentalRequest rentalRequest) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO rent_cars(customer_ssn,pick_up_date, pick_up_time, booking_number, active) VALUES (?,?,?,?,?)")) {
            String ssn = getCustomerSsn(rentalRequest);
            String bookingNumber = generateBookingNumber(rentalRequest);
            ps.setString(1, ssn);
            ps.setDate(2, Date.valueOf(rentalRequest.getPickUpDate()));
            ps.setTime(3, Time.valueOf(rentalRequest.getPickUpTime()));
            ps.setString(4, bookingNumber);
            ps.setBoolean(5, true);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when adding booking. " + e);
        }
    }

    @Override
    public void returnCar(ReturnRequest request, String bookingNumber) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE rent_cars SET return_date = ?, return_time = ?, active = ? WHERE booking_number = ?")) {
            ps.setDate(1, Date.valueOf(request.getReturnDate()));
            ps.setTime(2, Time.valueOf(request.getReturnTime()));
            ps.setBoolean(3, false);
            ps.setString(4, bookingNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when returning car with booking number: " + bookingNumber + ". " + e);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            List<Customer> customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(rsCustomer(rs));
            }
            return customers;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting all customers. " + e);
        }
    }

    @Override
    public String getCustomerSsn(RentalRequest rentalRequest) {
        String customerSsn = (rentalRequest.getDateOfBirth().toString()).replace("-", "") + "-" + rentalRequest.getLastFourDigits();
        return customerSsn;
    }

    private String generateBookingNumber(RentalRequest request) {
        String bookingNumber = request.getPickUpDate().toString().replace("-", "") + request.getLastFourDigits();
        return bookingNumber;
    }

    private Booking rsBooking(ResultSet rs) throws SQLException {
        return new Booking(
                rs.getString("customer_ssn"),
                rs.getInt("car_id"),
                rs.getDate("pick_up_date"),
                rs.getString("booking_number")
        );
    }

    private Car rsCar(ResultSet rs) throws SQLException {
        return new Car(
                rs.getInt("id"),
                rs.getString("registration_plate"),
                rs.getString("car_type"),
                rs.getInt("mileage"),
                rs.getBoolean("clean"),
                rs.getInt("times_rented"),
                rs.getBoolean("service")
        );
    }

    private Customer rsCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("ssn")
        );
    }
}
