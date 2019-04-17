package com.carrental;

import com.carrental.domain.RentCar;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CarTest {
   private RentCar car = new RentCar();

   @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCalculateNumberOfDays(){
        LocalDate pickUpDate = new LocalDate(2019,3,1);
        LocalDate returnDate = new LocalDate(2019,3,10);

        int nrOfDays = car.calculateNumberOfDays(pickUpDate, returnDate);

        assertEquals(nrOfDays, 9);
    }
}
