package com.hms.booking.exception;


public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) {
        super(message);
    }
}