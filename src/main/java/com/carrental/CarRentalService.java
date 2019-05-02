package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class CarRentalService {

    public double calculateCost (ReturnRequest returnRequest, LocalDate pickUpDate, Car car){
        double rentalCost = 100;
        double baseDayRental = 100;
        double kmPrice = 10;
        int numberOfDays = calculateNumberOfDays(pickUpDate, returnRequest.getReturnDate());
        int numberOfKm = calculateNumberOfKm(car.getMileage(), returnRequest.getMileageAtReturn());

        CarType carType = CarType.valueOf(car.getCarType().toUpperCase());

        switch (carType) {
            case SMALL:
                rentalCost = baseDayRental * numberOfDays;
                break;
            case VAN:
                rentalCost = baseDayRental * numberOfDays * 1.2 + kmPrice * numberOfKm;
                break;
            case MINIBUS:
                rentalCost = baseDayRental * numberOfDays * 1.7 + (kmPrice * numberOfKm * 1.5);
                break;
        }
        return rentalCost;
    }

    public int calculateNumberOfKm ( int kmAtPickup, int kmAtReturn){
        return kmAtReturn - kmAtPickup;
    }

    public int calculateNumberOfDays (LocalDate pickupDate, LocalDate returnDate){
        return (int) DAYS.between(pickupDate, returnDate);
    }

}
