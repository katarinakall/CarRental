package com.carrental.repository;

import com.carrental.RentalRequest;
import com.carrental.ReturnRequest;
import com.carrental.domain.Booking;
import com.carrental.domain.Car;
import com.carrental.domain.CarType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.carrental.domain.CarType.MINIBUS;
import static com.carrental.domain.CarType.SMALL;
import static com.carrental.domain.CarType.VAN;
import static java.time.temporal.ChronoUnit.DAYS;


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
    public Car getCar(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM CARS WHERE id = ?")) {
                ps.setInt(1, id);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) throw new CarRentalRepositoryException("No car found with id " + id);
                else return rsCar(rs);
                }
        } catch (SQLException e) {
            throw new CarRentalRepositoryException(e);
        }
    }


        @Override
        public void selectCar ( int carId, String ssn){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE rent_cars SET car_id = ? WHERE customer_ssn = ?")) {
                ps.setInt(1, carId);
                ps.setString(2, ssn);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }


        @Override
        public void toggleCarAvailability ( int carId, boolean available){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE cars SET  AVAILABLE = ? WHERE id = ?")) {
                ps.setBoolean(1, available);
                ps.setInt(2, carId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }

        @Override
        public double calculateCost (ReturnRequest returnRequest, LocalDate pickUpDate, Car car){
            double rentalCost = 0;
            double baseDayRental = 0;
            double kmPrice = 0;
            int numberOfDays = calculateNumberOfDays(pickUpDate, returnRequest.getReturnDate());
            int numberOfKm = calculateNumberOfKm(car.getMileage(), returnRequest.getMileageAtReturn());

            switch (car.getCarType()) {
                case "Small":
                    rentalCost = baseDayRental * numberOfDays;
                    break;
                case "Van":
                    rentalCost = baseDayRental * numberOfDays * 1.2 + kmPrice * numberOfKm;
                    break;
                case "Minibus":
                    rentalCost = baseDayRental * numberOfDays * 1.7 + (kmPrice * numberOfKm * 1.5);
                    break;
            }
            return rentalCost;
        }


        public int calculateNumberOfKm ( int kmAtPickup, int kmAtReturn){
            return kmAtReturn - kmAtPickup;
        }

        public int calculateNumberOfDays (LocalDate pickupDate, LocalDate returnDate){
            return (int) DAYS.between(pickupDate, returnDate);
        }


        @Override
        public List<Booking> getActiveBookings () {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT CUSTOMER_SSN, CAR_ID, PICK_UP_DATE, BOOKING_NUMBER FROM RENT_CARS WHERE active='TRUE'")) {
                List<Booking> bookings = new ArrayList<>();
                while (rs.next()) {
                    bookings.add(rsBooking(rs));
                }
                return bookings;
            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }

        @Override
        public Booking getBooking (String bookingNumber){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT CUSTOMER_SSN, CAR_ID, PICK_UP_DATE, BOOKING_NUMBER FROM RENT_CARS WHERE booking_number = ?")) {
                ps.setString(1, bookingNumber);
                try(ResultSet rs = ps.executeQuery()){
                    if(!rs.next()) throw new CarRentalRepositoryException("No booking found with booking number " + bookingNumber);
                    else return rsBooking(rs);
                }
            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }

        @Override
        public void addBooking (RentalRequest rentalRequest){
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
                throw new CarRentalRepositoryException(e);
            }
        }

        @Override
        public String getCustomerSsn (RentalRequest rentalRequest){
            String customerSsn = (rentalRequest.getDateOfBirth().toString()).replace("-", "") + "-" + rentalRequest.getLastFourDigits();
            return customerSsn;
        }

        @Override
        public void returnCar (ReturnRequest request, String bookingNumber){
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("UPDATE rent_cars SET return_date = ?, return_time = ?, active = ? WHERE booking_number = ?")) {
                ps.setDate(1, Date.valueOf(request.getReturnDate()));
                ps.setTime(2, Time.valueOf(request.getReturnTime()));
                ps.setBoolean(3, false);
                ps.setString(4, bookingNumber);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new CarRentalRepositoryException(e);
            }
        }

        private String generateBookingNumber (RentalRequest request){
            String bookingNumber = request.getPickUpDate().toString().replace("-", "") + request.getLastFourDigits();
            return bookingNumber;
        }

        private Booking rsBooking (ResultSet rs) throws SQLException {
            return new Booking(
                    rs.getString("customer_ssn"),
                    rs.getInt("car_id"),
                    rs.getDate("pick_up_date"),
                    rs.getString("booking_number")
            );
        }

        private Car rsCar (ResultSet rs) throws SQLException {
            return new Car(
                    rs.getInt("id"),
                    rs.getString("registration_plate"),
                    rs.getString("car_type"),
                    rs.getInt("mileage")
            );
        }
    }
