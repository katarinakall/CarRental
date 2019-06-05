package com.carrental.domain;

public enum CarType {
    SMALL("Small"),
    VAN("Van"),
    MINIBUS("Minibus");

    private final String displayName;

    CarType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CarType fromString(String parameterName) {
        if (parameterName != null) {
            for (CarType carType : CarType.values()) {
                if (parameterName.equalsIgnoreCase(carType.displayName)) {
                    return carType;
                }
            }
        }
        return null;
    }
}


