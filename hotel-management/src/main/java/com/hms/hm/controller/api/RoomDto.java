package com.hms.hm.controller.api;

import com.hms.hm.domain.Room;

import java.util.UUID;

public record RoomDto(
        UUID id,
        UUID hotelId,
        String number,
        Boolean available,
        Integer timesBooked
) {

    public RoomDto(Room room) {
        this(
                room.getId(),
                room.getHotel().getId(),
                room.getNumber(),
                room.getAvailable(),
                room.getTimesBooked()
        );
    }
}
