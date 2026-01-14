package com.hms.booking.controller.dto;

import com.hms.booking.domain.Booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingDto(
        UUID id,
        UUID userId,
        String username,
        UUID roomId,
        LocalDate startDate,
        LocalDate endDate,
        String status,
        LocalDateTime createdAt
) {

    public BookingDto(Booking booking) {
        this(
                booking.getId(),
                booking.getUser().getId(),
                booking.getUser().getUsername(),
                booking.getRoomId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getStatus().toString(),
                booking.getCreatedAt()
        );
    }
}
