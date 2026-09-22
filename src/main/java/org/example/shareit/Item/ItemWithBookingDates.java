package org.example.shareit.Item;

import java.time.LocalDate;
import java.util.Map;

public interface ItemWithBookingDates {
    String getName();
    String getDescription();
    String getStatus();
    int getShareCount();
    Map<LocalDate, LocalDate> getLastBookingDate();
    Map<LocalDate, LocalDate> getNearestBookingDate();
}
