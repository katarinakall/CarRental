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
		jdbcTemplate.execute("CREATE TABLE cars(id SERIAL, registration_plate VARCHAR(255), car_type VARCHAR(255), mileage INT)");

		insertCar("ABC 123", "Small", 0);
		insertCar("CDE 123", "Small", 0);
		insertCar("EFG 123", "Small", 0);
		insertCar("ABC 123", "Van", 0);
		insertCar("CDE 123", "Van", 0);
		insertCar("EFG 123", "Van", 0);
		insertCar("ABC 123", "Minibus", 0);
		insertCar("CDE 123", "Minibus", 0);
		insertCar("EFG 123", "Minibus", 0);
	}

	private int insertCar(String registration_plate, String car_type, int mileage){
		KeyHolder keyHolder = new GeneratedKeyHolder();
			jdbcTemplate.update(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
							PreparedStatement ps =
									connection.prepareStatement("INSERT INTO cars(registration_plate,car_type,mileage) VALUES (?,?,?) ", new String[]{"id"});
							ps.setString(1, registration_plate);
							ps.setString(2, car_type);
							ps.setInt(3, mileage);
							return ps;
						}
					},
					keyHolder);
			return keyHolder.getKey().intValue();

	}
}
