package com.carrental;

import com.carrental.domain.Car;
import com.carrental.domain.CarType;
import com.carrental.repository.CarRentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class CarRentalService {

    @Autowired
    private CarRentalRepository repository;

    public BigDecimal calculateCost (ReturnRequest returnRequest, LocalDate pickUpDate, Car car){
        BigDecimal rentalCost = new BigDecimal(0);
        BigDecimal baseDayRental = new BigDecimal(100);
        BigDecimal kmPrice = new BigDecimal(10);
        int numberOfDays = calculateNumberOfDays(pickUpDate, returnRequest.getReturnDate());
        int numberOfKm = calculateNumberOfKm(car.getMileage(), returnRequest.getMileageAtReturn());

        BigDecimal days = new BigDecimal(numberOfDays);
        BigDecimal km = new BigDecimal(numberOfKm);

        BigDecimal van = new BigDecimal(1.2);
        BigDecimal minibus = new BigDecimal(1.7);
        BigDecimal minibusKm = new BigDecimal(1.5);

        CarType carType = CarType.valueOf(car.getCarType().toUpperCase());

        switch (carType) {
            case SMALL:
                rentalCost = baseDayRental.multiply(days);
                break;
            case VAN:
                BigDecimal dayCostV = baseDayRental.multiply(days).multiply(van);
                BigDecimal kmCostV = kmPrice.multiply(km);
                 rentalCost = dayCostV.add(kmCostV);
                break;
            case MINIBUS:
                BigDecimal dayCostMB = baseDayRental.multiply(days).multiply(minibus);
                BigDecimal kmCostMB = kmPrice.multiply(km).multiply(minibusKm);
                rentalCost = dayCostMB.add(kmCostMB);

                break;
        }
        return rentalCost.setScale(2, RoundingMode.UP);
    }

    public int calculateNumberOfKm ( int kmAtPickup, int kmAtReturn){
        return kmAtReturn - kmAtPickup;
    }

    public int calculateNumberOfDays (LocalDate pickupDate, LocalDate returnDate){
        return (int) DAYS.between(pickupDate, returnDate);
    }

    public void updateReturnedCar(int carId, int mileageAtReturn){
        repository.toggleCarAvailability(carId, true);
        repository.updateCarMileage(carId, mileageAtReturn);
        repository.updateTimesRented(carId);
        repository.toggleCarCleaning(carId, false);
    }

}
