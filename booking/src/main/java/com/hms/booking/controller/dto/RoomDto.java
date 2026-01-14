package com.hms.booking.controller.dto;

import java.util.UUID;

public record RoomDto(
        UUID id,
        UUID hotelId,
        String number,
        Boolean available,
        Integer timesBooked
) {
}
