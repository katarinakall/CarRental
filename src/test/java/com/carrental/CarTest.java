package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.domain.RentCar;
import com.carrental.repository.CarRentalRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CarTest {
    @Autowired
    private CarRentalRepositoryImpl repository;

   @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateCost(){
       LocalDate localDate = LocalDate.of(2019,5,1);
       ReturnRequest returnRequest = new ReturnRequest();
       returnRequest.setReturnDate(localDate);
       returnRequest.setMileageAtReturn(12);
       returnRequest.setBookingNumber("201904301111");
       LocalDate localDate1 = LocalDate.of(2019, 4,30);
       int mileage = 0;
        CarType carType = CarType.SMALL;
     //   Car car = repository.getCar(1);
       // System.out.println(car.toString());
    //   double cost = repository.calculateCost(returnRequest, localDate1, mileage, carType);

     //   System.out.println(cost);

    }
}
