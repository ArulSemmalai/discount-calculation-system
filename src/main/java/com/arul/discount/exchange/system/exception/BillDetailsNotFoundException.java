package com.arul.discount.exchange.system.exception;

public class BillDetailsNotFoundException extends RuntimeException{

    public BillDetailsNotFoundException(String message) {
        super(message);
    }

    public BillDetailsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
