package com.carrental;

import com.carrental.domain.CarType;
import lombok.Data;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@Data
public class RentalRequest {
    private LocalDate dateOfBirth;
    private int lastFourDigits;
    private CarType carType;
    private LocalDate pickUpDate;
    private LocalTime pickUpTime;
    private int mileageAtPickup;
}
