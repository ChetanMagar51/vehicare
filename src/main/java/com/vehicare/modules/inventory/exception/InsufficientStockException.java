package com.vehicare.modules.inventory.exception;

public class InsufficientStockException extends InventoryException {

    public InsufficientStockException(String message) {
        super(message);
    }
}