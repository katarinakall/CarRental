package com.carrental.discountStrategy;

import com.carrental.domain.CostVariables;

public interface DiscountStrategy {
   CostVariables getCostVariables(int numberOfDays, int numberOfKm, int baseDayRental);

}
