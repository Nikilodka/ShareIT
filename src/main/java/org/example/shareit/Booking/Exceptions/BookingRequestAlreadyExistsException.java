package org.example.shareit.Booking.Exceptions;

public class BookingRequestAlreadyExistsException extends RuntimeException {
    public BookingRequestAlreadyExistsException(int userId,int itemId) {
        super("User with id " + userId + " already create booking for item  with id " + itemId);
    }
}
