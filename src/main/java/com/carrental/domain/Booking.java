package com.carrental.domain;

import lombok.Data;
import org.joda.time.LocalDate;

import java.time.LocalTime;

@Data
public class Booking {
    private String customerSSN;
    private int carId;
    private LocalDate pickupDate;
    private LocalTime pickUpTime;
    private String bookingNumber;

    public Booking(String customerSSN, int carId, String bookingNumber) {
        this.customerSSN = customerSSN;
        this.carId = carId;
        this.bookingNumber = bookingNumber;
    }
}
