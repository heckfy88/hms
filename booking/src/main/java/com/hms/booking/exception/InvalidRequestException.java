package com.hms.booking.exception;

public class InvalidRequestException extends DomainException {
    public InvalidRequestException(String message) {
        super(message);
    }
}