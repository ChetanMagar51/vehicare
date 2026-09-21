package com.vehicare.modules.scheduling.exception;

public class ServiceSlotAlreadyBookedException extends SchedulingException  {

    public ServiceSlotAlreadyBookedException(String message) {
        super(message);
    }
}