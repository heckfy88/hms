package com.hms.booking.service;

import com.hms.booking.domain.Booking;

import java.util.UUID;

public interface HotelService {
    void confirm(Booking booking);
    UUID autoSelect();
    void release(Booking booking);
}
