package com.carrental.discountStrategy;

import com.carrental.domain.CostVariables;

public class BronzeDiscountStrategy implements DiscountStrategy{
    @Override
    public CostVariables getCostVariables(int numberOfDays, int numberOfKm, int baseDayRental) {
        CostVariables costVariables = new CostVariables();
        costVariables.setBaseDayRental(baseDayRental / 2);
        costVariables.setNumberOfDays(numberOfDays);
        costVariables.setNumberOfKm(numberOfKm);
        return costVariables;
    }
}
