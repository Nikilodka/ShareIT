package org.example.shareit.Booking;

import lombok.extern.slf4j.Slf4j;
import org.example.shareit.Booking.Exceptions.*;
import org.example.shareit.Item.Exceptions.ItemNotExistsException;
import org.example.shareit.Item.Exceptions.WrongOwnerException;
import org.example.shareit.Item.Item;
import org.example.shareit.Item.ItemRepository;
import org.example.shareit.Item.ItemStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BookingService {

    BookingRepository bookingRepository;
    ItemRepository itemRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
    }

    @Cacheable(value="bookings", key="#bookingId")
    public Booking getBooking(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) {
            throw new NonExistingBookingException(bookingId);
        }

        return booking;
    }

    @CachePut(value="bookings", key="#result.bookingID")
    @CacheEvict(cacheNames = {"bookingsByUser","bookingsByOwner"}, allEntries = true)
    public Booking createBooking(Booking booking, int userId) throws BookingRequestAlreadyExistsException {
        if(bookingRepository.existsByItemIdAndUserId(booking.getItemId(),userId)) {
            throw new BookingRequestAlreadyExistsException(userId,booking.getItemId());
        }

        booking.setUserId(userId);
        booking.setStatus(BookingStatus.WAITING);

        return bookingRepository.save(booking);
    }

    @CachePut(value = "bookings", key="#result.bookingID")
    public Booking updateBooking(int bookingId, Booking booking) {
        Booking bookingToUpdate = bookingRepository.findById(bookingId);
        if (bookingToUpdate == null) {
            throw new NonExistingBookingException(bookingId);
        }
        return bookingRepository.save(booking);
    }

    @Cacheable(value="bookingForOwner", key="#ownerId")
    public List<Booking> getOwnerItemsBookings(int ownerId, String state) {
        List<Integer> ownerItemsIds= itemRepository.findItemIdsByOwnerIdAndStatus(ownerId,ItemStatus.BOOKED);
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(ownerItemsIds);

        for (Booking booking : bookings) {
            if (booking.getStatus() != parseState(state)) {
                booking.setStatus(parseState(state));
            }
        }
        return bookings;
    }

    @Cacheable(value="bookingsByUser", key="#userId")
    public List<Booking> getUserBookings(int userId, String state) {
        List<Booking> bookingList=bookingRepository.findAllByUserId(userId);
        if(state.equals("ALL")) {
            return bookingList;
        }
        else
        {
            for (Booking booking : bookingList) {
                log.info("State="+state);
                log.info("Booking state="+booking.getStatus());
                if (booking.getStatus() != parseState(state)) {
                    bookingList.remove(booking);
                }
            }
        }
        return bookingList;
    }

    @CachePut(value="bookings", key="#bookingId")
    @CacheEvict(cacheNames = {"bookingsByUser","bookingsByOwner"}, allEntries = true)
    public void approveBooking(int bookingId,
                               boolean approved,
                               int userId)
            throws NonExistingBookingException,
            WrongOwnerException,
            AlreadyBookedException
    {
        Booking booking = bookingRepository.findById(bookingId);

        if(booking==null) {
            throw new NonExistingBookingException(bookingId);
        }
        if(booking.status==BookingStatus.APPROVED)
        {
            throw new AlreadyBookedException(bookingId);
        }

        Item bookingItem = itemRepository.findById(booking.getItemId());

        if(bookingItem.getOwnerId()!=userId) {
            throw new WrongOwnerException(bookingItem.getId(), userId);
        }

        List<Booking> existingBookings=bookingRepository.findAll();
        for(Booking existingBooking:existingBookings) {
            if(checkIntersectingDates(booking,existingBooking)) {
                throw new IntersectingBookingDatesException(booking.getBookingID(),existingBooking.getBookingID());
            }
        }
        if(approved) {
            booking.setStatus(BookingStatus.APPROVED);
        }
        bookingRepository.save(booking);
    }

    private BookingStatus parseState(String state) {
        switch (state) {
            case "WAITING":
                return BookingStatus.WAITING;
                    
                    case "REJECTED":
                        return BookingStatus.REJECTED;

            case "CURRENT":
                return BookingStatus.CURRENT;

            case "PAST":
                return BookingStatus.PAST;
            case "FUTURE":
                return BookingStatus.APPROVED;//???
            default:
                throw new WrongBookingState();
        }
    }

    private boolean checkIntersectingDates(Booking booking1, Booking booking2)
    {
        if ((booking1.getStartDate().isAfter(booking2.getStartDate()) && booking1.getStartDate().isBefore(booking2.getEndDate()))
                || (booking1.getStartDate().isBefore(booking2.getEndDate()) && booking1.getEndDate().isBefore(booking2.getEndDate()))
                || (booking1.getStartDate().isAfter(booking2.getStartDate()) && booking1.getEndDate().isBefore(booking2.getEndDate()))
                || (booking2.getStartDate().isAfter(booking1.getStartDate()) && booking2.getEndDate().isBefore(booking1.getEndDate())))
        {
            return true;
        }
        return false;
    }
}



