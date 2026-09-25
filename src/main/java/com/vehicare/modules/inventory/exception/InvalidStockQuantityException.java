package com.vehicare.modules.inventory.exception;

public class InvalidStockQuantityException extends InventoryException {

    public InvalidStockQuantityException(String message) {
        super(message);
    }
}