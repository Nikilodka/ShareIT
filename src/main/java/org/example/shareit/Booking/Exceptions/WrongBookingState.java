package org.example.shareit.Booking.Exceptions;

public class WrongBookingState extends RuntimeException {
    public WrongBookingState() {
        super("Booking state doesn't exist");
    }
}
