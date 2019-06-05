package com.carrental.repository;

import com.carrental.domain.Log;
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
import java.time.LocalDate;
import java.time.LocalTime;
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

            String log = "Customer with ssn: " + ssn + " rented car with id: " + carId;
            insertLog(LocalDate.now(), LocalTime.now(), ssn, carId, log);

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

            if (clean) {
                String log = "Car with id: " + carId + " has been cleaned.";
                insertLog(LocalDate.now(), LocalTime.now(), null, carId, log);
            }
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

            if (!service) {
                String log = "Car with id: " + carId + " has been sent to service.";
                insertLog(LocalDate.now(), LocalTime.now(), null, carId, log);
            }
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating service variable for car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void updateTimesRented(int carId) {
        int timesRented = getCar(carId).getTimesRented();
        timesRented = timesRented + 1;
        if (timesRented % 3 == 0 && timesRented > 0) {
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

            String log = "Car with id: " + carId + " has been removed.";
            insertLog(LocalDate.now(), LocalTime.now(), null, carId, log);

        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when deleting car with car id: " + carId + ". " + e);
        }
    }

    @Override
    public void addNewCar(Car car) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO cars(registration_plate,car_type,mileage,available,clean,times_rented,service) VALUES (?,?,?,?,?,?,?)")) {
            ps.setString(1, registrationPlate(car.getRegistrationPlate()));
            ps.setString(2, carType(car.getCarType()));
            ps.setInt(3, car.getMileage());
            ps.setBoolean(4, true);
            ps.setBoolean(5, true);
            ps.setInt(6, 0);
            ps.setBoolean(7, false);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when adding new car. " + e);
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

            Booking booking = getBooking(bookingNumber);

            String log = "Customer with ssn: " + booking.getCustomerSSN() + " have returned car with id: " + booking.getCarId();
            insertLog(LocalDate.now(), LocalTime.now(), booking.getCustomerSSN(), booking.getCarId(), log);


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
    public Customer getCustomer(String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers where ssn = ?")) {
            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()){
                return null;
            }
            Customer customer = rsCustomer(rs);
            return customer;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting customer with ssn: " + ssn + ". " + e);
        }
    }

    @Override
    public void addNewCustomer(String name, String surname, String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO customers(ssn, name, surname, nr_rented, distance_driven) VALUES (?,?,?,?,?) ")) {
            ps.setString(1, ssn);
            ps.setString(2, name);
            ps.setString(3, surname);
            ps.setInt(4, 1);
            ps.setInt(5, 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when adding new customer with ssn: " + ssn + ". " + e);
        }
    }

    @Override
    public void updateCustomersMemberStatus(String ssn, String member) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE customers SET member = ? WHERE ssn = ?")) {
            ps.setString(1, member);
            ps.setString(2, ssn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating customers member status." + e);
        }
    }

    @Override
    public void updateCustomersNrRent(int nrRented, int distanceDriven, String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE customers SET nr_rented = ?, distance_driven = ? WHERE ssn = ?")){
            ps.setInt(1, nrRented);
            ps.setInt(2, distanceDriven);
            ps.setString(3, ssn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when updating times rented for customer with ssn: " + ssn + ". " + e);
        }
    }

    @Override
    public String getCustomerSsn(RentalRequest rentalRequest) {
        String customerSsn = (rentalRequest.getDateOfBirth() + "-" + rentalRequest.getLastFourDigits());
        return customerSsn;
    }

    @Override
    public List<Log> getAllLogs() {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * from events")) {
            List<Log> logs = new ArrayList<>();
            while (rs.next()) {
                logs.add(rsLog(rs));
            }
            return logs;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting all logs. " + e);
        }
    }

    @Override
    public List<Log> getCustomersLogs(String ssn) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM events WHERE customer_ssn=?")) {
            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();
            List<Log> logs = new ArrayList<>();
            while (rs.next()) {
                logs.add(rsLog(rs));
            }
            return logs;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting logs for customer with personal identification number: " + ssn + ". " + e);
        }
    }

    @Override
    public List<Log> getCarLogs(int carId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM events WHERE car_id=?")) {
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            List<Log> logs = new ArrayList<>();
            while (rs.next()) {
                logs.add(rsLog(rs));
            }
            return logs;
        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when getting logs for car with id: " + carId + ". " + e);
        }
    }

    private void insertLog(LocalDate date, LocalTime time, String customer_ssn, int car_id, String log) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO events(log_date, log_time, customer_ssn, car_id, log) VALUES (?,?,?,?,?)")) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(time));
            ps.setString(3, customer_ssn);
            ps.setInt(4, car_id);
            ps.setString(5, log);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new CarRentalRepositoryException("Error when inserting log to database. " + e);
        }
    }

    private String registrationPlate(String registrationPlate) {
        String str = registrationPlate.toUpperCase();
        str = new StringBuilder(str).insert(str.length() - 3, ' ').toString();
        return str;
    }

    private String carType(String carType) {
        char c = carType.charAt(0);
        String str = carType.substring(1).toLowerCase();
        return c + str;
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
                rs.getString("ssn"),
                rs.getString("member"),
                rs.getInt("nr_rented"),
                rs.getInt("distance_driven")
        );
    }

    private Log rsLog(ResultSet rs) throws SQLException {
        return new Log(
                rs.getInt("id"),
                rs.getDate("log_date"),
                rs.getTime("log_time"),
                rs.getString("customer_ssn"),
                rs.getInt("car_id"),
                rs.getString("log")
        );
    }
}
