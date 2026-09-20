package com.vehicare.modules.scheduling.exception;

public class ServiceSlotNotFoundException extends RuntimeException {

    public ServiceSlotNotFoundException(String message) {
        super(message);
    }
}