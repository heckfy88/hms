package com.hms.booking.controller;

import com.hms.booking.controller.dto.BookingDto;
import com.hms.booking.controller.dto.CreateBookingDto;
import com.hms.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping("/booking")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody CreateBookingDto request) {
        return bookingService.create(request);
    }

    @GetMapping("/bookings")
    public List<BookingDto> getUserBookings() {
        return bookingService.findAll();
    }

    @GetMapping("/booking/{id}")
    public BookingDto getBooking(@PathVariable UUID id) {
        return bookingService.findById(id);
    }

    @DeleteMapping("/booking/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(@PathVariable UUID id) {
        bookingService.cancel(id);
    }
}