package com.carrental.domain;

import lombok.Data;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Booking {
    private String customerSSN;
    private int carId;
    private LocalDate pickupDate;
    private LocalTime pickUpTime;
    private String bookingNumber;

    public Booking(String customerSSN, int carId, Date pickupDate, String bookingNumber) {
        this.customerSSN = customerSSN;
        this.carId = carId;
        this.pickupDate = pickupDate.toLocalDate();
        this.bookingNumber = bookingNumber;
    }
}
