package com.shopsphere.inventory.exception;

public class ReservationNotFoundException extends InventoryException {
    public ReservationNotFoundException(String reservationId) {
        super("Reservation not found: " + reservationId);
    }
}
