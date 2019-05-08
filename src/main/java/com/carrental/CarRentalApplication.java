package com.carrental;

import com.carrental.domain.CarType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SpringBootApplication
public class CarRentalApplication implements CommandLineRunner {

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final Logger log = LoggerFactory.getLogger(CarRentalApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		log.info("Creating tables");
		jdbcTemplate.execute("DROP TABLE cars IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE cars(id SERIAL, registration_plate VARCHAR(255), car_type VARCHAR(255), mileage INT, available BOOLEAN, clean BOOLEAN, times_rented INT)");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(id SERIAL, ssn VARCHAR(255), name VARCHAR(255), surname VARCHAR(255))");

		jdbcTemplate.execute("DROP TABLE rent_cars IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE rent_cars(id SERIAL, customer_ssn VARCHAR(255), car_id INT, pick_up_date DATE, pick_up_time TIME, return_date DATE, return_time TIME, booking_number VARCHAR(255), active BOOLEAN)");

		insertCar("ABC 123", "Small", 300, true, true, 3);
		insertCar("CDE 123", "Small", 100, true, false, 1);
		insertCar("EFG 123", "Small", 0, true, true, 6);
		insertCar("ABC 456", "Van", 2000, true, true, 2);
		insertCar("CDE 456", "Van", 100, true, false, 1);
		insertCar("EFG 456", "Van", 0, true, true, 0);
		insertCar("ABC 789", "Minibus", 0, true,true, 0);
		insertCar("CDE 789", "Minibus", 0, true, true, 0);
		insertCar("EFG 789", "Minibus", 0, true, false, 0);

		insertCustomer("19900810-1234", "Arne", "Svensson");
		insertCustomer("19810901-1234", "Anna", "Larsson");
		insertCustomer("19720810-1234", "Berit", "Svensson");
		insertCustomer("19900110-1234", "Ulla-Bella", "Björnesson");
		insertCustomer("19800810-1234", "Sven", "Ström");

	}

	private void insertCar(String registration_plate, String car_type, int mileage, boolean available, boolean clean, int times_rented){
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps =
									connection.prepareStatement("INSERT INTO cars(registration_plate,car_type,mileage,available,clean,times_rented) VALUES (?,?,?,?,?,?) ", new String[]{"id"});
							ps.setString(1, registration_plate);
							ps.setString(2, car_type);
							ps.setInt(3, mileage);
							ps.setBoolean(4, available);
							ps.setBoolean(5, clean);
							ps.setInt(6, times_rented);
							return ps;
						}
					});

	}

	private void insertCustomer(String ssn, String name, String surname){
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
}
