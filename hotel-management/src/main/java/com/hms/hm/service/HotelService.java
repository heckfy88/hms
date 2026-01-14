package com.hms.hm.service;

import com.hms.hm.controller.api.HotelDto;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    List<HotelDto> getAllHotels();
}