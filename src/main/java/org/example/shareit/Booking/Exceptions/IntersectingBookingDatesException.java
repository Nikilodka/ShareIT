package org.example.shareit.Booking.Exceptions;

public class IntersectingBookingDatesException extends RuntimeException {
    public IntersectingBookingDatesException(int bookingId1, int bookingId2) {
        super("Booking dates intersect for booking with id: "+bookingId1+" and id: "+bookingId2);
    }
}
