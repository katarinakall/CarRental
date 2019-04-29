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
		jdbcTemplate.execute("CREATE TABLE cars(id SERIAL, registration_plate VARCHAR(255), car_type VARCHAR(255), mileage INT, available BOOLEAN)");

		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(id SERIAL, bday DATE, last_four_digits INT)");

		jdbcTemplate.execute("DROP TABLE rent_cars IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE rent_cars(id SERIAL, customer_ssn VARCHAR(255), car_id INT, pick_up_date DATE, pick_up_time TIME, return_date DATE, return_time TIME, booking_number VARCHAR(255), active BOOLEAN)");

		insertCar("ABC 123", "Small", 0, true);
		insertCar("CDE 123", "Small", 0, true);
		insertCar("EFG 123", "Small", 0, true);
		insertCar("ABC 456", "Van", 0, true);
		insertCar("CDE 456", "Van", 0, true);
		insertCar("EFG 456", "Van", 0, true);
		insertCar("ABC 789", "Minibus", 0, true);
		insertCar("CDE 789", "Minibus", 0, true);
		insertCar("EFG 789", "Minibus", 0, true);
	}

	private int insertCar(String registration_plate, String car_type, int mileage, boolean available){
		KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps =
									connection.prepareStatement("INSERT INTO cars(registration_plate,car_type,mileage,available) VALUES (?,?,?,?) ", new String[]{"id"});
							ps.setString(1, registration_plate);
							ps.setString(2, car_type);
							ps.setInt(3, mileage);
							ps.setBoolean(4, available);
							return ps;
						}
					},
					keyHolder);
			return keyHolder.getKey().intValue();

	}
}
