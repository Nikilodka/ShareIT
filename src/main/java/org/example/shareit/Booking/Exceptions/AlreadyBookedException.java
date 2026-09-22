package org.example.shareit.Booking.Exceptions;

public class AlreadyBookedException extends RuntimeException {
    public AlreadyBookedException(int itemId) {
        super("Item with id " + itemId + " is already booked");
    }
}
