package org.example.shareit.Booking.Exceptions;

public class WrongBookingUserException extends RuntimeException {
    public WrongBookingUserException(int userId, int itemId) {
        super("User with id " + userId + " didn't book item with id " + itemId);
    }
}
