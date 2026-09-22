package org.example.shareit.Booking;

import lombok.extern.slf4j.Slf4j;
import org.example.shareit.Booking.Exceptions.AlreadyBookedException;
import org.example.shareit.Booking.Exceptions.NonExistingBookingException;
import org.example.shareit.Booking.Exceptions.WrongOwnerException;
import org.example.shareit.Item.Exceptions.ItemNotExistsException;
import org.example.shareit.Item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class BookingController { // сделать логику бронирвоания

    BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/bookings/{bookingID}")
    public ResponseEntity<?> getBooking(@PathVariable int bookingID,
                                     @RequestHeader int userID) {
        try{
            Booking booking=bookingService.getBooking(bookingID, userID);
            log.info("Found booking with ID " + bookingID);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }
        catch(NonExistingBookingException e){
            log.error("Booking with ID " + bookingID + " not found");
            return new ResponseEntity<>("Booking with ID " + bookingID + " not found", HttpStatus.NOT_FOUND);
        }
        catch(WrongOwnerException e){
            log.error("Booking with ID " + bookingID + " is not owned");//?
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/bookings")
    public ResponseEntity<?> createBooking(@RequestBody Booking booking,
                                 @RequestHeader("X-User-Id") int userID) {
        try{
            Booking createdBooking=bookingService.createBooking(booking, userID);
            log.info("Created Booking with ID " + createdBooking.getBookingID());
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        }
        catch(AlreadyBookedException e){
            log.error("Item is already booked");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @PatchMapping("/bookings/{bookingId}/approved={approved}")
    public ResponseEntity<?> approveBooking(@PathVariable int bookingId,
                                  @PathVariable boolean approved,
                                  @RequestHeader("X-Owner-Id") int ownerId) {
        try{
            bookingService.approveBooking(bookingId, approved, ownerId);
            log.info("Approved Booking with ID " + bookingId);
            return new ResponseEntity(bookingService.getBooking(bookingId, ownerId), HttpStatus.OK);
        }
        catch(NonExistingBookingException e){
            log.error("Booking with ID " + bookingId + " not found");
            return new ResponseEntity<>("Booking with ID " + bookingId + " not found", HttpStatus.NOT_FOUND);
        }
        catch(WrongOwnerException e){
            log.error("User with id " + ownerId + " is not owner");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch(AlreadyBookedException e){
            log.error("Booking with ID " + bookingId + " is already confirmed");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

    }

    @GetMapping("/bookings/owner?state={state}")
    public List<Booking> getOwnerBookings(@RequestParam(defaultValue = "ALL") String state, // тест
                                         @RequestHeader("X-Owner-Id") int ownerID) {
        List<Booking> bookings = new ArrayList<Booking>();
        try
        {
            bookings=bookingService.getOwnerItemsBookings(ownerID,state);
        }
        catch (Exception e)
        {
            log.error("Cannot get owner bookings: "+e.getMessage());
        }
        return bookings;
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings(@RequestParam (defaultValue ="ALL") String state,
                                         @RequestHeader("X-User-Id") int userId) {
        List<Booking> bookings=new ArrayList<>();
        try{
            bookings=bookingService.getUserBookings(userId,state);
            log.info("Found "+bookings.size()+" bookings for user with id "+userId);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        }
        catch(Exception e){
            log.error("Cannot get bookings for user with id "+userId+": "+e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        }
    }


}
