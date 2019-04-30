package com.carrental.domain;

import lombok.Data;
import org.joda.time.Days;
import org.joda.time.LocalDate;

@Data

public class RentCar {
    private Customer customer;
    private Car car;
    private LocalDate pickupDate;
    private String bookingNumber;
    private LocalDate returnDate;
    private int mileageAtReturn;
    private int baseDayRental;
    private int kmPrice;


}
