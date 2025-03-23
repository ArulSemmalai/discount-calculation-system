package com.arul.discount.exchange.system.exception;

public class InvalidBillDetailsException extends RuntimeException{

    public InvalidBillDetailsException(String message) {
        super(message);
    }

    public InvalidBillDetailsException(String message, Throwable cause) {
        super(message, cause);
    }
}
