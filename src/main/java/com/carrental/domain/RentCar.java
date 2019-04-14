package com.carrental.domain;

import lombok.Data;
import org.joda.time.Days;
import org.joda.time.LocalDate;

@Data

public class RentCar {
    private Customer customer;
    private Car car;
    private LocalDate pickupDate;
    private LocalDate returnDate;
    private int mileageAtReturn;
    private int baseDayRental;
    private int kmPrice;


    public double calculateRentalCost(CarType carType) {
        double rentalCost = 0;
        int numberOfDays = calculateNumberOfDays(pickupDate, returnDate);
        int numberOfKm = calculateNumberOfKm(car.getMileage(), mileageAtReturn);

        switch (carType) {
            case SMALL:
                rentalCost = baseDayRental * numberOfDays;
                break;
            case VAN:
                rentalCost = baseDayRental * numberOfDays * 1.2 + kmPrice * numberOfKm;
                break;
            case MINIBUS:
                rentalCost = baseDayRental * numberOfDays * 1.7 +(kmPrice * numberOfKm * 1.5);
                break;
        }
        return rentalCost;
    }

    public int calculateNumberOfKm(int kmAtPickup, int kmAtReturn){
        return kmAtReturn - kmAtPickup;
    }

    public int calculateNumberOfDays(LocalDate pickupDate, LocalDate returnDate) {
        return Days.daysBetween(pickupDate, returnDate).getDays();
    }

}
