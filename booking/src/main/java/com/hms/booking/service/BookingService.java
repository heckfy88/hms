package com.hms.booking.service;

import com.hms.booking.controller.dto.BookingDto;
import com.hms.booking.controller.dto.CreateBookingDto;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    BookingDto create(CreateBookingDto bookingDto);
    List<BookingDto> findAll();
    BookingDto findById(UUID id);
    void cancel(UUID id);
}
