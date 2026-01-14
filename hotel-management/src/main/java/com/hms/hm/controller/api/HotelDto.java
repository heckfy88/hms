package com.hms.hm.controller.api;

import com.hms.hm.domain.Hotel;

import java.util.UUID;

public record HotelDto(
        UUID id,
        String name,
        String address
) {
    public HotelDto(Hotel hotel) {
        this(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress()
        );
    }
}
