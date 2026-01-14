package com.hms.booking.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingDto(
        UUID roomId,
        Boolean autoSelect,
        LocalDate startDate,
        LocalDate endDate
) {
}
