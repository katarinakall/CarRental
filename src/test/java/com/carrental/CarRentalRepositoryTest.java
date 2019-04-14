package com.carrental;


import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.repository.CarRentalRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CarRentalRepositoryTest {
    private CarRentalRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testGetCars() throws SQLException {
//        List<Car> cars = new ArrayList<>();
//
//        cars = repository.getAvailableCars(CarType.SMALL);
//
//        System.out.println(cars.toString());
//    }
}
