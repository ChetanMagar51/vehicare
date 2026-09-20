package com.vehicare.modules.scheduling.exception;

public class ServiceSlotAlreadyBookedException extends RuntimeException {

    public ServiceSlotAlreadyBookedException(String message) {
        super(message);
    }
}