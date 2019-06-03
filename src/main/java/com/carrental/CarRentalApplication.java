package com.carrental;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootApplication
public class CarRentalApplication implements CommandLineRunner {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(CarRentalApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        jdbcTemplate.execute("DROP TABLE cars IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE cars(id SERIAL, registration_plate VARCHAR(255), car_type VARCHAR(255), mileage INT, available BOOLEAN, clean BOOLEAN, times_rented INT, service BOOLEAN)");

        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(id SERIAL, ssn VARCHAR(255), name VARCHAR(255), surname VARCHAR(255))");

        jdbcTemplate.execute("DROP TABLE rent_cars IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE rent_cars(id SERIAL, customer_ssn VARCHAR(255), car_id INT, pick_up_date DATE, pick_up_time TIME, return_date DATE, return_time TIME, booking_number VARCHAR(255), active BOOLEAN)");

        jdbcTemplate.execute("DROP TABLE events IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE events(id SERIAL, log_date DATE, log_time TIME, customer_ssn VARCHAR(255), car_id INT, log VARCHAR(255))");


        insertCar("ABC 123", "Small", 300, true, false, 3, true);
        insertCar("CDE 123", "Small", 100, true, false, 1, false);
        insertCar("EFG 123", "Small", 0, true, true, 6, true);
        insertCar("ABC 456", "Van", 2000, true, true, 2, false);
        insertCar("CDE 456", "Van", 100, true, false, 1, false);
        insertCar("EFG 456", "Van", 0, true, true, 0, false);
        insertCar("ABC 789", "Minibus", 0, true, true, 0, false);
        insertCar("CDE 789", "Minibus", 0, true, true, 0, false);
        insertCar("EFG 789", "Minibus", 0, true, false, 0, false);

        insertCustomer("19900810-1234", "Arne", "Svensson");
        insertCustomer("19810901-1234", "Anna", "Larsson");
        insertCustomer("19720810-1234", "Berit", "Svensson");
        insertCustomer("19900110-1234", "Ulla-Bella", "Björnesson");
        insertCustomer("19800810-1234", "Sven", "Ström");

        insertLog(LocalDate.now(), LocalTime.now(), "19900810-1234", 1, "test");

    }

    private void insertCar(String registration_plate, String car_type, int mileage, boolean available, boolean clean, int times_rented, boolean service) {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement("INSERT INTO cars(registration_plate,car_type,mileage,available,clean,times_rented,service) VALUES (?,?,?,?,?,?,?) ", new String[]{"id"});
                        ps.setString(1, registration_plate);
                        ps.setString(2, car_type);
                        ps.setInt(3, mileage);
                        ps.setBoolean(4, available);
                        ps.setBoolean(5, clean);
                        ps.setInt(6, times_rented);
                        ps.setBoolean(7, service);
                        return ps;
                    }
                });

    }

    private void insertCustomer(String ssn, String name, String surname) {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement("INSERT INTO customers(ssn, name, surname) VALUES (?,?,?) ", new String[]{"id"});
                        ps.setString(1, ssn);
                        ps.setString(2, name);
                        ps.setString(3, surname);
                        return ps;
                    }
                });
    }

    private void insertLog(LocalDate date, LocalTime time, String customer_ssn, int car_id, String log) {
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement("INSERT INTO events(log_date, log_time, customer_ssn, car_id, log) VALUES (?,?,?,?,?)");
                        ps.setDate(1, Date.valueOf(date));
                        ps.setTime(2, Time.valueOf(time));
                        ps.setString(3, customer_ssn);
                        ps.setInt(4, car_id);
                        ps.setString(5, log);
                        return ps;
                    }
                }
        );
    }
}