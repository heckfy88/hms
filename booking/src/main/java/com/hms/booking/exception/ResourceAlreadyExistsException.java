package com.hms.booking.exception;

public class ResourceAlreadyExistsException extends DomainException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}