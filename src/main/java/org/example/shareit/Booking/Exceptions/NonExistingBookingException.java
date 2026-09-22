package org.example.shareit.Booking.Exceptions;

public class NonExistingBookingException extends RuntimeException {
    public NonExistingBookingException(int id) {
        super("Booking with id " + id + " does not exist");
    }
}
