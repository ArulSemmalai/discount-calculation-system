package com.arul.discount.exchange.system.utils;

public enum UserType {
    EMPLOYEE,
    AFFILIATE,
    CUSTOMER;

    public static UserType fromString(String userType){
        try {
            return UserType.valueOf(userType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

}
