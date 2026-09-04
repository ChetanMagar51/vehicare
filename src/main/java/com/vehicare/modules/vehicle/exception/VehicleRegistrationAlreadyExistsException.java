package com.vehicare.modules.vehicle.exception;

public class VehicleRegistrationAlreadyExistsException extends RuntimeException {

    public VehicleRegistrationAlreadyExistsException(String message) {
        super(message);
    }
}