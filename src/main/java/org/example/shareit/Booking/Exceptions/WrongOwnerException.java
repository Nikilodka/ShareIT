package org.example.shareit.Booking.Exceptions;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(int bookingId, int userId) {
        super("User with id " + userId + " is not owner of booking with id " + bookingId);
    }
}
