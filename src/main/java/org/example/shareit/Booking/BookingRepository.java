package org.example.shareit.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByUserId(int userId);
    List<Booking> findAllByItemId(int itemId);
    Booking save(Booking booking);
    Booking findById(int id);
    void deleteById(int id);
    List<Booking> findAllByStatus(String status);
    List<Booking> findAllByItemIdIn(List<Integer> itemIds);
    boolean existsByItemIdAndUserId(int itemId,int userId);
}
