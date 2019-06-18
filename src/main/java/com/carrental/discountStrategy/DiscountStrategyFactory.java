package com.carrental.discountStrategy;

public class DiscountStrategyFactory {
    private DiscountStrategy discountStrategy;


    public DiscountStrategy fromMemberStatus(String membersStatus){
        switch(membersStatus){
            case(""):
                discountStrategy = new NoDiscountStrategy();
                break;
            case "Bronze":
                discountStrategy = new BronzeDiscountStrategy();
                break;
            case "Silver":
                discountStrategy = new SilverDiscountStrategy();
                break;
            case "Gold":
                discountStrategy = new GoldDiscountStrategy();
                break;
        }
        return discountStrategy;
    }
}
