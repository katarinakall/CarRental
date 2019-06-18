package com.carrental.discountStrategy;

import com.carrental.domain.CostVariables;

public class SilverDiscountStrategy implements DiscountStrategy{

    @Override
    public CostVariables getCostVariables(int numberOfDays, int numberOfKm, int baseDayRental) {
        CostVariables costVariables = new CostVariables();
        costVariables.setBaseDayRental(baseDayRental / 2);
        if (numberOfDays == 3) {
            numberOfDays = numberOfDays - 1;
            costVariables.setNumberOfDays(numberOfDays);
        }
        if (numberOfDays > 3) {
            numberOfDays = numberOfDays - 2;
            costVariables.setNumberOfDays(numberOfDays);
        } else {
            costVariables.setNumberOfDays(numberOfDays);
        }
        costVariables.setNumberOfKm(numberOfKm);

        return costVariables;
    }
}
