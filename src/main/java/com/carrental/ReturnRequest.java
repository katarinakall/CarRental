package com.carrental;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReturnRequest {
    private String bookingNumber;
    private LocalDate returnDate;
    private LocalTime returnTime;
    private int mileageAtReturn;
}
