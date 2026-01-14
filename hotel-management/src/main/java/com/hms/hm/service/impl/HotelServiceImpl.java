package com.hms.hm.service.impl;

import com.hms.hm.controller.api.HotelDto;
import com.hms.hm.dao.HotelRepository;
import com.hms.hm.domain.Hotel;
import com.hms.hm.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {
    private final HotelRepository hotelRepository;
    @Transactional
    public HotelDto createHotel(HotelDto dto) {
        Hotel hotel = Hotel.fromDto(dto);

        Hotel saved = hotelRepository.save(hotel);
        log.info("Created hotel: {}", saved.getName());
        return new HotelDto(saved);
    }

    @Transactional(readOnly = true)
    public List<HotelDto> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(HotelDto::new)
                .collect(Collectors.toList());
    }
}