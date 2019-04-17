package com.carrental.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Customer {
    private LocalDate dateOfBirth;
    private int lastFourDigits;
}
